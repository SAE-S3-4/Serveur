package fr.univ_amu.iut;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.UUID;

/**
 * Class containing the server used to simulate a linux console
 */
public class ServerTerminal {

    private static final String[] protocols = new String[]{"TLSv1.3"};
    private static final String[] cipher_suites = new String[]{"TLS_AES_128_GCM_SHA256"};
    private int port;
    private final int MAX_SIMULTANEUS_CONNEXIONS;
    private String dockerId;

    private int nbOfCurrentConnectedClients;

    /**
     * Constructor for the server hosting the linux console used to play the games
     *
     * @param port
     * @param MAX_SIMULTANEUS_CONNEXIONS
     */
    public ServerTerminal(int port, int MAX_SIMULTANEUS_CONNEXIONS) {
        this.port = port;
        this.MAX_SIMULTANEUS_CONNEXIONS = MAX_SIMULTANEUS_CONNEXIONS;
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

            if(nbOfCurrentConnectedClients<MAX_SIMULTANEUS_CONNEXIONS){

                nbOfCurrentConnectedClients += 1;

                dockerId = UUID.randomUUID().toString();

                //Creation of a docker container per client
                Runtime.getRuntime().exec("docker run -it -d --rm --name " + dockerId + " terminal");

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                Thread receive = new Thread(new Runnable() {
                    String msg;

                    @Override
                    public void run() {
                        try {
                            while ((msg = in.readLine()) != null) {
                                try {
                                    //Execution of the command sent in the docker
                                    Process process = Runtime.getRuntime().exec("docker exec " + dockerId + " " + msg);

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    String line = "";

                                    while ((line = reader.readLine()) != null) {
                                        if(line.contains("OCI runtime exec failed: exec failed: unable to start container process: exec:")){
                                            throw new IllegalArgumentException("Command does not exists");
                                        }
                                        out.write(line);
                                        out.newLine();
                                        out.flush();
                                        System.out.println(line);
                                    }
                                } catch (IOException | NullPointerException | IllegalArgumentException e) {
                                    out.write("Command does not exist");
                                    out.newLine();
                                    out.flush();
                                }
                            }
                            //Stop client docker
                            Runtime.getRuntime().exec("docker stop " + dockerId);
                            //Exit if the user disconnects
                            System.out.println("Client disconnected");
                            //Close the flux if the user disconnects
                            out.close();
                            client.close();

                            nbOfCurrentConnectedClients -= 1;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                receive.start();

            }else {
                System.out.println("The maximal capacity supported by the server has been reached");
            }
        }
        // Unreachable variable -> could put a failsafe in the server like : if after 1h of maxclient connected then break
        //server.close();
    }

    public static void main(String[] args) {

        try {
            ServerTerminal server = new ServerTerminal(10013,10000);
            server.launch();

        } catch (IOException e) {
            System.out.println("Server couldn't be executed");
            throw new RuntimeException(e);
        }

    }
}
