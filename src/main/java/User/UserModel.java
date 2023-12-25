package User;

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

    public int getUserID() {
        return userID;
    }
}
