package User;

import java.sql.Timestamp;
import java.util.Date;

public class UserModel {
    int userID;
    String firstName;
    String lastName;
    String username;
    String password;
    String email;
    String address;
    Date dateOfBirth;
    // Male is 0 Female is 1
    boolean gender;
    Timestamp first_joined;

    public UserModel(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserModel(String username, String firstName, String lastName, boolean gender) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public UserModel(int userID, String firstName, String lastName, String username, String password, String email, Date dateOfBirth, boolean gender, Timestamp first_joined) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.first_joined = first_joined;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}

