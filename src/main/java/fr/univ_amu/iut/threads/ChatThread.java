package fr.univ_amu.iut.threads;

import fr.univ_amu.iut.Server;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class ChatThread extends Thread {
    private BufferedReader in ;
    private BufferedWriter out;
    private SSLSocket client;
    private Server serverTerminal;
    private boolean isAIActive;

    /**
     * The constructor of the Thread managing the chat
     *
     * @param in
     * @param out
     * @param client
     * @param isAIActive
     * @param serverTerminal
     */
    public ChatThread(BufferedReader in, BufferedWriter out, SSLSocket client,boolean isAIActive, Server serverTerminal) {
        this.in = in;
        this.out = out;
        this.client = client;
        this.isAIActive = isAIActive;
        this.serverTerminal = serverTerminal;
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                if(isAIActive) {
                    addAiToConversation(msg);
                }
                serverTerminal.broadcast(msg,this);
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

    /**
     * The method used to integrate the AI and make it interact with the conversation
     *
     * @param msg
     */
    public void addAiToConversation(String msg){
        String entireMessage="";
        try {
            //Use the bash script to send the message to the AI
            ProcessBuilder pb = new ProcessBuilder("./talk.sh","\""+msg+"\"");

            pb.directory(new File("/home/opc/llama.cpp"));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            //Read the AI response
            String line = "";
            while ((line = reader.readLine()) != null) {
                entireMessage+=line;
            }
            System.out.println(entireMessage);

        } catch (IOException e) {
            System.out.println("Error while executing the process");
            //throw new RuntimeException(e);
        }
        serverTerminal.broadcast(entireMessage,this);
        sendMessage(entireMessage);
    }

    /**
     * Method used to send the client a message
     *
     * @param message
     */
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
