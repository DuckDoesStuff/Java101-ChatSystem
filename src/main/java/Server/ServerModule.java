package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ClientHandler {
    Socket clientSocket;
    String host;
    int port;
    BufferedReader in;
    PrintWriter out;
    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        host = clientSocket.getInetAddress().getHostAddress();
        port = clientSocket.getPort();
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error creating input stream");
            throw new RuntimeException(e);
        }
    }
    public void run() {
        System.out.println("Client connected: " + host + ":" + port);
        receiveMessage();
        sendMessage("Hello, client!");
    }

    public void receiveMessage() {
        String clientMessage;
        try {
            System.out.println("Waiting for message from client...");
            while(!in.ready()) {}
            System.out.println("Message received from client");

            clientMessage = in.readLine();
            System.out.println("Client " + host + ":" + port + " says: " + clientMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending message: " + message + " to client " + host + ":" + port);
        out.println(message);
    }
}
public class ServerModule {
    ServerSocket socket;
    ArrayList<Thread> clientThreads = new ArrayList<>();
    ServerModule(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Server is now on port " + port);
    }
    public void startServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopServer();
            System.out.println("Server closed");
        }));
        try {
            Socket clientSocket = socket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread clientThread = new Thread(clientHandler::run);
            clientThread.start();
            clientThreads.add(clientThread);
        } catch (IOException e) {
            System.out.println("Error accepting client connection");
            throw new RuntimeException(e);
        }
    }

    public void stopServer() {
        for(Thread clientThread : clientThreads) {
            clientThread.interrupt();
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Define other client methods here

}
