package User;

import java.sql.Timestamp;

public class Spam {
    int userID;
    int spammerID;
    String userName;
    String spammerName;
    Timestamp timeSpam;

    public Spam(int userID, int spammerID, Timestamp timeSpam) {
        this.userID = userID;
        this.spammerID = spammerID;
        this.timeSpam = timeSpam;
    }
    public Spam(int userID, int spammerID, String userName, String spammerName, Timestamp timeSpam) {
        this.userID = userID;
        this.spammerID = spammerID;
        this.userName = userName;
        this.spammerName = spammerName;
        this.timeSpam = timeSpam;
    }
}
