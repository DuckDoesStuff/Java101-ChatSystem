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
    // Male is 1 Female is 0
    boolean gender;
    Timestamp first_joined;
    Timestamp online_since;
    int friends_count;
    int friends_of_friends_count;

    public UserModel(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserModel(int userID, String username, Timestamp first_joined, int friends_count, int friends_of_friends_count) {
        this.userID = userID;
        this.username = username;
        this.first_joined = first_joined;
        this.friends_count = friends_count;
        this.friends_of_friends_count = friends_of_friends_count;
    }

    public UserModel(int userID, String username, String firstName, String lastName, Timestamp online_since) {
        this.userID = userID;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.online_since = online_since;
    }

    public UserModel(int userID, String firstName, String lastName, String username, String password, String email, String address, Date dateOfBirth, boolean gender, Timestamp first_joined) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
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
    public String getFirstName(){return firstName; }

    public Timestamp getFirst_joined() {
        return first_joined;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String isGender() {
        if (gender) {
            return "Male";
        } else {
            return "Female";
        }
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String text) {
        this.username = text;
    }

    public void setDateOfBirth(Date date) {
        this.dateOfBirth = date;
    }

    public void setGender(boolean male) {
        this.gender = male;
    }

    public void setAddress(String text) {
        this.address = text;
    }

    public void setPassword(String text) {
        this.password = text;
    }

    public void setEmail(String text) {
        this.email = text;
    }

    public void setLastName(String text) {
        this.lastName = text;
    }

    public void setFirstName(String text) {
        this.firstName = text;
    }

    public int getFriendsCount() {
        return friends_count;
    }

    public int getFriendsOfFriendsCount() {
        return friends_of_friends_count;
    }

    public Timestamp getOnlineSince() {
        return online_since;
    }
}

