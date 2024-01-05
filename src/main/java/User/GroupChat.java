package User;
import java.sql.Timestamp;
public class GroupChat {
    int chatID;
    String name;

    Timestamp createdTime;


    public GroupChat(int chatID, String name) {
        this.chatID = chatID;
        this.name = name;

    }

    public GroupChat(int chatID, String name, Timestamp createdTime) {
        this.chatID = chatID;
        this.name = name;
        this.createdTime = createdTime;
    }

    public String getName() {
        return name;
    }

    public Timestamp getTimeCreated() {
        return createdTime;
    }
}

