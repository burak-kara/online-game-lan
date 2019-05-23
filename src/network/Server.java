package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    private final int PORT = 34001;
    private ServerSocket serverSocket;
    private Socket client;
    private ArrayList<String> users;
    private ArrayList<PrintWriter> outputStreams;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        users = new ArrayList<>();
        outputStreams = new ArrayList<>();
        int count = 0;
        try {
            while (true) {
                client = serverSocket.accept();

                PrintWriter writer = new PrintWriter(client.getOutputStream());
                outputStreams.add(writer);

                Thread clientThread = new Thread(new ClientHandler(client, count));
                clientThread.start();
                count++;
                System.out.println("got a connection");
            }
        } catch (Exception e) {
            System.out.println("fail to make connection");
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                System.out.println("Could not close connection " + e);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private int count;

        private ClientHandler(Socket clientSocket, int count) {
            this.clientSocket = clientSocket;
            this.count = count;
            try {
                InputStreamReader isReader = new InputStreamReader(this.clientSocket.getInputStream());
                reader = new BufferedReader(isReader);
                users.add("" + count);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message:" + message);
                    sendBothUsers(message);
                }
            } catch (Exception e) {
                System.out.println("No message came");
            }

        }

        private void sendBothUsers(String message) {
            for (int i = 0; i < outputStreams.size(); i++) {
                PrintWriter stream = outputStreams.get(i);
                try {
                    if (!users.get(i).equals("" + count)) {
                        stream.println(message);
                        System.out.println("Sending message " + message);
                        stream.flush();
                    }
                } catch (Exception e) {
                    System.out.println("Cannot send move from server");
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread server = new Thread(new Server());
        server.start();
    }

}
