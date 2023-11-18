package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import SampleDataStructure.PackageDataStructure;
class ClientHandler {
    Socket clientSocket;
    String host;
    int port;
    BufferedReader in;
    PrintWriter out;

    ObjectOutputStream objOut;
    ObjectInputStream objIn;
    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        host = clientSocket.getInetAddress().getHostAddress();
        port = clientSocket.getPort();
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating input stream");
            throw new RuntimeException(e);
        }
    }
    public void run() {
        System.out.println("Client connected: " + host + ":" + port);
//        receiveMessage();
//        sendMessage("Hello, client!");

        receivePackageData();
        PackageDataStructure packageData =
                new PackageDataStructure(
                        "From server",
                        "To client",
                        "Some random thing in content",
                        1234
                );
        sendPackageData(packageData);
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

    public void receivePackageData() {
        try {
            PackageDataStructure packageData = (PackageDataStructure) objIn.readObject();
            System.out.println("Package data received from client " + host + ":" + port);
            System.out.println("Package data: " + packageData);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving package data");
            throw new RuntimeException(e);
        }
    }

    public void sendPackageData(PackageDataStructure packageData) {
        try {
            objOut.writeObject(packageData);
        } catch (IOException e) {
            System.out.println("Error sending package data");
            throw new RuntimeException(e);
        }
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
            int connected = 0;
            while(connected < 2) {
                Socket clientSocket = socket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler::run);
                clientThread.start();
                clientThreads.add(clientThread);
                connected++;
            }
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
