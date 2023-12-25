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
        ChatModel newChat = chatService.addChat(isGroup, username);
        chatMemberService.addChatMember(newChat.getChatID(), userid);
        chatMemberService.addChatMember(newChat.getChatID(), userService.getUserIDFromUsername(username));
    }

    // Send a chat to a new username
    public void sendMessage(String receiver, String message) {
        int receiverID = userService.getUserIDFromUsername(receiver);
        ChatModel chat = chatService.findChat(userid, receiverID);
        messageService.addMessage(chat.getChatID(), userid, message);

    }

    // TODO: Get chat history
    public ArrayList<MessageModel> getMessageHistory(String receiver) {
        int receiverID = userService.getUserIDFromUsername(receiver);
        ChatModel chat = chatService.findChat(userid, receiverID);
        return messageService.getMessages(chat.getChatID());
    }
}
