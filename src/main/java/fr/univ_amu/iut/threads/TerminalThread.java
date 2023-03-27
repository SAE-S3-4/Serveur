package fr.univ_amu.iut.threads;

import fr.univ_amu.iut.Server;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalThread extends Thread {
    private BufferedReader in ;
    private BufferedWriter out;
    private String dockerId;
    private SSLSocket client;

    /**
     * The constructor of the thread used to execute the commands for the Terminal Pane
     *
     * @param in
     * @param out
     * @param dockerId
     * @param client
     */
    public TerminalThread(BufferedReader in , BufferedWriter out, String dockerId, SSLSocket client){
        this.in = in;
        this.out = out;
        this.dockerId = dockerId;
        this.client = client;
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                try {
                    //Execution of the command sent in the docker
                    Process process = Runtime.getRuntime().exec("docker exec " + dockerId + " " + msg);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        if (line.contains("OCI runtime exec failed: exec failed: unable to start container process: exec:")) {
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

            Server.nbOfCurrentConnectedClients -= 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
