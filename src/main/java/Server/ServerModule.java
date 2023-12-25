package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import Message.MessageModel;
import User.UserController;
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

    UserController userController;
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
        userController = new UserController(conn);
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
        chatController.setUserID(userService.getUserIDFromUsername(username));
        while(clientSocket.isConnected()) {
            PackageDataStructure packageData;
            try {
                packageData = receivePackageData();
            }catch (Exception e) {
                System.out.println("Error receiving package data in main loop");
                closeConnection();
                break;
            }

            if (packageData.content.getFirst().equals("/exit")) {
                PackageDataStructure pd = new PackageDataStructure(
                        username + " has left the chat"
                        );
                userService.logoutUser(username);
                System.out.println(username + " has left the chat");
                broadcast(packageData);
                closeConnection();
                break;
            }
//            else if (packageData.content.getFirst().startsWith("/msg")){
//                String[] split = packageData.content.split(" ");
//                String user = split[1];
//                PackageDataStructure pd = new PackageDataStructure(
//                        "MSG from " + username + ": " + packageData.content);
//                chatController.sendMessage(user, packageData.content);
//                sendToClient(pd, user);
//            }
//            else if (packageData.content.startsWith("/newgroup")){
//                //</newgroup groupname user1 user2 user3...>
//                String[] split = packageData.content.split(" ");
//                String groupName = split[1];
//                GroupChat newGroup = new GroupChat(groupName);
//                for (int i = 2; i < split.length; i++){
//                    newGroup.addMember(split[i]);
//                }
//                groups.add(newGroup);
//            }
            else if (packageData.content.get(0).startsWith("/chatgroup")){
                String[] split = packageData.content.get(0).split(" ");
                String groupName = split[1];
                String content = split[2];
                sendToGroupMembers(new PackageDataStructure(content), groupName);
            }
            else if (packageData.content.equals("/addfriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content.get(0);
                PackageDataStructure resultPD = new PackageDataStructure("");
                if(friendService.sendRequest(username, friendUsername)) {
                    System.out.println("Friend request sent");
                    resultPD.content.add("success");
                }
                else {
                    System.out.println("Error sending friend request");
                    resultPD.content.add("failed");
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.equals("/acceptfriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content.get(0);
                PackageDataStructure resultPD = new PackageDataStructure("");
                if(friendService.acceptRequest(friendUsername, username)) {
                    System.out.println("Friend request accepted");
                    resultPD.content.add("success");
                    chatController.createChat(friendUsername, false);
                }
                else {
                    System.out.println("Error accepting friend request");
                    resultPD.content.add("failed");
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.equals("/declinefriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content.get(0);
                PackageDataStructure resultPD = new PackageDataStructure("");
                if(friendService.declineRequest(friendUsername, username)) {
                    System.out.println("Friend request declined");
                    resultPD.content.add("success");
                }
                else {
                    System.out.println("Error declining friend request");
                    resultPD.content.add("failed");
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.equals("/requests")) {
                ArrayList<String> requests = friendService.getRequestList(username);
                PackageDataStructure requestsPD = new PackageDataStructure("");
                if (requests == null || requests.isEmpty()){
                    requestsPD.content.add("No friend requests");
                }else{
                    //requestsPD.content = "Friend requests:\n";
                    for (String request : requests) {
                        requestsPD.content.add(request);
                    }
                }
                sendPackageData(requestsPD);
            }
            else if (packageData.content.equals("/removerequest")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content.get(0);
                PackageDataStructure resultPD = new PackageDataStructure("");
                if(friendService.removeRequest(username, friendUsername)) {
                    System.out.println("Friend request removed");
                    resultPD.content.add("success");
                }
                else {
                    System.out.println("Error removing friend request");
                    resultPD.content.add("success");
                }
                sendPackageData(resultPD);
            }
            else if (packageData.content.getFirst().equals("/friends")) {
                System.out.println("Received get friend list pd request");
                ArrayList<String> friends = friendService.getFriendList(username);
//                for (String friend : friends){
//                   System.out.println(friend);
//                }
                System.out.println("Done read the friend list");

                PackageDataStructure friendsPD = new PackageDataStructure("");
                //PackageDataStructure friendsStatus = new PackageDataStructure("");
                if (friends == null || friends.isEmpty()){
                    friendsPD.content.add("No friends");
                }else{
                    //friendsPD.content = "Friends:\n";
                    //friendsStatus.content.add(Boolean.toString(userController.getUserStatus(friend)));
                    friendsPD.content.addAll(friends);
                }
                System.out.println("Done prepare the friend list");

                sendPackageData(friendsPD);
                System.out.println("Sent the friend list");

                //sendPackageData(friendsStatus);
            }
            else if (packageData.content.getFirst().equals("/friendstatus")){
                ArrayList<String> friends = packageData.content;
                PackageDataStructure friendsStatus = new PackageDataStructure("");
                for (int i = 1; i < friends.size(); i++){
                    friendsStatus.content.add(Boolean.toString(userController.getUserStatus(friends.get(i))));
                }
                sendPackageData(friendsStatus);
            }
            else if (packageData.content.get(0).startsWith("/history")){
                String[] split = packageData.content.get(0).split(" ");
                String friendname = split[1];
                ArrayList<MessageModel> history = chatController.getMessageHistory(friendname);
                PackageDataStructure result = new PackageDataStructure("");
                for (MessageModel message : history){
                    String msg = userService.findUsernameWithUserID(message.getSendUserID()) + "," + message.getContent();
                    //PackageDataStructure pd = new PackageDataStructure(msg);
                    result.content.add(msg);
                }
                sendPackageData(result);
            }
            else if (packageData.content.equals("/unfriend")) {
                PackageDataStructure friendUsernamePD = receivePackageData();
                String friendUsername = friendUsernamePD.content.get(0);
                PackageDataStructure resultPD = new PackageDataStructure("");
                if(friendService.removeFriend(username, friendUsername)) {
                    System.out.println("Friend removed");
                    resultPD.content.add("success");
                }
                else {
                    System.out.println("Error removing friend");
                    resultPD.content.add("failed");
                }
                sendPackageData(resultPD);
            }
            else {
                PackageDataStructure pd = new PackageDataStructure(
                        username + ": " + packageData.content.get(0));
                broadcast(pd);
            }
        }
    }

    public PackageDataStructure receivePackageData() {
        try {
            return (PackageDataStructure) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving package data");
            return null;
        }
    }

    public void sendPackageData(PackageDataStructure packageData) {
        try {
            out.writeObject(packageData);
        } catch (IOException e) {
            System.out.println("Error sending package data");
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
        System.out.println(packageData.content.getFirst());
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
        while(true) {
            PackageDataStructure pd = receivePackageData();
            if (pd.content.getFirst().equals("/login")) {
                String username = pd.content.get(1);
                String password = pd.content.get(2);
                PackageDataStructure resultPD = new PackageDataStructure("");
                String result = userService.loginUser(username, password);
                resultPD.content.add(result);
                sendPackageData(resultPD);
                if (result.equals("success")) {
                    this.username = username;
                    System.out.println("User " + this.username + " has logged in");
                    return true;
                }
            }
            else if (pd.content.getFirst().equals("/register")) {
                PackageDataStructure registerPD = pd;

                System.out.println("Username: " + registerPD.content.get(1));
                String username = registerPD.content.get(1);
                System.out.println("Email: " + registerPD.content.get(2));
                String email = registerPD.content.get(2);
                System.out.println("Password: " + registerPD.content.get(3));
                String password = registerPD.content.get(3);
                System.out.println("Firstname: " + registerPD.content.get(4));
                String firstname = registerPD.content.get(4);
                System.out.println("Lastname: " + registerPD.content.get(5));
                String lastname = registerPD.content.get(5);
                System.out.println("Address: " + registerPD.content.get(6));
                String address = registerPD.content.get(6);
                System.out.println("DOB: " + registerPD.content.get(7));
                String dob = registerPD.content.get(7);
                System.out.println("Gender: " + registerPD.content.get(8));
                String gender = registerPD.content.get(8);
                PackageDataStructure resultPD = new PackageDataStructure("");
                String result = userService.registerUser(username,
                       password, email, firstname, lastname, address, dob, gender);
                resultPD.content.add(result);
                sendPackageData(resultPD);
                if (result.equals("success")) {
                    this.username = registerPD.content.get(1);
                    System.out.println("User " + this.username + " has registered");
                    return true;
                }
            }
            else {
                System.out.println("Invalid command " + pd.content  );
                break;
            }
        }
        return false;
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
    //Define other server methods here
}
