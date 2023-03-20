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
    private int nbClients;
    private String dockerId;

    /**
     * Constructor for the server hosting the linux console used to play the games
     *
     * @param port
     * @param nbClients
     */
    public ServerTerminal(int port, int nbClients) {
        this.port = port;
        this.nbClients = nbClients;
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

        for (int i = 1; i <= nbClients; i++) {
            SSLSocket client = (SSLSocket)server.accept();

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
                                    out.write(line);
                                    out.newLine();
                                    out.flush();
                                    System.out.println(line);
                                }
                            }catch (IOException | NullPointerException | IllegalArgumentException e) {
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
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            receive.start();
        }
        server.close();
    }

    public static void main(String[] args) throws IOException {
        ServerTerminal server = new ServerTerminal(10013,10000);
        server.launch();
    }
}
