package Server;

import Chat.ChatController;
import Chat.ChatModel;
import DataStructure.PackageDataStructure;
import DataStructure.UserInfo;
import Database.DB;
import Friend.FriendService;
import Message.MessageModel;
import User.UserController;
import User.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

class ChatHandler implements Runnable {
    ChatController chatController;
    FriendService friendService;
    UserService userService;

    UserController userController;
    Connection conn;
    public static volatile ArrayList<ChatHandler> clients = new ArrayList<>();
    String username;
    Socket clientChatSocket;
    ObjectInputStream inChat; // for chat
    ObjectOutputStream outChat; // for chat

    ChatHandler(Socket clientChatSocket, Connection conn, String username, ObjectOutputStream outChat, ObjectInputStream inChat) {
        this.clientChatSocket = clientChatSocket;
        this.username = username;
        this.outChat = outChat;
        this.inChat = inChat;
        System.out.println("ChatHandler created");
        clients.add(this);

        chatController = new ChatController(conn);
        userService = new UserService(conn);
        userController = new UserController(conn);
        friendService = new FriendService(conn);
    }

    public void sendToClient(PackageDataStructure packageData, String username) {
        for (ChatHandler client : clients) {
            if (client.username.equals(username)) {
                client.sendPackageDataForChat(packageData);
                return;
            }
        }
        System.out.println("User not found");
    }

    public void sendToClient(PackageDataStructure packageData, ArrayList<String> usernames) {
        for (ChatHandler client : clients) {
            for (String username : usernames) {
                if (client.username.equals(username)) {
                    System.out.println("Sending to " + client.username);
                    client.sendPackageDataForChat(packageData);
                }
            }
        }
        //System.out.println("User not found");
    }

    public void run() {
        chatController.setUserID(userService.getUserIDFromUsername(username));
        while (clientChatSocket.isConnected()) {
            //PackageDataStructure packageData;
            PackageDataStructure packageDataForChat;
            try {
                //packageData = receivePackageDataForChat();
                packageDataForChat = receivePackageDataForChat();
            } catch (Exception e) {
                System.out.println("Error receiving package data in main loop");
                closeConnection();
                break;
            }

            if (packageDataForChat != null) {
                if (packageDataForChat.content.getFirst().equals("/exit")) {
                    closeConnection();
                } else if (packageDataForChat.content.get(0).equals("/sendmessage")) {
                    String msg = packageDataForChat.content.get(1);
                    String sender = packageDataForChat.content.get(2);
                    String type = packageDataForChat.content.get(4);
                    PackageDataStructure pd = new PackageDataStructure(
                            "");

                    if (Objects.equals(type, "0")) {
                        pd.content.add(sender);
                        pd.content.add(msg);
                        String receiver = packageDataForChat.content.get(3);
                        chatController.sendMessage(receiver, msg);
                        sendToClient(pd, receiver);
                    }

                    if (Objects.equals(type, "1")) {
                        String groupName = packageDataForChat.content.get(3);
                        pd.content.add(sender);
                        pd.content.add(msg);
                        pd.content.add(groupName);

                        String id = packageDataForChat.content.get(5);
                        pd.content.add(id);

                        ArrayList<String> groupMembers = chatController.getGroupMembers(Integer.parseInt(id));
                        chatController.sendGroupMessage(Integer.parseInt(id), msg);
                        sendToClient(pd, groupMembers);
                    }
                }
            }
        }
    }

    private void closeConnection() {
        try {
            if (clientChatSocket != null) {
                clientChatSocket.close();
                clientChatSocket = null;
            }
            inChat.close();
            outChat.close();
            clients.remove(this);
        } catch (IOException e) {
            System.out.println("Error closing client socket");
            throw new RuntimeException(e);
        }
    }


    public void sendPackageDataForChat(PackageDataStructure packageData) {
        try {
            outChat.writeObject(packageData);
        } catch (IOException e) {
            System.out.println("Error sending package data");
        }
    }

    public PackageDataStructure receivePackageDataForChat() {
        try {
            return (PackageDataStructure) inChat.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving package data for chat");
            return null;
        }
    }
}

class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    public static ArrayList<GroupChat> groups = new ArrayList<>();
    Socket clientSocket;
    UserInfo user;
    Socket clientChatSocket;
    ObjectOutputStream out; // for UI rendering
    ObjectInputStream in; // for UI rendering
    ObjectInputStream inChat; // for chat
    ObjectOutputStream outChat; // for chat
    String username;

    ChatController chatController;
    FriendService friendService;
    UserService userService;

    UserController userController;
    Connection conn;

    ClientHandler(Socket clientSocket, Socket clientChatSocket, Connection conn) {
        this.clientChatSocket = clientChatSocket;
        this.clientSocket = clientSocket;

        try {
            outChat = new ObjectOutputStream(clientChatSocket.getOutputStream());
            outChat.flush();
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();


            inChat = new ObjectInputStream(clientChatSocket.getInputStream());
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
        this.conn = conn;
    }

    @Override
    public void run() {

        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
//        PackageDataStructure firstPackage = receivePackageData();
//        username = firstPackage.content;

        //Testing the new features
        if (!authUser()) {
            System.out.println("Authentication failed");
            closeConnection();
            return;
        }
        new Thread(new ChatHandler(clientChatSocket, conn, username, outChat, inChat)).start();
        System.out.println("Client username: " + username);
        chatController.setUserID(userService.getUserIDFromUsername(username));
        //System.out.println("Connected to Socket of Chat");
        while (clientSocket.isConnected()) {
            PackageDataStructure packageData;
            try {
                packageData = receivePackageData();
            } catch (Exception e) {
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
            } else if (packageData.content.getFirst().equals("/getgroups")) {
                PackageDataStructure pd = new PackageDataStructure("");
                ArrayList<ChatModel> allGroups = chatController.getAllGroupChat();
                for (ChatModel group : allGroups) {
                    pd.content.add(group.getName());
                }
                sendPackageData(pd);
            } else if (packageData.content.getFirst().equals("/creategroup")) {
                int chatid = chatController.createChat(packageData.content.get(1), true);
                System.out.println("Created group with id " + chatid);
                System.out.println("Admin adding: " + username);
                boolean result = chatController.addGroupAdmin(username, chatid);
                System.out.println(result);
                System.out.println("Done adding admin");
                PackageDataStructure pd = new PackageDataStructure(
                        (result ? "true" : "false")
                );
                sendPackageData(pd);
            } else if (packageData.content.getFirst().equals("/getgroupids")) {
                ArrayList<ChatModel> allGroups = chatController.getAllGroupChat();
                PackageDataStructure result = new PackageDataStructure("");
                for (ChatModel group: allGroups){
                    result.content.add(Integer.toString(group.getChatID()));
                }
                sendPackageData(result);
            } else if (packageData.content.getFirst().equals("/grouphistory")) {
                //String groupName = packageData.content.get(1);
                int groupID = Integer.parseInt(packageData.content.get(1));
                ArrayList<MessageModel> history = chatController.getGroupMessageHistory(groupID);
                PackageDataStructure result = new PackageDataStructure("");
                for (MessageModel messageModel : history) {
                    String msg = userService.findUsernameWithUserID(messageModel.getSendUserID()) + "," + messageModel.getContent();
                    result.content.add(msg);
                }
                sendPackageData(result);
            } else if (packageData.content.getFirst().equals("/deletechathistory")){
                System.out.println("Receive delete chat history request");
                int chatid = Integer.parseInt(packageData.content.get(1));
                boolean result = chatController.deleteChatHistory(chatid);

                System.out.println("Result is: " + result);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/checkisadmin")) {
                int mainChatID = Integer.parseInt(packageData.content.get(1));
                boolean result = chatController.checkGroupAdmin(username, mainChatID);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/deleteuserfromgroup")){
                String userToDelete = packageData.content.get(1);
                int mainChatID = Integer.parseInt(packageData.content.get(2));
                boolean result = chatController.deleteGroupMember(userToDelete, mainChatID);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }

            }

            else if (packageData.content.getFirst().equals("/addusertogroup")) {
                String userToAdd = packageData.content.get(1);
                int mainChatID = Integer.parseInt(packageData.content.get(2));
                boolean result = chatController.addGroupMember(userToAdd, mainChatID);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/changegroupname")) {
                String newname = packageData.content.get(1);
                int chatID = Integer.parseInt(packageData.content.get(2));
                boolean result = chatController.changeGroupName(newname, chatID);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/addgroupadmin")) {
                String newadmin = packageData.content.get(1);
                int chatID = Integer.parseInt(packageData.content.get(2));
                boolean result = chatController.addNewAdmin(newadmin, chatID);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                }
                else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            }
            else if (packageData.content.getFirst().equals("/addfriend")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.sendRequest(username, packageData.content.get(1))
                );
                sendPackageData(resultPd);
            } else if (packageData.content.getFirst().equals("/acceptfriend")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.acceptRequest(packageData.content.get(1), username)
                );
                chatController.createChat(packageData.content.get(1), false);
                sendPackageData(resultPd);
            } else if (packageData.content.getFirst().equals("/declinefriend")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.declineRequest(packageData.content.get(1), username)
                );
                sendPackageData(resultPd);
            } else if (packageData.content.equals("/requests")) {
                ArrayList<String> requests = friendService.getRequestList(username);
                PackageDataStructure requestsPD = new PackageDataStructure("");
                if (requests == null || requests.isEmpty()) {
                    requestsPD.content.add("No friend requests");
                } else {
                    //requestsPD.content = "Friend requests:\n";
                    for (String request : requests) {
                        requestsPD.content.add(request);
                    }
                }
                sendPackageData(requestsPD);
            } else if (packageData.content.getFirst().equals("/removerequest")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.removeRequest(username, packageData.content.get(1))
                );
                sendPackageData(resultPd);
            } else if (packageData.content.getFirst().equals("/friendshipstatus")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.friendShipStatus(username, packageData.content.get(1))
                );
                sendPackageData(resultPd);
            } else if (packageData.content.getFirst().equals("/friends")) {
                System.out.println("Received get friend list pd request");
                ArrayList<String> friends = friendService.getFriendList(username);
//                for (String friend : friends){
//                   System.out.println(friend);
//                }
                System.out.println("Done read the friend list");

                PackageDataStructure friendsPD = new PackageDataStructure("");
                //PackageDataStructure friendsStatus = new PackageDataStructure("");
                if (friends == null || friends.isEmpty()) {
                    friendsPD.content.add("No friends");
                } else {

                    friendsPD.content.addAll(friends);
                }
                System.out.println("Done prepare the friend list");

                sendPackageData(friendsPD);
                System.out.println("Sent the friend list");

                //sendPackageData(friendsStatus);
            } else if (packageData.content.getFirst().equals("/friendstatus")) {
                ArrayList<String> friends = packageData.content;
                PackageDataStructure friendsStatus = new PackageDataStructure("");

                for (int i = 1; i < friends.size(); i++) {
                    friendsStatus.content.add(Boolean.toString(userController.getUserStatus(friends.get(i))));
                }

                sendPackageData(friendsStatus);
            } else if (packageData.content.get(0).equals("/chathistory")) {
                String friendname = packageData.content.get(1);
                ArrayList<MessageModel> history = chatController.getMessageHistory(friendname);
                PackageDataStructure result = new PackageDataStructure("");
                for (MessageModel message : history) {
                    String msg = userService.findUsernameWithUserID(message.getSendUserID()) + "," + message.getContent();
                    //PackageDataStructure pd = new PackageDataStructure(msg);
                    result.content.add(msg);
                }
                sendPackageData(result);
            } else if (packageData.content.getFirst().equals("/unfriend")) {
                PackageDataStructure resultPd = new PackageDataStructure(
                        friendService.removeFriend(username, packageData.content.get(1))
                );
                chatController.deleteChat(username, packageData.content.get(1));
                sendPackageData(resultPd);
            } else if (packageData.content.getFirst().equals("/finduserbyname")){
                ArrayList<String> usersMatch;
                String name = packageData.content.get(1);
                usersMatch = userService.findUserWithUsername(name, username);
                PackageDataStructure result = new PackageDataStructure("");
                if (usersMatch == null || usersMatch.isEmpty()) {
                    result.content.add("No users found");
                } else {
                    result.content.addAll(usersMatch);
                }
                sendPackageData(result);
            } else if (packageData.content.getFirst().equals("/checkisblocked")){
                PackageDataStructure result = new PackageDataStructure("");
                String username = packageData.content.get(1);
                boolean blockStatus = chatController.blockStatus(username);
                result.content.add(Boolean.toString(blockStatus));
                sendPackageData(result);
            } else if (packageData.content.getFirst().equals("/getchatid")) {
                String username = packageData.content.get(1);
                int chatID = chatController.findChatID(username);
                PackageDataStructure result = new PackageDataStructure("");
                result.content.add(Integer.toString(chatID));
                sendPackageData(result);
            }
            else if (packageData.content.getFirst().equals("/blockuser")){
                String username = packageData.content.get(1);
                boolean result = chatController.blockUser(username);
                if (result) {
                    PackageDataStructure pd = new PackageDataStructure(
                            "true"
                    );
                    sendPackageData(pd);
                } else {
                    PackageDataStructure pd = new PackageDataStructure(
                            "false"
                    );
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/unblockuser")) {
                String username = packageData.content.get(1);
                boolean result = chatController.unblockUser(username);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                } else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
            } else if (packageData.content.getFirst().equals("/reportspam")){
                String username = packageData.content.get(1);
                boolean result = chatController.reportSpammer(username);
                if (result){
                    PackageDataStructure pd = new PackageDataStructure("true");
                    sendPackageData(pd);
                } else {
                    PackageDataStructure pd = new PackageDataStructure("false");
                    sendPackageData(pd);
                }
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
            System.out.println("Error receiving package data for data");
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
            //clientChatSocket.close();
            clients.remove(this);
        } catch (IOException e) {
            System.out.println("Error closing client socket");
            throw new RuntimeException(e);
        }
    }

    public void broadcast(PackageDataStructure packageData) {
        System.out.println(packageData.content.getFirst());
        if (clients.isEmpty()) {
            System.out.println("No other clients connected");
            return;
        }
        for (ClientHandler client : clients) {
            if (client.username.equals(username))
                continue;
            client.sendPackageData(packageData);
        }
    }


    public boolean authUser() {
        while (true) {
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
            } else if (pd.content.getFirst().equals("/register")) {
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
            } else {
                System.out.println("Invalid command " + pd.content);
                break;
            }
        }
        return false;
    }
}


public class ServerModule {
    ServerSocket socket;
    ServerSocket chatSocket;
    boolean isRunning;
    DB db;
    Connection conn;

    ServerModule(int portForData, int portForChat) {
        try {
            socket = new ServerSocket(portForData);
            System.out.println("Server is now on port " + portForData + " for data");
            chatSocket = new ServerSocket(portForChat);
            System.out.println("Server is now on port " + portForChat + " for chat");
        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Server is now on port " + portForData + " for data and port " + portForChat + " for chat");
        db = new DB();
        conn = db.getConnection();
    }

    public void startServer() {
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                if (Objects.equals(scanner.nextLine(), "/exit")) {
                    stopServer();
                    break;
                } else
                    System.out.println("Invalid command");
            }
        }).start();
        isRunning = true;
        while (isRunning) {
            try {
                Socket clientSocket = socket.accept();
                Socket clientChatSocket = chatSocket.accept();
                System.out.println(clientChatSocket.isConnected());
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientChatSocket, conn);
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
            if (socket != null) {
                socket.close();
            }
            if (chatSocket != null) {
                chatSocket.close();
            }
            isRunning = false;
            db.closeConnection();
        } catch (IOException e) {
            System.out.println("Error closing server socket");
            throw new RuntimeException(e);
        }
    }
    //Define other server methods here
}
