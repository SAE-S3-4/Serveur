package fr.univ_amu.iut;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int port = 10013;
        boolean aiEnabled = true;
        int maxSimultaneousConnections = 1000;

        // check if command line arguments were passed
        if (args.length > 0) {
            // loop through the arguments and check for each parameter
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-p")) {
                    // if port parameter is found, get the next argument as the port value
                    if (i + 1 < args.length) {
                        port = Integer.parseInt(args[i + 1]);
                        i++; // skip the next argument, since we already used it
                    }
                } else if (args[i].equals("--help")) {
                    // if help parameter is found, print usage information and exit
                    System.out.println("Usage: server.jar [-p <port>] [--help] [-ai <true/false>] [-maxConns <Max Simultaneous Connections>]\nBy default the port is set to 10013, the usage of the AI to true and the ax Simultaneous Connections to 1000");
                    System.exit(0);
                } else if (args[i].equals("-ai")) {
                    // if ai parameter is found, get the next argument as the ai enabled value
                    if (i + 1 < args.length) {
                        aiEnabled = Boolean.parseBoolean(args[i + 1]);
                        i++; // skip the next argument, since we already used it
                    }
                }else if (args[i].equals("-maxConns")) {
                    // if maxConns parameter is found, get the next argument as the maxConnections value
                    if (i + 1 < args.length) {
                        maxSimultaneousConnections = Integer.parseInt(args[i + 1]);
                        i++; // skip the next argument, since we already used it
                    }
                }
            }
        }

        try {
            Server server = new Server(port,maxSimultaneousConnections,aiEnabled);
            server.launch();

        } catch (IOException e) {
            System.out.println("Server couldn't be executed");
            throw new RuntimeException(e);
        }
    }
}

