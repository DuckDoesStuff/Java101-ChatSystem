package Client;
import java.io.*;
import java.net.*;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.atomic.LongAccumulator;

import DataStructure.PackageDataStructure;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

public class ClientModule implements Runnable {
    Socket clientSocket;
    Socket clientChatSocket;
    ObjectOutputStream out;
    public ObjectOutputStream outChat;
    ObjectInputStream in;
    public ObjectInputStream inChat;
    String username;
    Scanner scanner;
    volatile boolean loggedIn = false;

    private static ClientModule instance;

    public static synchronized ClientModule getInstance(String host, int portForData, int portForChat) {
        if (instance == null) {
            System.out.println("Initializing new client module instance");
            instance = new ClientModule(host, portForData, portForChat);
        }
        System.out.println("Returning existing client module instance");
        return instance;
    }



    ClientModule(String host, int portForData, int portForChat) {
        try {
            clientSocket = new Socket(host, portForData);
            System.out.println("accepted socket for data");
            clientChatSocket = new Socket(host, portForChat);
            System.out.println("accepted socket for chat");

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();

            outChat = new ObjectOutputStream(clientChatSocket.getOutputStream());
            outChat.flush();

            inChat = new ObjectInputStream(clientChatSocket.getInputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("accepted streams");
            scanner = new Scanner(System.in);
            System.out.println("accepted scanner");
            System.out.println("Connected to server port for Data " +
                    clientSocket.getInetAddress().getHostAddress() + ":" +
                    clientSocket.getPort());
            System.out.println("Connected to server port for Chat " +
                    clientChatSocket.getInetAddress().getHostAddress() + ":" +
                    clientChatSocket.getPort());
        } catch (Exception e) {
            System.out.println("Error connecting to server");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        if(!isConnected()) {
            System.out.println("Client not connected");
            return;
        }
        while (!loggedIn) Thread.onSpinWait();

        //packageListener();

        while(isConnected() && loggedIn) {
            String content = scanner.nextLine();
            if(clientSocket.isClosed()) {
            System.out.println("Connection closed");
            break;
            }

            // These are for console interactions
            if(content.startsWith("/addfriend")) {
                String friendUsername = content.substring(11);
                String[] pdContent = new String[2];
                pdContent[0] = "/addfriend";
                pdContent[1] = friendUsername;
                PackageDataStructure addFriendPD = new PackageDataStructure(
                        pdContent
                );

                sendPackageData(addFriendPD);
                //sendPackageData(friendUsernamePD);
            }
            else if(content.startsWith("/removerequest")) {
                String friendUsername = content.substring(15);
                String[] pdContent = new String[2];
                pdContent[0] = "/removerequest";
                pdContent[1] = friendUsername;
                PackageDataStructure removeRequestPD = new PackageDataStructure(
                        pdContent
                );
                sendPackageData(removeRequestPD);
                //sendPackageData(friendUsernamePD);
            }
            else if (content.startsWith("/acceptfriend")){
                String friendUsername = content.substring(14);
                String[] pdContent = new String[2];
                pdContent[0] = "/acceptfriend";
                pdContent[1] = friendUsername;
                PackageDataStructure acceptFriendPD = new PackageDataStructure(
                        pdContent
                );

                sendPackageData(acceptFriendPD);
                //sendPackageData(friendUsernamePD);
            }
            else if (content.startsWith("/declinefriend")) {
                String friendUsername = content.substring(15);
                String[] pdContent = new String[2];
                pdContent[0] = "/declinefriend";
                pdContent[1] = friendUsername;
                PackageDataStructure declineFriendPD = new PackageDataStructure(
                        pdContent
                );

                sendPackageData(declineFriendPD);
                //sendPackageData(friendUsernamePD);
            }
            else if (content.startsWith("/unfriend")) {
                String friendUsername = content.substring(10);
                String[] pdContent = new String[2];
                pdContent[0] = "/unfriend";
                pdContent[1] = friendUsername;
                PackageDataStructure unfriendPD = new PackageDataStructure(
                        pdContent
                );

                sendPackageData(unfriendPD);
                //sendPackageData(friendUsernamePD);
            }
            else if (content.equals("/exit")) {
                closeConnection();
                break;
            }
            else {
                PackageDataStructure messagePD = new PackageDataStructure(
                        content
                );
                sendPackageData(messagePD);
            }
        }
    }



    public PackageDataStructure receivePackageData() {
        PackageDataStructure packageData;
        try {
            System.out.println("Waiting for package data");
            packageData = (PackageDataStructure) in.readObject();
        } catch (Exception e) {
            System.out.println("Error receiving package data");
            System.out.println(e.getMessage());
            return null;
        }
        return packageData;
    }

    public PackageDataStructure receivePackageDataForChat() {
        PackageDataStructure packageData;
        try {
            packageData = (PackageDataStructure) inChat.readObject();
        } catch (Exception e) {
            System.out.println("Error receiving package data");

            return null;
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

    public void sendPackageDataForChat(PackageDataStructure packageData) {
        try {
            outChat.writeObject(packageData);
        } catch (Exception e) {
            System.out.println("Error sending package data");
        }
    }

    //Define other client methods here
    public String loginUser(String username, String password) {
        String[] loginPDcontent = new String[3];
        loginPDcontent[0] = "/login";
        loginPDcontent[1] = username;
        loginPDcontent[2] = password;
        PackageDataStructure loginPD = new PackageDataStructure(
                loginPDcontent
        );
        sendPackageData(loginPD);

        String result = receivePackageData().content.getFirst();
        loggedIn = result.equals("success");
        if (loggedIn) {
            this.username = username;
        }
        return result;
    }

    public String registerUser(String username, String email, String password, String firstname, String lastname, String address, Date dob, boolean gender) {
        PackageDataStructure registerPD = new PackageDataStructure(
                "");
        registerPD.content.add("/register");
        registerPD.content.add(username);
        registerPD.content.add(email);
        registerPD.content.add(password);
        registerPD.content.add(firstname);
        registerPD.content.add(lastname);
        registerPD.content.add(address);
        registerPD.content.add(new SimpleDateFormat("yyyy/MM/dd").format(dob));
        registerPD.content.add(gender ? "true" : "false");


        sendPackageData(registerPD);

        String result = receivePackageData().content.getFirst();
        loggedIn = result.equals("success");
        return result;
    }

    public ArrayList<String> getChatHistory(String friendUsername) {
        PackageDataStructure getChatHistoryPD = new PackageDataStructure(
                "/chathistory"
        );
        getChatHistoryPD.content.add(friendUsername);
        sendPackageData(getChatHistoryPD);
        System.out.println("Sent get chat history pd request");
        PackageDataStructure chatHistory = receivePackageData();
        System.out.println("Received chat history pd response");
        return chatHistory.content;
    }

    public ArrayList<String> getGroupHistory(String groupName){
        PackageDataStructure getGroupHistoryPD = new PackageDataStructure(
                "/grouphistory"
        );
        getGroupHistoryPD.content.add(groupName);
        sendPackageData(getGroupHistoryPD);
        System.out.println("Sent get group history pd request");
        PackageDataStructure groupHistory = receivePackageData();
        System.out.println("Received group history pd response");
        return groupHistory.content;
    }

    public ArrayList<String> getFriendList() {
        PackageDataStructure getFriendListPD = new PackageDataStructure(
                "/friends"
        );
        sendPackageData(getFriendListPD);
        System.out.println("Sent get friend list pd request");
        PackageDataStructure friendlist = receivePackageData();
        System.out.println("Received friend list pd response");
        return friendlist.content;
    }

    public ArrayList<String> getFriendStatus(ArrayList<String> friends){
        PackageDataStructure getFriendStatusPD = new PackageDataStructure(
                "/friendstatus"
        );
        getFriendStatusPD.content.addAll(friends);
        sendPackageData(getFriendStatusPD);
        System.out.println("Sent get friend status pd request");
        PackageDataStructure friendStatus = receivePackageData();
        System.out.println("Received friend status pd response");
        return friendStatus.content;
    }

    public boolean reportSpam(String username){
        PackageDataStructure reportpd = new PackageDataStructure("/reportspam");
        reportpd.content.add(username);
        sendPackageData(reportpd);
        System.out.println("Send unblock pd req");
        PackageDataStructure unblockUser = receivePackageData();
        System.out.println("Received unlock result");
        return unblockUser.content.getFirst().equals("true");
    }

    public ArrayList<String> getGroupChat(){
        PackageDataStructure getGroupPD = new PackageDataStructure(
                "/getgroups"
        );
        sendPackageData(getGroupPD);
        ArrayList<String> groupList = receivePackageData().content;
        return groupList;
    }

    public boolean blockUser(String username){
        PackageDataStructure blockUserPD = new PackageDataStructure(
                "/blockuser"
        );
        blockUserPD.content.add(username);
        sendPackageData(blockUserPD);
        System.out.println("Sent block user pd request");
        PackageDataStructure blockUser = receivePackageData();
        System.out.println("Received block user pd response");
        return blockUser.content.getFirst().equals("true");
    }

    public boolean unblockUser(String username){
        PackageDataStructure unblockpd = new PackageDataStructure("/unblockuser");
        unblockpd.content.add(username);
        sendPackageData(unblockpd);
        System.out.println("Send unblock pd req");
        PackageDataStructure unblockUser = receivePackageData();
        System.out.println("Received unlock result");
        return unblockUser.content.getFirst().equals("true");
    }

    public boolean checkIsBlocked(String username){
        PackageDataStructure checkIsBlockedPD = new PackageDataStructure(
                "/checkisblocked"
        );
        checkIsBlockedPD.content.add(username);
        sendPackageData(checkIsBlockedPD);
        System.out.println("Sent check is blocked pd request");
        PackageDataStructure isBlocked = receivePackageData();
        System.out.println("Received check is blocked pd response");
        return isBlocked.content.getFirst().equals("true");
    }

    public boolean sendMessage(String msg, String sender, String receiver, int type, int chatid) {
        PackageDataStructure sendMessagePD = new PackageDataStructure(
                "/sendmessage"
        );
        boolean isBlocked = checkIsBlocked(receiver);
        if (isBlocked) {
            System.out.println("Cannot send message to blocked user");
            return false;
        }
        sendMessagePD.content.add(msg);
        sendMessagePD.content.add(sender);
        sendMessagePD.content.add(receiver);
        sendMessagePD.content.add(Integer.toString(type));
        sendMessagePD.content.add(Integer.toString(chatid));
        sendPackageDataForChat(sendMessagePD);
        System.out.println("Sent send message pd request");

        return true;

    }



    public ArrayList<String> findUserByName(String username){
        PackageDataStructure findUserByNamePD = new PackageDataStructure(
                "/finduserbyname"
        );
        findUserByNamePD.content.add(username);
        sendPackageData(findUserByNamePD);
        System.out.println("Sent find user by name pd request");
        PackageDataStructure userList = receivePackageData();
        System.out.println("Received find user by name pd response");
        return userList.content;
    }

    public void closeConnection() {
        PackageDataStructure exitPD = new PackageDataStructure(
                "/exit"
        );
        sendPackageDataForChat(exitPD);
        sendPackageData(exitPD);
        try {
            if (clientSocket == null) {
                clientSocket.close();
                clientSocket = null;
            }

            //clientChatSocket.close();
            clientChatSocket = null;
            in.close();
            out.close();
            //inChat.close();
            //outChat.close();
            System.out.println("Client connection closed");
        } catch (IOException e) {
            System.out.println("Error closing connection");
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    public void createGroupChat(String chatMember) {
        PackageDataStructure createGroupPD = new PackageDataStructure(
                "/creategroup"
        );
        createGroupPD.content.add(chatMember);
        sendPackageData(createGroupPD);
        System.out.println("Sent create group pd request");
    }

    public String sendFriendRequest(String friendUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/addfriend";
        pdContent[1] = friendUsername;
        PackageDataStructure friendUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(friendUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public String removeFriendRequest(String friendUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/removerequest";
        pdContent[1] = friendUsername;
        PackageDataStructure friendUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(friendUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public String acceptFriendRequest(String senderUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/acceptfriend";
        pdContent[1] = senderUsername;
        PackageDataStructure senderUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(senderUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public String declineFriendRequest(String senderUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/declinefriend";
        pdContent[1] = senderUsername;
        PackageDataStructure senderUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(senderUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public String removeFriend(String friendUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/acceptfriend";
        pdContent[1] = friendUsername;
        PackageDataStructure friendUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(friendUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public String getFriendShipStatus(String friendUsername) {
        String[] pdContent = new String[2];
        pdContent[0] = "/friendshipstatus";
        pdContent[1] = friendUsername;
        PackageDataStructure friendUsernamePD = new PackageDataStructure(
                pdContent
        );
        sendPackageData(friendUsernamePD);

        PackageDataStructure resultPD = receivePackageData();
        return resultPD.content.getFirst();
    }

    public ArrayList<Integer> getGroupIDs() {
        PackageDataStructure pd = new PackageDataStructure("/getgroupids");
        sendPackageData(pd);
        PackageDataStructure resultPD = receivePackageData();
        ArrayList<Integer> result = new ArrayList<>();
        for (String id: resultPD.content){
            result.add(Integer.parseInt(id));
        }
        return result;
    }

    public boolean checkAdmin(int mainChatID) {
        PackageDataStructure pd = new PackageDataStructure("/checkisadmin");
        pd.content.add(Integer.toString(mainChatID));
        sendPackageData(pd);
        PackageDataStructure result = receivePackageData();
        return result.content.getFirst().equals("true");
    }

    public boolean addUserToGroup(String userToAdd, int mainChatID) {
        PackageDataStructure pd = new PackageDataStructure("/addusertogroup");
        pd.content.add(userToAdd);
        pd.content.add(Integer.toString(mainChatID));
        sendPackageData(pd);
        PackageDataStructure result = receivePackageData();
        return result.content.getFirst().equals("true");
    }

    public boolean changeGroupName(String newname, int mainChatID){
        PackageDataStructure pd = new PackageDataStructure("/changegroupname");
        pd.content.add(newname);
        pd.content.add(Integer.toString(mainChatID));
        sendPackageData(pd);
        PackageDataStructure result = receivePackageData();
        return result.content.getFirst().equals("true");
    }

    public boolean addGroupAdmin(String newadmin, int mainChatID){
        PackageDataStructure pd = new PackageDataStructure("/addgroupadmin");
        pd.content.add(newadmin);
        pd.content.add(Integer.toString(mainChatID));
        sendPackageData(pd);
        PackageDataStructure result = receivePackageData();
        return result.content.getFirst().equals("true");
    }
}
