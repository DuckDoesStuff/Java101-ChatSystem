package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import Chat.ChatController;
import Chat.ChatModel;
import Chat.ChatService;
import ChatMember.ChatMemberService;
import DataStructure.PackageDataStructure;
import DataStructure.UserInfo;
import Database.DB;
import Friend.FriendService;
import User.UserService;

class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    public static ArrayList<GroupChat> groups = new ArrayList<>();
    Socket clientSocket;
    UserInfo user;

    ObjectOutputStream out;
    ObjectInputStream in;
    String username;

    ChatController chatController;
    FriendService friendService;
    UserService userService;
    Connection conn;

    ClientHandler(Socket clientSocket, Connection conn) {
        this.clientSocket = clientSocket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            clients.add(this);
        } catch (IOException e) {
            System.out.println("Error creating object streams");
            throw new RuntimeException(e);
        }
        chatController = new ChatController(conn);
        userService = new UserService(conn);
        friendService = new FriendService(conn);
    }

    @Override
    public void run() {
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
//        PackageDataStructure firstPackage = receivePackageData();
//        username = firstPackage.content;

        //Testing the new features
        if(!authUser()) {
            System.out.println("Authentication failed");
            closeConnection();
            return;
        }

        System.out.println("Client username: " + username);

        while(clientSocket.isConnected()) {
            PackageDataStructure packageData;
            try {
                packageData = receivePackageData();
            }catch (Exception e) {
                System.out.println("Error receiving package data in main loop");
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
            }
            else if (packageData.content.startsWith("/msg")){
                String[] split = packageData.content.split(" ");
                String user = split[1];
                PackageDataStructure pd = new PackageDataStructure(
                        "MSG from " + username + ": " + packageData.content,
                        0);
                sendToClient(pd, user);
            }
            else if (packageData.content.startsWith("/newgroup")){
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
            else if (packageData.content.equals("/addfriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content;
                PackageDataStructure resultPD = new PackageDataStructure("", 0);
                if(friendService.sendRequest(username, friendUsername)) {
                    System.out.println("Friend request sent");
                    resultPD.content = "success";
                }
                else {
                    System.out.println("Error sending friend request");
                    resultPD.content = "failed";
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.equals("/acceptfriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content;
                PackageDataStructure resultPD = new PackageDataStructure("", 0);
                if(friendService.acceptRequest(friendUsername, username)) {
                    System.out.println("Friend request accepted");
                    resultPD.content = "success";
                    chatController.createChat(friendUsername, false);
                }
                else {
                    System.out.println("Error accepting friend request");
                    resultPD.content = "failed";
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.equals("/requests")) {
                ArrayList<String> requests = friendService.getRequestList(username);
                PackageDataStructure requestsPD = new PackageDataStructure("", 0);
                if (requests == null || requests.isEmpty()){
                    requestsPD.content = "No friend requests";
                }else{
                    requestsPD.content = "Friend requests:\n";
                    for (String request : requests) {
                        requestsPD.content += request + "\n";
                    }
                }
                sendPackageData(requestsPD);
            }
            else if (packageData.content.equals("/friends")) {
                ArrayList<String> friends = friendService.getFriendList(username);
                PackageDataStructure friendsPD = new PackageDataStructure("", 0);
                if (friends == null || friends.isEmpty()){
                    friendsPD.content = "No friends";
                }else{
                    friendsPD.content = "Friends:\n";
                    for (String friend : friends) {
                        friendsPD.content += friend + "\n";
                    }
                }
                sendPackageData(friendsPD);
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

    public boolean authUser() {
        PackageDataStructure pd = receivePackageData();
        if(pd.content.equals("/login")) {
            PackageDataStructure usernamePD = receivePackageData();
            PackageDataStructure passwordPD = receivePackageData();
            PackageDataStructure resultPD = new PackageDataStructure("", 0);
            if(userService.loginUser(usernamePD.content, passwordPD.content)) {
                this.username = usernamePD.content;
                chatController.setUserID(userService.getUserIDFromUsername(usernamePD.content));
                System.out.println("User" + this.username + " has logged in");
                resultPD.content = "success";
                sendPackageData(resultPD);
                return true;
            }
            else {
                resultPD.content = "failed";
                sendPackageData(resultPD);
                return false;
            }
        }
        else if(pd.content.equals("/register")) {
            PackageDataStructure usernamePD = receivePackageData();
            PackageDataStructure emailPD = receivePackageData();
            PackageDataStructure passwordPD = receivePackageData();
            System.out.println("Username: " + usernamePD.content);
            System.out.println("Email: " + emailPD.content);
            System.out.println("Password: " + passwordPD.content);
            PackageDataStructure resultPD = new PackageDataStructure("", 0);
            if(userService.registerUser(usernamePD.content, passwordPD.content, emailPD.content)) {
                this.username = usernamePD.content;
                chatController.setUserID(userService.getUserIDFromUsername(usernamePD.content));
                System.out.println("User" + this.username + " has been registered");
                resultPD.content = "success";
                sendPackageData(resultPD);
                return true;
            }
            else {
                System.out.println("Error registering user");
                resultPD.content = "failed";
                sendPackageData(resultPD);
                return false;
            }
        }
        else {
            System.out.println("Invalid command");
            return false;
        }
    }
}


public class ServerModule {
    ServerSocket socket;
    boolean isRunning;
    DB db;
    Connection conn;
    ServerModule(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Server is now on port " + port);
        db = new DB();
        conn = db.getConnection();
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
                ClientHandler clientHandler = new ClientHandler(clientSocket, conn);
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
            db.closeConnection();
        } catch (IOException e) {
            System.out.println("Error closing server socket");
            throw new RuntimeException(e);
        }
    }
    //Define other client methods here
}
