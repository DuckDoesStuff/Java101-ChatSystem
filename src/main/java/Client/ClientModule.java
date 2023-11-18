package Client;
import java.io.*;
import java.net.*;

import SampleDataStructure.PackageDataStructure;

public class ClientModule {
    Socket socket;
    String host;
    int port;
    BufferedReader in;
    PrintWriter out;

    ObjectOutputStream objOut;
    ObjectInputStream objIn;
    ClientModule(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            objOut = new ObjectOutputStream(socket.getOutputStream());
            objIn = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        System.out.println("Connected to server: " + host + ":" + port);
    }
    //Define other client methods here
    public void receiveMessage() {
        String serverMessage;
        try {
            System.out.println("Waiting for message from server...");
            while(!in.ready()) {}
            System.out.println("Message received from server");

            serverMessage = in.readLine();
            System.out.println("Server " + host + ":" + port + " says: " + serverMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(String message) {
        System.out.println("Sending message: " + message);
        out.println(message);
    }
    public void sendPackageData(PackageDataStructure packageData) {
        try {
            objOut.writeObject(packageData);
        } catch (IOException e) {
            System.out.println("Error sending package data");
            throw new RuntimeException(e);
        }
    }
    public PackageDataStructure receivePackageData() {
        PackageDataStructure packageData;
        try {
            System.out.println("Waiting for package data from server...");
            packageData = (PackageDataStructure) objIn.readObject();
            System.out.println("Package data received from server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return packageData;
    }
}
