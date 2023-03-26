package fr.univ_amu.iut;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
                    //Execution of the command sent in the docker
                    Process process = Runtime.getRuntime().exec("/llama.cpp/./main -m /llama.cpp/./models/7B/ggml-model-q4_0.bin -p \""+msg+"\" -f /llama.cpp/prompts/message.txt");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        //**
                        //serverTerminal.broadcast(line,this);
                        //out.write(line);
                        //out.newLine();
                        //out.flush();
                        entireMessage+=line;
                        System.out.println(line);
                    }

                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }

                int start = entireMessage.indexOf("End of prompt.") + "End of prompt.".length();
                int end = entireMessage.indexOf("[end of text]");

                if (start >= 0 && end >= 0) {
                    String response = entireMessage.substring(start, end).trim();
                    serverTerminal.broadcast(response,this);
                }
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
