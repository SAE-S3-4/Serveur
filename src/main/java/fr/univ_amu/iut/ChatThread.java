package fr.univ_amu.iut;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class ChatThread extends Thread {
    private BufferedReader in ;
    private BufferedWriter out;
    private SSLSocket client;
    private Server serverTerminal;

    public ChatThread(BufferedReader in, BufferedWriter out, SSLSocket client, Server serverTerminal) {
        this.in = in;
        this.out = out;
        this.client = client;
        this.serverTerminal = serverTerminal;
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                String entireMessage="";
                try {
                    ProcessBuilder pb = new ProcessBuilder("./talk.sh","\""+msg+"\"");

                    pb.directory(new File("/home/opc/llama.cpp"));
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        entireMessage+=line;
                    }
                    System.out.println(entireMessage);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                serverTerminal.broadcast(entireMessage,this);
                System.out.println("message sent");
            }
            //Exit if the user disconnects
            System.out.println("Client disconnected");
            //Close the flux if the user disconnects
            out.close();
            client.close();
            serverTerminal.removeClient(this);

            Server.nbOfCurrentConnectedClients -= 1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
