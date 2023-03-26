package fr.univ_amu.iut;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class containing the server used to simulate a linux console
 */
public class Server {

    private static final String[] protocols = new String[]{"TLSv1.3"};
    private static final String[] cipher_suites = new String[]{"TLS_AES_128_GCM_SHA256"};
    private int port;
    private final int MAX_SIMULTANEUS_CONNEXIONS;
    public static int nbOfCurrentConnectedClients;
    private ArrayList<ChatThread> chatClientThread;

    /**
     * Constructor for the server hosting the linux console used to play the games
     *
     * @param port
     * @param MAX_SIMULTANEUS_CONNEXIONS
     */
    public Server(int port, int MAX_SIMULTANEUS_CONNEXIONS) {
        this.port = port;
        this.MAX_SIMULTANEUS_CONNEXIONS = MAX_SIMULTANEUS_CONNEXIONS;
        this.chatClientThread = new ArrayList<>();
    }

    /**
     * Method used to launch the server
     *
     * @throws IOException
     */
    public void launch() throws IOException {

        System.setProperty("javax.net.ssl.keyStore","myKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","password");
        //System.setProperty("javax.net.debug","all");

        // Create SSL server socket factory
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(port);

        server.setEnabledProtocols(protocols);
        server.setEnabledCipherSuites(cipher_suites);

        System.out.println("Bash server launched on port : " + port);

        nbOfCurrentConnectedClients = 0;

        while (true) {
            SSLSocket client = (SSLSocket) server.accept();
            String flag;
            if(nbOfCurrentConnectedClients<MAX_SIMULTANEUS_CONNEXIONS){

                nbOfCurrentConnectedClients += 1;
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                flag = in.readLine();
                if(flag.equals("terminal")) {

                    String dockerId = UUID.randomUUID().toString();

                    //Creation of a docker container per client
                    Runtime.getRuntime().exec("docker run -it -d --rm --name " + dockerId + " terminal");
                    //Thread
                    TerminalThread terminalThread = new TerminalThread(in,out,dockerId,client);
                    terminalThread.start();

                }else if(flag.equals("chat")) {
                    ChatThread clientChatThread = new ChatThread(in,out,client,this);
                    chatClientThread.add(clientChatThread);
                    clientChatThread.start();
                }
            }else {
                System.out.println("The maximal capacity supported by the server has been reached");
            }
        }
        // Unreachable variable -> could put a failsafe in the server like : if after 1h of maxclient connected then break
        //server.close();
    }

    // Broadcast a message to all clients
    public synchronized void broadcast(String message, ChatThread sender) {
        for (int i = 0; i < chatClientThread.size(); i++) {
            ChatThread clientThread = chatClientThread.get(i);
            if (clientThread != sender) {
                clientThread.sendMessage(message);
            }
        }
    }

    // Remove a client thread from the ArrayList
    public synchronized void removeClient(ChatThread clientThread) {
        chatClientThread.remove(clientThread);
    }

    public static void main(String[] args) {

        try {
            Server server = new Server(10013,10000);
            server.launch();

        } catch (IOException e) {
            System.out.println("Server couldn't be executed");
            throw new RuntimeException(e);
        }

    }
}