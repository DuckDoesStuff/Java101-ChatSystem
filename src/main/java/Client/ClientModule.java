package Client;
import java.io.*;
import java.net.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import DataStructure.PackageDataStructure;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

public class ClientModule implements Runnable {
    Socket clientSocket;

    ObjectOutputStream out;
    ObjectInputStream in;
    String username;
    Scanner scanner;
    boolean loggedIn = false;

    ClientModule(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to server: " +
                clientSocket.getInetAddress().getHostAddress() + ":" +
                clientSocket.getPort());
    }

    @Override
    public void run() {
//        PackageDataStructure firstPackage = new PackageDataStructure(
//                username,
//                0
//        );
//        sendPackageData(firstPackage);
        loggedIn = authUser();

        packageListener();

        while(clientSocket.isConnected() && loggedIn) {
            String content = scanner.nextLine();
            if(clientSocket.isClosed()) {
                System.out.println("Connection closed");
                break;
            }

            if(content.startsWith("/addfriend")) {
                String friendUsername = content.substring(11);
                PackageDataStructure addFriendPD = new PackageDataStructure(
                        "/addfriend",
                        0
                );
                PackageDataStructure friendUsernamePD = new PackageDataStructure(
                        friendUsername,
                        0
                );
                sendPackageData(addFriendPD);
                sendPackageData(friendUsernamePD);
            }
            else if (content.startsWith("/acceptfriend")){
                String friendUsername = content.substring(14);
                PackageDataStructure acceptFriendPD = new PackageDataStructure(
                        "/acceptfriend",
                        0
                );
                PackageDataStructure friendUsernamePD = new PackageDataStructure(
                        friendUsername,
                        0
                );
                sendPackageData(acceptFriendPD);
                sendPackageData(friendUsernamePD);
            }
            else if (content.equals("/exit")) {
                PackageDataStructure exitPD = new PackageDataStructure(
                        "/exit",
                        0
                );
                sendPackageData(exitPD);
                closeConnection();
                break;
            }
            else {
                PackageDataStructure messagePD = new PackageDataStructure(
                        content,
                        0
                );
                sendPackageData(messagePD);
            }
        }
    }

    public PackageDataStructure receivePackageData() {
        PackageDataStructure packageData;
        try {
            packageData = (PackageDataStructure) in.readObject();
        } catch (Exception e) {
            System.out.println("Error receiving package data");
            throw new RuntimeException(e);
        }
        return packageData;
    }

    public void sendPackageData(PackageDataStructure packageData) {
        try {
            out.writeObject(packageData);
        } catch (Exception e) {
            System.out.println("Error sending package data");
            throw new RuntimeException(e);
        }
    }

    //Define other client methods here

    public boolean authUser() {
        System.out.println("Login or Register? (l/r)");
        String loginOrRegister = scanner.nextLine();
        if (loginOrRegister.equals("l")) {
            System.out.println("Enter your username: ");
            username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            PackageDataStructure loginPD = new PackageDataStructure(
                    "/login",
                    0
            );
            PackageDataStructure usernamePD = new PackageDataStructure(
                    username,
                    0
            );
            PackageDataStructure passwordPD = new PackageDataStructure(
                    password,
                    0
            );

            sendPackageData(loginPD);
            sendPackageData(usernamePD);
            sendPackageData(passwordPD);
            return receivePackageData().content.equals("success");

        } else if (loginOrRegister.equals("r")) {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your email: ");
            String email = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            PackageDataStructure registerPD = new PackageDataStructure(
                    "/register",
                    0
            );
            PackageDataStructure usernamePD = new PackageDataStructure(
                    username,
                    0
            );
            PackageDataStructure emailPD = new PackageDataStructure(
                    email,
                    0
            );
            PackageDataStructure passwordPD = new PackageDataStructure(
                    password,
                    0
            );

            sendPackageData(registerPD);
            sendPackageData(usernamePD);
            sendPackageData(emailPD);
            sendPackageData(passwordPD);
            return receivePackageData().content.equals("success");
        } else {
            System.out.println("Invalid input");
            return false;
        }
    }

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
