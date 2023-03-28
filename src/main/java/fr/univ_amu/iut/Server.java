package fr.univ_amu.iut;

import fr.univ_amu.iut.threads.ChatThread;
import fr.univ_amu.iut.threads.TerminalThread;

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
    private final int MAX_SIMULTANEOUS_CONNEXIONS;
    public static int nbOfCurrentConnectedClients;
    private ArrayList<ChatThread> chatClientThread;
    private boolean isAiActive;

    /**
     * Constructor for the server hosting the linux console used to play the games
     *
     * @param port
     * @param MAX_SIMULTANEOUS_CONNEXIONS
     * @param isAiActive
     */
    public Server(int port, int MAX_SIMULTANEOUS_CONNEXIONS,boolean isAiActive) {
        this.port = port;
        this.MAX_SIMULTANEOUS_CONNEXIONS = MAX_SIMULTANEOUS_CONNEXIONS;
        this.chatClientThread = new ArrayList<>();
        this.isAiActive = isAiActive;
    }

    /**
     * Method used to launch the server
     *
     * @throws IOException
     */
    public void launch() throws IOException {

        System.setProperty("javax.net.ssl.keyStore","myKeyStore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","password");
        System.setProperty("javax.net.debug","all");

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
            if(nbOfCurrentConnectedClients<MAX_SIMULTANEOUS_CONNEXIONS){

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
                    ChatThread clientChatThread = new ChatThread(in,out,client,in.readLine()+" : ",isAiActive,this);
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

    /**
     * Method used to broadcast a message to all clients connected to the chat, except the sender
     *
     * @param message
     * @param sender
     */
    public synchronized void broadcast(String message, ChatThread sender) {
        for (int i = 0; i < chatClientThread.size(); i++) {
            ChatThread clientThread = chatClientThread.get(i);
            if (clientThread != sender) {
                clientThread.sendMessage(message);
            }
        }
    }

    /**
     * Method used to remove a client upon disconnection
     *
     * @param clientThread
     */
    public synchronized void removeClient(ChatThread clientThread) {
        chatClientThread.remove(clientThread);
    }

}