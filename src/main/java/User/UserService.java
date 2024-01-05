package User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class UserService {
    Connection conn;
    public UserService(Connection conn) {
        this.conn = conn;
    }

    public String registerUser(String username, String password, String email, String firstname, String lastname, String address, String dob, String gender) {
        try {
            //See if a user with the same username already exists
            String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                System.out.println("User already exists");
                return "User already exists";
            }
            //Add the user to the database
            UserModel user = new UserModel(username, password, email);
            sql = "INSERT INTO users (username, password, email, online_status, opened_time, first_joined, first_name, last_name, address, dateofbirth, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.username);
            stmt.setString(2, user.password);
            stmt.setString(3, user.email);
            stmt.setBoolean(4, true);
            stmt.setInt(5, 1);
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setString(7, firstname);
            stmt.setString(8, lastname);
            stmt.setString(9, address);
            Date utilDate = new SimpleDateFormat("yyyy/MM/dd").parse(dob);
            stmt.setDate(10, new java.sql.Date(utilDate.getTime()));
            stmt.setBoolean(11, gender.equals("true"));
            stmt.executeUpdate();
            conn.commit();

            //Log the user activity
            sql = "INSERT INTO LoginHistory (userID, timeLog) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, getUserIDFromUsername(username));
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            conn.commit();
            return "success";
        } catch (Exception e) {
            System.out.println("Error adding user");
            throw new RuntimeException(e);
        }
    }

    public String loginUser(String username, String password) {
        try {
            //See if a user with the same username already exists
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("User does not exist");
                return "User does not exist";
            }

            //Check if the password is correct
            String UserPassword = rs.getString("password");
            if(!password.equals(UserPassword)) {
                System.out.println("Incorrect password");
                return "Incorrect password";
            }

            sql = "UPDATE users SET online_status = ? WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setString(2, username);
            stmt.executeUpdate();
            conn.commit();

            //Log the user activity
            sql = "INSERT INTO LoginHistory (userID, timeLog) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, getUserIDFromUsername(username));
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            conn.commit();
            return "success";
        } catch (Exception e) {
            System.out.println("Error logging in user");
            throw new RuntimeException(e);
        }
    }

    public void logoutUser(String username) {
        try {
            String sql = "UPDATE users SET online_status = ?, last_joined = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, false);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, username);
            stmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            System.out.println("Error logging out user");
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(String username) {
        try {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            System.out.println("Error deleting user");
            throw new RuntimeException(e);
        }
    }

    public int getUserIDFromUsername(String username){
        try {
            String sql = "SELECT userID FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("User does not exist");
                return -1;
            }
            return rs.getInt("userID");
        } catch (Exception e) {
            System.out.println("Error getting userID");
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> findUserWithUsername(String userNameToFind, String userWhoFind) {
        ArrayList<String> userList = new ArrayList<>();
        try {
            String sql = "SELECT username FROM users WHERE username LIKE ? AND username != ?";
            String keyword = "%" + userNameToFind + "%";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
            stmt.setString(2, userWhoFind);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return null;
            }
            do {
                String username = rs.getString("username");
                //String firstName = rs.getString("firstName");
                //String lastName = rs.getString("lastName");
                //boolean gender = rs.getBoolean("gender");
                //UserModel user = new UserModel(username, firstName, lastName, gender);
                userList.add(username);
            } while(rs.next());
            return userList;
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
    }

    public boolean reportSpammer(int userID, int spammerID) {
        try {
            String sql = "INSERT INTO spammer (userID, spammerID) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setInt(2, spammerID);
            int rows = stmt.executeUpdate();
            conn.commit();
            return rows != 0;
        } catch (Exception e) {
            System.out.println("Error reporting spammer");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public boolean blockUser (int userID, int blockedID) {
        try {
            String sql = "INSERT INTO blocked (blockerID, blockedID) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setInt(2, blockedID);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error blocking user");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public String findUsernameWithUserID(int userID) {
        try {
            String sql = "SELECT username FROM users WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return null;
            }
            return rs.getString("username");
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
    }

    public Boolean getUserStatus(String username){
        try {
            String sql = "SELECT online_status FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return null;
            }
            return rs.getBoolean("online_status");
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
    }

    public boolean unblockUser(int userid, int receiverID) {
        try {
            String sql = "DELETE FROM blocked WHERE blockerID = ? AND blockedID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            stmt.setInt(2, receiverID);
            int rows = stmt.executeUpdate();
            conn.commit();

            return rows != 0;
        } catch (Exception e) {
            System.out.println("Error unblocking user");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public boolean blockStatus(int userid, int receiverID) {
        try {
            String sql = "SELECT * FROM blocked WHERE (blockerID = ? AND blockedID = ?) OR (blockerID = ? AND blockedID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            stmt.setInt(2, receiverID);
            stmt.setInt(3, receiverID);
            stmt.setInt(4, userid);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
    }

    // Admin
    public ArrayList<UserModel> getAllUser() {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<UserModel> getOnlineUsers() {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE online_status = true");
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public UserModel getUser(int userID) {
        UserModel user;
        try {
            String sql = "SELECT * FROM users WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return null;
            }
            user = new UserModel(
                    rs.getInt("userid"),rs.getString("first_name"),rs.getString("last_name"),
                    rs.getString("username"),rs.getString("password"),rs.getString("email"),
                    rs.getString("address"),rs.getDate("dateofbirth"),rs.getBoolean("gender"),
                    rs.getTimestamp("first_joined")
                    );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public ArrayList<UserModel> filterUser(String keyword) {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            String sql = "SELECT * FROM users WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR email LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");
            stmt.setString(4, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<UserModel> sortUserByFirstName() {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
            UserModel temp;
            for (int i = 0; i < userList.size(); i++) {
                for (int j = i + 1; j < userList.size(); j++) {
                    String name1 = userList.get(i).firstName;
                    String name2 = userList.get(j).firstName;
                    if (name1.compareTo(name2) > 0) {
                        temp = userList.get(i);
                        userList.set(i, userList.get(j));
                        userList.set(j, temp);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<UserModel> sortUserByFirstJoined() {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
            UserModel temp;
            for (int i = 0; i < userList.size(); i++) {
                for (int j = i + 1; j < userList.size(); j++) {
                    Timestamp time1 = userList.get(i).first_joined;
                    Timestamp time2 = userList.get(j).first_joined;
                    if (time1.compareTo(time2) > 0) {
                        temp = userList.get(i);
                        userList.set(i, userList.get(j));
                        userList.set(j, temp);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public String addByAdmin(String first_name, String last_name, String username, String password, String email, String address, Date dateOfBirth, boolean gender) {
        try {
            String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return "A user with the same username or email has already exists";
            }

            sql = "INSERT INTO users (first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined) VALUES (?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,first_name);
            stmt.setString(2,last_name);
            stmt.setString(3,username);
            stmt.setString(4,password);
            stmt.setString(5,email);
            stmt.setString(6,address);
            stmt.setDate(7,new java.sql.Date(dateOfBirth.getTime()));
            stmt.setBoolean(8,gender);
            stmt.setTimestamp(9,new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
            conn.commit();
            return "success";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editByAdmin(UserModel user) {
        try {
            String sql = "SELECT * FROM users WHERE (username = ? OR email = ?) AND userID != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.username);
            stmt.setString(2, user.email);
            stmt.setInt(3, user.userID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return "A user with the same username or email has already exists";
            }

            sql = "UPDATE users " + "SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, address = ?, dateOfBirth = ?, gender = ? WHERE userID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.firstName);
            stmt.setString(2, user.lastName);
            stmt.setString(3, user.username);
            stmt.setString(4, user.password);
            stmt.setString(5, user.email);
            stmt.setString(6, user.address);
            stmt.setDate(7, new java.sql.Date(user.dateOfBirth.getTime()));
            stmt.setBoolean(8, user.gender);
            stmt.setInt(9, user.userID);
            stmt.executeUpdate();
            conn.commit();
            return "success";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByAdmin(int userID) {
        try {
            String sql =
                    "DELETE FROM username_userid WHERE userID = ?;" +
                    "DELETE FROM friendlist WHERE userID = ? OR friendID = ?;" +
                    "DELETE FROM blocked WHERE blockerID = ? OR blockedID = ?;" +
                    "DELETE FROM spammer WHERE userID = ? OR spammerID = ?;" +
                    "DELETE FROM friendrequest WHERE senderID = ? OR receiverID = ?;" +
                    "DELETE FROM reports WHERE userID = ? OR spammerID = ?;" +
                    "DELETE FROM loginhistory WHERE userID = ?;" +
                    "DELETE FROM message WHERE senderID = ?;" +
                    "DELETE FROM chatmember WHERE userID = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for(int i = 1; i <= 14; i++) {
                stmt.setInt(i, userID);
            }
            stmt.executeUpdate();

            sql = "SELECT chatID FROM groupadmin WHERE userid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int chatID = rs.getInt("chatID");
                sql = "SELECT COUNT(*) AS count FROM groupadmin WHERE chatID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, chatID);
                rs = stmt.executeQuery();
                if(rs.next()) {
                    int count = rs.getInt("count");
                    // The only admin, disband the group
                    if(count == 1) {
                        deleteGroupChat(chatID);
                    }
                }
            }

            sql = "DELETE FROM groupadmin WHERE userID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.executeUpdate();

            sql = "DELETE FROM users WHERE userId = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteGroupChat(int chatID) {
        try {
            String sql = "DELETE FROM chat WHERE chatID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            stmt.executeUpdate();

            sql = "DELETE FROM chatmember WHERE chatID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean blockByAdmin (int userID) {
        try {
            String sql = "UPDATE users " + "SET banned = true WHERE userID LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error banned user");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public boolean unblockByAdmin(String username) {
        try {
            String sql = "UPDATE users " + "SET banned = false WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            conn.commit();
            return rows != 0;
        } catch (Exception e) {
            System.out.println("Error banned user");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public ArrayList<HistoryLogin> getUserLoginHistory(int userID) {
        ArrayList<HistoryLogin> userList = new ArrayList<HistoryLogin>();
        try {
            String sql = ("SELECT * FROM LoginHistory WHERE userID = ?");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userid = rs.getInt("userID");
                Timestamp timeLog = rs.getTimestamp("timeLog");
                HistoryLogin temp = new HistoryLogin(userid, timeLog);
                userList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<String> getUserFriendList(int userid) {
        ArrayList<String> friends = new ArrayList<>();
        try {
            String sql =
                    "SELECT UU.username as friendname " +
                    "FROM friendlist AS FL " +
                    "JOIN username_userid AS UU ON FL.friendID = UU.userID " +
                    "WHERE FL.userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(rs.getString("friendname"));
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return friends;
    }

    public ArrayList<HistoryLogin> getAllLoginHistory() {
        ArrayList<HistoryLogin> userList = new ArrayList<HistoryLogin>();
        try {
            String sql = ("SELECT * FROM LoginHistory");
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userid = rs.getInt("userID");
                Timestamp timeLog = rs.getTimestamp("timeLog");
                HistoryLogin temp = new HistoryLogin(userid, timeLog);
                userList.add(temp);
            }

            for (int i = 0; i < userList.size(); i++) {
                sql = ("SELECT * FROM users WHERE userID LIKE ?");
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userList.get(i).userID);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    String first_name = rs.getString("first_name");
                    String last_name = rs.getString("last_name");
                    userList.get(i).userName = username;
                    userList.get(i).firstName = first_name;
                    userList.get(i).lastName = last_name;
                }
            }
            HistoryLogin temp;
            for (int i = 0; i < userList.size(); i++) {
                for (int j = i + 1; j < userList.size(); j++) {
                    Timestamp time1 = userList.get(i).timeLog;
                    Timestamp time2 = userList.get(j).timeLog;
                    if (time1.compareTo(time2) > 0) {
                        temp = userList.get(i);
                        userList.set(i, userList.get(j));
                        userList.set(j, temp);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<GroupChat> getAllGroupChat(String sortBy) {
        ArrayList<GroupChat> groupList = new ArrayList<GroupChat>();
        try {
            String sql = "SELECT * FROM Chat WHERE isGroup = true";
            if (sortBy.equals("nameasc")){
                sql += " ORDER BY name ASC";
            } else if (sortBy.equals("namedes")){
                sql += " ORDER BY name DESC";
            } else if (sortBy.equals("timeasc")){
                sql += " ORDER BY createdTime ASC";
            } else if (sortBy.equals("timedes")){
                sql += " ORDER BY createdTime DESC";
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int chatID = rs.getInt("chatID");
                String name = rs.getString("name");
                Timestamp createdTime = rs.getTimestamp("createdTime");
                GroupChat temp = new GroupChat(chatID, name, createdTime);
                groupList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groupList;
    }

    public ArrayList<GroupChat> sortGroupChat() {
        ArrayList<GroupChat> groupList = new ArrayList<GroupChat>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Chat WHERE isGroup = true");
            while (rs.next()) {
                int chatID = rs.getInt("chatID");
                String name = rs.getString("name");
                GroupChat temp = new GroupChat(chatID, name);
                groupList.add(temp);
            }
            GroupChat temp;
            for (int i = 0; i < groupList.size(); i++) {
                for (int j = i + 1; j < groupList.size(); j++) {
                    String name1 = groupList.get(i).name;
                    String name2 = groupList.get(j).name;
                    if (name1.compareTo(name2) > 0) {
                        temp = groupList.get(i);
                        groupList.set(i, groupList.get(j));
                        groupList.set(j, temp);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groupList;
    }

    public ArrayList<GroupChat> filterGroupChat(String keyword) {
        ArrayList<GroupChat> groupList = new ArrayList<GroupChat>();
        try {
            String sql = ("SELECT * FROM Chat WHERE isGroup = true AND name LIKE ?");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int chatID = rs.getInt("chatID");
                String name = rs.getString("name");

                Timestamp createdTime = rs.getTimestamp("createdTime");
                GroupChat temp = new GroupChat(chatID, name, createdTime);
                groupList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groupList;
    }
    //xem ds người dùng đăng kí mới, isByName = true (sx theo tên)/ false (sx theo thời gian tạo)
    public ArrayList<UserModel> newUserWithSort(Date dateStart, Date dateEnd, boolean isByName) {
        ArrayList<UserModel> newUsers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users WHERE first_joined BETWEEN ? AND ?";
            if (isByName){
                sql += " ORDER BY username ASC";
            }
            else {
                sql += " ORDER BY username DESC ";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(dateStart.getTime()));
            stmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                newUsers.add(user);
            }


        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return newUsers;
    }

    //xem ds người dùng đăng kí mới, lọc theo tên
    public ArrayList<UserModel> newUserByName(Date dateStart, Date dateEnd, String name) {
        ArrayList<UserModel> newUsers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users " +
                    "WHERE (first_joined BETWEEN ? AND ?) " +
                    "AND username LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(dateStart.getTime()));
            stmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
            stmt.setString(3, "%" + name + "%");
            //stmt.setString(4, "%" + name.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                newUsers.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return newUsers;
    }

    //Số lượng người đăng kí mới theo năm
    public int [] numberOfNewUserByYear(int year){
        int [] numberOfNewUser = new int [12];
        try {
            String sql =
                    "SELECT EXTRACT(MONTH FROM first_joined) AS month, COUNT(*) AS count " +
                    "FROM users " +
                    "WHERE EXTRACT(YEAR FROM first_joined) = ? " +
                    "GROUP BY month " +
                    "ORDER BY month";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                numberOfNewUser[month - 1] = count;
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return numberOfNewUser;
    }

    //7. Xem danh sách người dùng và số lượng bạn bè (1 cột bạn bè trực tiếp, 1 cột tính luôn số lượng bạn của bạn)

    //Số lượng bạn bè trực tiếp
    public int numberOfDirectFriends(int userid){
        int ans = 0;
        try {
            String sql =  "SELECT COUNT(DISTINCT friendid) AS count " +
                    "FROM friendlist WHERE userid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                ans = rs.getInt("count");
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return ans;
    }
    //Số lượng bạn + bạn của bạn
    public int numberOfDirectAndIndirectFriends(int userid){
        int ans = 0;
        try {
            ArrayList<Integer> userIds = new ArrayList<>();
            String sql =  "SELECT DISTINCT friendid " +
                    "FROM friendlist WHERE userid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                ans = rs.getInt("friendid");
                if (ans != userid && !userIds.contains(ans))
                    userIds.add(ans);
            }
            sql =  "SELECT DISTINCT f.friendid " +
                    "FROM friendlist AS u " +
                    "JOIN friendlist AS f ON u.friendid = f.userid " +
                    "WHERE u.userid = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()){
                ans = rs.getInt("friendid");
                if (ans != userid && !userIds.contains(ans))
                    userIds.add(ans);
            }
            ans = userIds.size();
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return ans;
    }
    //7a. Sắp xếp theo tên (isByName = true)/ thời gian tạo (isByName = false)
    public ArrayList<UserModel> userListWithSort(boolean isByName){
        ArrayList<UserModel> users = new ArrayList<>();
        try {
            String sql;
            if (isByName){
                sql = "SELECT * FROM users " +
                        "ORDER BY first_name, last_name";
            }
            else {
                sql = "SELECT * FROM users " +
                        "ORDER BY first_joined DESC";
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return users;
    }
    //7b. Lọc theo tên
    public ArrayList<UserModel> userListByName(String name) {
        ArrayList<UserModel> userList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users " +
                    "WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + name.toLowerCase() + "%");
            stmt.setString(2, "%" + name.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return userList;
    }
    //7c. c. Lọc theo số lượng bạn trực tiếp (bằng - type=0, nhỏ hơn - type=-1, lớn hơn 1 số được nhập - type=1)
    public ArrayList<UserModel> userListByNumberOfDirectFriends(int num, int type) {
        ArrayList<UserModel> userList = new ArrayList<>();
        try {
            String sql =    "SELECT U.*, COUNT(FL.friendid) AS num_friends, COUNT(FL2.friendid) as num_friends_of_friends " +
                            "FROM users as U " +
                            "JOIN friendlist as FL ON U.userID = FL.userID " +
                            "JOIN friendlist as FL2 ON FL.friendid = FL2.userid " +
                            "GROUP BY U.userID";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String username = rs.getString("username");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                int num_friends = rs.getInt("num_friends");
                int num_friends_of_friends = rs.getInt("num_friends_of_friends");
                UserModel user = new UserModel(userID, username, first_joined, num_friends, num_friends_of_friends);
                userList.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return userList;
    }

    //8. Xem danh sách người dùng hoạt động: chọn khoảng thời gian, hiện ra danh sách người dùng có
    //hoạt động và các số liệu (mở ứng dụng, chat với bao nhiêu người, chat bao nhiêu nhóm)

    //Số lần mở ứng dụng
    public int numOpenning (int userid){
        int ans = 0;
        try {
            String sql =  "SELECT opened_time " +
                    "FROM users WHERE userid = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                ans = rs.getInt("opened_time");
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return ans;
    }
    //Chat với bao nhiêu người
    public int numChatPersons (int userid){
        int ans = 0;
        try {
            String sql =  "SELECT COUNT(DISTINCT c.chatID) AS count " +
                    "FROM Message AS m " +
                    "JOIN Chat AS c ON c.chatID = m.chatID " +
                    " WHERE senderID = ? AND isGroup = false";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                ans = rs.getInt("count");
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return ans;
    }

    public ArrayList<UserModel> MemberGroupChat(String groupName, Timestamp createdTime) {
        ArrayList<Integer> memberList = new ArrayList<Integer>();
        ArrayList<UserModel> members = new ArrayList<UserModel>();
        try {
            int chatID=-1;
            String sqlchatID = "SELECT * FROM Chat WHERE name = ? AND createdTime = ?";
            PreparedStatement stmtchatID = conn.prepareStatement(sqlchatID);
            stmtchatID.setString(1, groupName);
            stmtchatID.setTimestamp(2, createdTime);
            ResultSet rschatID = stmtchatID.executeQuery();
            if(rschatID.next())
                chatID = rschatID.getInt("chatID");
            String sql = "SELECT * FROM ChatMember WHERE chatID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                memberList.add(userID);
            }

            for (int i = 0; i < memberList.size(); i++) {
                sql = ("SELECT * FROM users WHERE userID = ?");
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, memberList.get(i));
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int userID = rs.getInt("userID");
                    String first_name = rs.getString("first_name");
                    String last_name = rs.getString("last_name");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    Date dateOfBirth = rs.getDate("dateOfBirth");
                    Boolean gender = rs.getBoolean("gender");
                    Timestamp first_joined = rs.getTimestamp("first_joined");
                    UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                    members.add(temp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return members;
    }

    public ArrayList<UserModel> AdminGroupChat(String name, Timestamp createdTime) {
        int chatID=-1;
        ArrayList<Integer> memberList = new ArrayList<Integer>();
        ArrayList<UserModel> members = new ArrayList<UserModel>();
        try {
            String sqlchatID = "SELECT * FROM Chat WHERE name = ? AND createdTime = ?";
            PreparedStatement stmtchatID = conn.prepareStatement(sqlchatID);
            stmtchatID.setString(1, name);
            stmtchatID.setTimestamp(2, createdTime);
            ResultSet rschatID = stmtchatID.executeQuery();
            if(rschatID.next())
                chatID = rschatID.getInt("chatID");
            String sql = "SELECT * FROM groupAdmin WHERE chatID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                memberList.add(userID);
            }

            for (int i = 0; i < memberList.size(); i++) {
                sql = ("SELECT * FROM users WHERE userID = ?");
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, memberList.get(i));
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int userID = rs.getInt("userID");
                    String first_name = rs.getString("first_name");
                    String last_name = rs.getString("last_name");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    Date dateOfBirth = rs.getDate("dateOfBirth");
                    Boolean gender = rs.getBoolean("gender");
                    Timestamp first_joined = rs.getTimestamp("first_joined");
                    UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                    members.add(temp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return members;
    }

    //TODO
    public ArrayList<Spam> getAllSpam(String sortBy) {
        ArrayList<Spam> spamList = new ArrayList<Spam>();
        try {
            String sql = "SELECT s.userID as userID, s.spammerID as spammerID, s.time as time, u.username as username, u2.username as usernamespammer FROM spammer s " +
                    "INNER JOIN users u ON s.userID = u.userID " +
                    "INNER JOIN users u2 ON s.spammerID = u2.userID";
            if (sortBy.equals("nameasc")){
                sql += " ORDER BY username ASC";
            } else if (sortBy.equals("namedes")){
                sql += " ORDER BY username DESC";
            } else if (sortBy.equals("timeasc")){
                sql += " ORDER BY time ASC";
            } else if (sortBy.equals("timedes")){
                sql += " ORDER BY time DESC";
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                int spammerID = rs.getInt("spammerID");
                String userName = rs.getString("username");
                String spammerName = rs.getString("usernamespammer");
                Timestamp timeSpam = rs.getTimestamp("time");
                Spam temp = new Spam(userID, spammerID, userName, spammerName, timeSpam);
                spamList.add(temp);
            }

//            for (int i = 0; i < spamList.size(); i++) {
//                sql = ("SELECT * FROM users WHERE userID LIKE ?");
//                stmt = conn.prepareStatement(sql);
//                stmt.setInt(1, spamList.get(i).userID);
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    spamList.get(i).userName = rs.getString("username");
//                }
//            }
//
//            for (int i = 0; i < spamList.size(); i++) {
//                sql = ("SELECT * FROM users WHERE userID LIKE ?");
//                stmt = conn.prepareStatement(sql);
//                stmt.setInt(1, spamList.get(i).spammerID);
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    spamList.get(i).spammerName = rs.getString("username");
//                }
//            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spamList;
    }
    //TODO
    public ArrayList<Spam> sortSpamByName() {
        ArrayList<Spam> spamList = new ArrayList<Spam>();
        try {
            String sql = "SELECT s.userID, s.spammerID, s.timeSpam, u.username, u2.username * FROM spammer s " +
                    "INNER JOIN users u ON s.userID = u.userID " +
                    "INNER JOIN users u2 ON s.spammerID = u2.userID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("s.userID");
                int spammerID = rs.getInt("s.spammerID");
                String userName = rs.getString("u.username");
                String spammerName = rs.getString("u2.username");
                Timestamp timeSpam = rs.getTimestamp("timeSpam");
                Spam temp = new Spam(userID, spammerID, userName, spammerName, timeSpam);
                spamList.add(temp);
            }

//            for (int i = 0; i < spamList.size(); i++) {
//                sql = ("SELECT * FROM users WHERE userID LIKE ?");
//                stmt = conn.prepareStatement(sql);
//                stmt.setInt(1, spamList.get(i).userID);
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    spamList.get(i).userName = rs.getString("username");
//                }
//            }
//
//            for (int i = 0; i < spamList.size(); i++) {
//                sql = ("SELECT * FROM users WHERE userID LIKE ?");
//                stmt = conn.prepareStatement(sql);
//                stmt.setInt(1, spamList.get(i).spammerID);
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    spamList.get(i).spammerName = rs.getString("username");
//                }
//            }

            Spam temp;
            for (int i = 0; i < spamList.size(); i++) {
                for (int j = i + 1; j < spamList.size(); j++) {
                    String name1 = spamList.get(i).userName;
                    String name2 = spamList.get(j).userName;
                    if (name1.compareTo(name2) > 0) {
                        temp = spamList.get(i);
                        spamList.set(i, spamList.get(j));
                        spamList.set(j, temp);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spamList;
    }
    //TODO
    public ArrayList<Spam> filterSpamByTime(Timestamp from, Timestamp to){
        ArrayList<Spam> spamList = new ArrayList<Spam>();
        try {
            String sql = "SELECT s.userID as userID, s.spammerID as spammerID, s.time as time, u.username as username, u2.username as spammer FROM spammer s " +
                    "INNER JOIN users u ON s.userID = u.userID " +
                    "INNER JOIN users u2 ON s.spammerID = u2.userID " +
                    "WHERE s.time BETWEEN ? AND ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, from);
            stmt.setTimestamp(2, to);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                int spammerID = rs.getInt("spammerID");
                String userName = rs.getString("username");
                String spammerName = rs.getString("spammer");
                Timestamp timeSpam = rs.getTimestamp("time");
                Spam temp = new Spam(userID, spammerID, userName, spammerName, timeSpam);
                spamList.add(temp);
            }
        } catch (SQLException e){

        }
        return spamList;
    }
    //TODO
    public ArrayList<Spam> filterSpam(String keyword) {
        ArrayList<Spam> spamList = new ArrayList<Spam>();
        try {
            String sql = "SELECT s.userID as userID, s.spammerID as spammerID, s.time as time, u.username as username, u2.username as spammer FROM spammer s " +
                    "INNER JOIN users u ON s.userID = u.userID " +
                    "INNER JOIN users u2 ON s.spammerID = u2.userID " +
                    "WHERE u.username LIKE ?";;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            //stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                int spammerID = rs.getInt("spammerID");
                String userName = rs.getString("username");
                String spammerName = rs.getString("spammer");
                Timestamp timeSpam = rs.getTimestamp("time");
                Spam temp = new Spam(userID, spammerID, userName, spammerName, timeSpam);
                spamList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spamList;
    }

    public boolean banUser(String username){
        try {
            String sql = "UPDATE users SET banned = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setString(2, username);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error banning user");
            return false;
            //throw new RuntimeException(e);
        }
    }
    //Chat với bao nhiêu nhóm
    public int numChatGroups (int userid){
        int ans = 0;
        try {
            String sql =  "SELECT COUNT(DISTINCT c.chatID) AS count " +
                    "FROM Message AS m " +
                    "JOIN Chat AS c ON c.chatID = m.chatID " +
                    " WHERE senderID = ? AND isGroup = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                ans = rs.getInt("count");
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return ans;
    }

    //8a.  Sắp xếp theo tên (isByName = true)/ thời gian tạo (isByName = false)
    public ArrayList<UserModel> activeUserWithSort(Date dateStart, Date dateEnd, boolean isByName) {
        ArrayList<UserModel> activeUsers = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT u.* " +
                    "FROM LoginHistory AS lh " +
                    "JOIN users AS u ON lh.userID = u.userID " +
                    "WHERE timeLog BETWEEN ? AND ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(dateStart.getTime()));
            stmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                activeUsers.add(user);
            }
            if (isByName) {
                Collections.sort(activeUsers, Comparator.comparing(UserModel::getFirstName));
            }
            else{
                Collections.sort(activeUsers, Comparator.comparing(UserModel::getFirst_joined));
            }
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return activeUsers;
    }
    //8b. Lọc theo tên
    public ArrayList<UserModel> activeUserByName(Date dateStart, Date dateEnd, String name) {
        ArrayList<UserModel> activeUsers = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT u.* " +
                    "FROM LoginHistory AS lh " +
                    "JOIN users AS u ON lh.userID = u.userID " +
                    "WHERE timeLog BETWEEN ? AND ? " +
                    "AND (LOWER(u.first_name) LIKE ? OR LOWER(u.last_name) LIKE ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(dateStart.getTime()));
            stmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
            stmt.setString(3, "%" + name.toLowerCase() + "%");
            stmt.setString(4, "%" + name.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                activeUsers.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return activeUsers;
    }
    //8c. Lọc theo số lượng hoạt động (bằng - type=0, nhỏ hơn - type=-1, lớn hơn 1 số được nhập - type=1)
    public ArrayList<UserModel> activeUsersByNumberOfActivities(Date dateStart, Date dateEnd, int num, int type) {
        ArrayList<UserModel> activeList = new ArrayList<>();
        try {
            ArrayList<UserModel> tmp = new ArrayList<>();
            String sql = "SELECT DISTINCT u.* " +
                    "FROM LoginHistory AS lh " +
                    "JOIN users AS u ON lh.userID = u.userID " +
                    "WHERE timeLog BETWEEN ? AND ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, new Timestamp(dateStart.getTime()));
            stmt.setTimestamp(2, new Timestamp(dateEnd.getTime()));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int userID = rs.getInt("userID");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Date dateOfBirth = rs.getDate("dateOfBirth");
                boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel user = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                tmp.add(user);
            }
            for(int i = 0; i < tmp.size(); i++){
                if (type == 0 && numOpenning(tmp.get(i).getUserID()) == num){
                    activeList.add(tmp.get(i));
                }
                else if (type == 1 && numOpenning(tmp.get(i).getUserID()) > num){
                    activeList.add(tmp.get(i));
                }
                else if (type == -1 && numOpenning(tmp.get(i).getUserID()) < num){
                    activeList.add(tmp.get(i));
                }
            }
            tmp.clear();
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
        return activeList;
    }

    //9. Biểu đồ số lượng người hoạt động theo năm: chọn năm, vẽ biểu đồ với trục hoành là tháng, trục tung là số lượng người có mở ứng dụng.
    //Số lượng người hoạt động theo năm
    public int [] numberOfActiveUserByYear(int year){
        int [] numberOfNewUser = new int [12];
        try {

            String sql =
                    "SELECT EXTRACT(MONTH FROM timeLog) AS month, COUNT(DISTINCT userid) AS count " +
                            "FROM loginhistory " +
                            "WHERE EXTRACT(YEAR FROM timeLog) = ? " +
                            "GROUP BY month " +
                            "ORDER BY month";;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                numberOfNewUser[month - 1] = count;
            }
        } catch (Exception e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
        return numberOfNewUser;
    }

    public static void main(String[] args) throws ParseException {
//        DB db = new DB();
//        new UserService(db.getConnection());
//        db.closeConnection();
    }
}
