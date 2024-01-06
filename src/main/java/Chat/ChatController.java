package Chat;

import ChatMember.ChatMemberService;
import Message.MessageModel;
import Message.MessageService;
import User.UserService;

import java.sql.Connection;
import java.util.ArrayList;

public class ChatController {
    private int userid;
    private ChatService chatService;
    private ChatMemberService chatMemberService;
    private MessageService messageService;
    private UserService userService;
    public ChatController(int userid, Connection conn){
            this.userid = userid;
            this.chatService = new ChatService(conn);
            this.messageService = new MessageService(conn);
            this.userService = new UserService(conn);
            this.chatMemberService = new ChatMemberService(conn);
    }

    public ChatController(Connection conn){
        this.userid = -1;
        this.chatService = new ChatService(conn);
        this.messageService = new MessageService(conn);
        this.userService = new UserService(conn);
        this.chatMemberService = new ChatMemberService(conn);
    }

    public boolean addGroupAdmin(String username, int chatID){
        return chatService.addGroupAdmin(userService.getUserIDFromUsername(username), chatID);
    }

    public boolean checkGroupAdmin(String username, int chatID){
        return chatService.checkIsAdmin(userService.getUserIDFromUsername(username), chatID);
    }

    public boolean deleteChatHistory(int chatID){
        return messageService.deleteChatHistory(chatID);
    }

    public boolean setUserID(int userid){
        this.userid = userid;
        return true;
    }
    public int createChat(String username, boolean isGroup) {
        if (isGroup) {
            ChatModel newChat = chatService.addChat(isGroup, "Group of " + userService.findUsernameWithUserID(userid));
            chatMemberService.addChatMember(newChat.getChatID(), userid);
            chatMemberService.addChatMember(newChat.getChatID(), userService.getUserIDFromUsername(username));
            return newChat.getChatID();
        }
        else {
            ChatModel newChat = chatService.addChat(isGroup, userService.findUsernameWithUserID(userid) + ',' + username);
            chatMemberService.addChatMember(newChat.getChatID(), userid);
            chatMemberService.addChatMember(newChat.getChatID(), userService.getUserIDFromUsername(username));
            return newChat.getChatID();
        }
    }

    public boolean blockStatus(String username){
        int receiverID = userService.getUserIDFromUsername(username);
        return userService.blockStatus(userid, receiverID);
    }

    public boolean blockUser(String username){
        int receiverID = userService.getUserIDFromUsername(username);
        return userService.blockUser(userid, receiverID);
    }

    public boolean unblockUser(String username){
        int receiverID = userService.getUserIDFromUsername(username);
        return userService.unblockUser(userid, receiverID);
    }

    public boolean reportSpammer(String username){
        int receiverID = userService.getUserIDFromUsername(username);
        return userService.reportSpammer(userid, receiverID);
    }

    // Send a chat to a new username
    public void sendMessage(String receiver, String message) {
        int receiverID = userService.getUserIDFromUsername(receiver);
        ChatModel chat = chatService.findChat(userid, receiverID);
        messageService.addMessage(chat.getChatID(), userid, message);

    }

    public void sendGroupMessage(int chatID, String message) {
        //int chatID = chatService.getChatIdFromName(groupName);
        messageService.addMessage(chatID, userid, message);
    }

    public boolean addGroupMember(String username, int groupID){
        // Check for admin

        // Add member code goes from here
        int userID = userService.getUserIDFromUsername(username);
        int result = chatMemberService.addChatMember(groupID, userID);
        if (result != -1){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean changeGroupName(String newname, int chatID){
        return chatService.changeGroupName(newname, chatID);
    }

    public boolean addNewAdmin(String username, int chatid){
        return chatService.addGroupAdmin(userService.getUserIDFromUsername(username), chatid);
    }

    public ArrayList<String> getGroupMembers(int groupID){
        return chatMemberService.getGroupMembers(groupID);
    }

    // TODO: Get chat history
    public ArrayList<MessageModel> getMessageHistory(String receiver) {
        int receiverID = userService.getUserIDFromUsername(receiver);
        ChatModel chat = chatService.findChat(userid, receiverID);
        return messageService.getMessages(chat.getChatID());
    }

    public ArrayList<MessageModel> getGroupMessageHistory(int groupid) {
        return messageService.getMessages(groupid);
    }

    public ArrayList<ChatModel> getAllGroupChat(){
        return chatService.getAllGroupChat(userid);
    }

    public void deleteChat(String username, String s) {
        int receiverID = userService.getUserIDFromUsername(s);
        ChatModel chat = chatService.findChat(userid, receiverID);
        messageService.deleteChatHistory(chat.getChatID());
        chatMemberService.deleteChatMember(receiverID,chat.getChatID());
        chatMemberService.deleteChatMember(userid,chat.getChatID());
        chatService.deleteChat(chat.getChatID());
    }

    public int findChatID(String username){
        int receiverID = userService.getUserIDFromUsername(username);
        ChatModel chat = chatService.findChat(userid, receiverID);
        return chat.getChatID();
    }

    public boolean deleteGroupMember(String userToDelete, int mainChatID) {
        int userID = userService.getUserIDFromUsername(userToDelete);
        return chatMemberService.deleteChatMember(userID, mainChatID);
    }
}
