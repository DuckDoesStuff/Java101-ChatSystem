package Client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import DataStructure.PackageDataStructure;

public class ClientModule implements Runnable {
    Socket clientSocket;

    ObjectOutputStream out;
    ObjectInputStream in;
    String username;
    Scanner scanner;

    ClientModule(String host, int port, String username) {
        try {
            clientSocket = new Socket(host, port);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.username = username;
        System.out.println("Connected to server: " +
                clientSocket.getInetAddress().getHostAddress() + ":" +
                clientSocket.getPort());
    }

    @Override
    public void run() {
        packageListener();
        PackageDataStructure firstPackage = new PackageDataStructure(
                username,
                0
        );
        sendPackageData(firstPackage);

        while(clientSocket.isConnected()) {
            String content = scanner.nextLine();
            if(clientSocket.isClosed()) {
                System.out.println("Connection closed");
                break;
            }
            PackageDataStructure packageData = new PackageDataStructure(
                    content,
                    0
            );
            sendPackageData(packageData);
        }
    }

    public PackageDataStructure receivePackageData() {
        PackageDataStructure packageData;
        try {
            packageData = (PackageDataStructure) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return packageData;
    }

    public void sendPackageData(PackageDataStructure packageData) {
        try {
            out.writeObject(packageData);
        } catch (Exception e) {
            System.out.println("Error sending package data");
        }
    }

    //Define other client methods here

    public void closeConnection() {
        try {
            clientSocket.close();
            in.close();
            out.close();
            System.out.println("Client connection closed");
        } catch (IOException e) {
            System.out.println("Error closing connection");
            throw new RuntimeException(e);
        }
    }

    public void packageListener() {
        new Thread(() -> {
            while (clientSocket.isConnected()) {
                PackageDataStructure packageData;
                try {
                    packageData = receivePackageData();
                } catch (Exception e) {
                    System.out.println("Socket probably closed");
                    closeConnection();
                    break;
                }
                System.out.println(packageData.content);
                //TODO: maybe? handle each command here with threads
            }
        }).start();
        System.out.println("Package listener started");
    }

}
