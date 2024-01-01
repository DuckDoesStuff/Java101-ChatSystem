package view;

public class Group extends User{
    int chatID;
    public Group(int id, String name, String status, int chatID) {
        super(id, name, status);
        this.chatID = chatID;
    }

    public int getChatID(){
        return this.chatID;
    }
}
