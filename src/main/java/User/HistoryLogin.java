package User;

import java.sql.Timestamp;

public class HistoryLogin {
    int userID;
    Timestamp timeLog;
    String userName;
    String firstName;
    String lastName;

    public HistoryLogin(int userID, Timestamp timeLog) {
        this.userID = userID;
        this.timeLog = timeLog;
    }
    public HistoryLogin(int userID, Timestamp timeLog, String userName, String firstName, String lastName) {
        this.userID = userID;
        this.timeLog = timeLog;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getTimeLog() {
        return timeLog.toString();
    }
}

