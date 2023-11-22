package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import DataStructure.PackageDataStructure;
import DataStructure.UserInfo;

class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    public static ArrayList<GroupChat> groups = new ArrayList<>();
    Socket clientSocket;
    UserInfo user;

    ObjectOutputStream out;
    ObjectInputStream in;
    String username;

    ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            clients.add(this);
        } catch (IOException e) {
            System.out.println("Error creating object streams");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
        PackageDataStructure firstPackage = receivePackageData();
        username = firstPackage.content;
        user = new UserInfo(username);

        System.out.println("Client username: " + username);

        while(clientSocket.isConnected()) {
            PackageDataStructure packageData;
            try {
                packageData = receivePackageData();
            }catch (Exception e) {
                System.out.println("Error receiving package data");
                closeConnection();
                break;
            }

            if (packageData.content.equals("/exit")) {
                PackageDataStructure pd = new PackageDataStructure(
                        " has left the chat",
                        0);
                System.out.println(username + " has left the chat");
                broadcast(packageData);
                closeConnection();
                break;
            }else if (packageData.content.startsWith("/msg")){
                String[] split = packageData.content.split(" ");
                String user = split[1];
                PackageDataStructure pd = new PackageDataStructure(
                        "MSG from " + username + ": " + packageData.content,
                        0);
                sendToClient(pd, user);
            }else if (packageData.content.startsWith("/newgroup")){
                //</newgroup groupname user1 user2 user3...>
                String[] split = packageData.content.split(" ");
                String groupName = split[1];
                GroupChat newGroup = new GroupChat(groupName);
                for (int i = 2; i < split.length; i++){
                    newGroup.addMember(split[i]);
                }
                groups.add(newGroup);
            }
            else if (packageData.content.startsWith("/chatgroup")){
                String[] split = packageData.content.split(" ");
                String groupName = split[1];
                String content = split[2];
                sendToGroupMembers(new PackageDataStructure(content, 0), groupName);
            }
            else if (packageData.content.startsWith("/addfriend")) {
                String[] split = packageData.content.split(" ");
                String username = split[1];
                user.addFriend(username);
            }
            else if (packageData.content.startsWith("/friendlist")) {
                String content = "";
                for(String name : user.friendList) {
                    content += name + " ";
                }
                sendToClient(new PackageDataStructure(content, 0), username);
            }
            else {
                PackageDataStructure pd = new PackageDataStructure(
                        username + ": " + packageData.content,
                        0);
                broadcast(pd);
            }
        }
    }

    public PackageDataStructure receivePackageData() {
        try {
            return (PackageDataStructure) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving package data");
            throw new RuntimeException(e);
        }
    }

    public void sendPackageData(PackageDataStructure packageData) {
        try {
            out.writeObject(packageData);
        } catch (IOException e) {
            System.out.println("Error sending package data");
            throw new RuntimeException(e);
        }
    }

    //Define other client methods here

    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            clients.remove(this);
        } catch (IOException e) {
            System.out.println("Error closing client socket");
            throw new RuntimeException(e);
        }
    }

    public void broadcast(PackageDataStructure packageData) {
        System.out.println(packageData.content);
        if(clients.isEmpty()) {
            System.out.println("No other clients connected");
            return;
        }
        for (ClientHandler client : clients) {
            if(client.username.equals(username))
                continue;
            client.sendPackageData(packageData);
        }
    }

    public void sendToClient(PackageDataStructure packageData, String username) {
        for (ClientHandler client : clients) {
            if(client.username.equals(username)) {
                client.sendPackageData(packageData);
                return;
            }
        }
        System.out.println("User not found");
    }

    public void sendToGroupMembers(PackageDataStructure packageData, String groupName){
        for (GroupChat group: groups){
            if (Objects.equals(group.getGroupName(), groupName)){
                for (String username: group.groupMembers){
                    sendToClient(packageData, username);
                }
                return;
            }
            else{
                continue;
            }
        }
    }
}


public class ServerModule {
    ServerSocket socket;
    boolean isRunning;

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
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                if(Objects.equals(scanner.nextLine(), "/exit")) {
                    stopServer();
                    break;
                }
                else
                    System.out.println("Invalid command");
            }
        }).start();
        isRunning = true;
        while(isRunning) {
            try {
                Socket clientSocket = socket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (SocketException e) {
               System.out.println("Server socket closed");
                break;
            } catch (IOException e) {
                System.out.println("Error accepting client connection");
                break;
            }
        }
    }

    public void stopServer() {
        try {
            if (socket != null)
                socket.close();
            isRunning = false;
        } catch (IOException e) {
            System.out.println("Error closing server socket");
            throw new RuntimeException(e);
        }
    }
    //Define other client methods here
}
