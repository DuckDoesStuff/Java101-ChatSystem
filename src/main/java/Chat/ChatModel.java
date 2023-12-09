package Chat;

public class ChatModel {

    int chatID; // Primary Key
    boolean isGroup; // for distinguishing between 1v1 and group chat
    String name;

    public ChatModel(boolean isGroup, String name){
        //this.chatID = chatID;
        this.isGroup = isGroup;
        this.name = name;
    }

    public ChatModel(int chatID, boolean isGroup, String name){
        this.chatID = chatID;
        this.isGroup = isGroup;
        this.name = name;
    }

    public void setChatID(int chatID){
        this.chatID = chatID;
    }
    public int getChatID(){
        return chatID;
    }

    public boolean isGroup(){
        return isGroup;
    }

    public String getName(){
        return name;
    }
}
