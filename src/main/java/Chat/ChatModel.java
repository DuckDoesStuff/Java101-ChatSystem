package Chat;

public class ChatModel {

    String chatID; // Primary Key
    boolean isGroup; // for distinguishing between 1v1 and group chat
    String name;

    public ChatModel(String chatID, boolean isGroup, String name){
        this.chatID = chatID;
        this.isGroup = isGroup;
        this.name = name;
    }

    public String getChatID(){
        return chatID;
    }

    public boolean isGroup(){
        return isGroup;
    }

    public String getName(){
        return name;
    }
}
