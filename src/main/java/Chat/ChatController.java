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

    public boolean setUserID(int userid){
        this.userid = userid;
        return true;
    }
    public void createChat(String username, boolean isGroup) {
        if (isGroup) {
            ChatModel newChat = chatService.addChat(isGroup, "Group of " + userService.findUsernameWithUserID(userid));
            chatMemberService.addChatMember(newChat.getChatID(), userid);
            chatMemberService.addChatMember(newChat.getChatID(), userService.getUserIDFromUsername(username));
        }
        else {
            ChatModel newChat = chatService.addChat(isGroup, userService.findUsernameWithUserID(userid) + ',' + username);
            chatMemberService.addChatMember(newChat.getChatID(), userid);
            chatMemberService.addChatMember(newChat.getChatID(), userService.getUserIDFromUsername(username));
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

    public void sendGroupMessage(String groupName, String message) {
        int chatID = chatService.getChatIdFromName(groupName);
        messageService.addMessage(chatID, userid, message);
    }

    public ArrayList<String> getGroupMembers(String groupName){
        return chatMemberService.getGroupMembers(chatService.getChatIdFromName(groupName));
    }

    // TODO: Get chat history
    public ArrayList<MessageModel> getMessageHistory(String receiver) {
        int receiverID = userService.getUserIDFromUsername(receiver);
        ChatModel chat = chatService.findChat(userid, receiverID);
        return messageService.getMessages(chat.getChatID());
    }

    public ArrayList<MessageModel> getGroupMessageHistory(String groupName) {
        return messageService.getMessages(chatService.getChatIdFromName(groupName));
    }

    public ArrayList<ChatModel> getAllGroupChat(){
        return chatService.getAllGroupChat(userid);
    }
}
