package DataStructure;

import java.util.ArrayList;

public class UserInfo {
    public ArrayList<String> friendList = new ArrayList<>();
    String username;
//    String password;

    public UserInfo(String username) {
        this.username = username;
//        this.password = password;

    }

    public void addFriend(String friend) {
        friendList.add(friend);
    }


    public void setName(String username) {
        this.username = username;
    }
}
