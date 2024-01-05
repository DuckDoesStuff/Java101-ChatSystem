package User;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public ArrayList<String> findUserWithUsername(String userNameToFind) {
        ArrayList<String> userList = new ArrayList<>();
        try {
            String sql = "SELECT username FROM users WHERE username LIKE ?";
            String keyword = "%" + userNameToFind + "%";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keyword);
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
                Boolean gender = rs.getBoolean("gender");
                Timestamp first_joined = rs.getTimestamp("first_joined");
                UserModel temp = new UserModel(userID, first_name, last_name, username, password, email, address, dateOfBirth, gender, first_joined);
                userList.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<UserModel> filterUser(String keyword) {
        ArrayList<UserModel> userList = new ArrayList<UserModel>();
        try {
            String sql = "SELECT * FROM users WHERE first_name LIKE ? OR last_name LIKE ? OR username LIKE ? OR online_status LIKE ?";
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
                Boolean gender = rs.getBoolean("gender");
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
                Boolean gender = rs.getBoolean("gender");
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
                Boolean gender = rs.getBoolean("gender");
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

    public void addByAdmin(int userID, String first_name, String last_name, String username, String password, String email, String address, Date dateOfBirth, boolean gender) {
        try {
            String sql = "INSERT INTO users (userID, first_name, last_name, username, password, email, address, dateOfBirth, gender) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,userID);
            stmt.setString(2,first_name);
            stmt.setString(3,last_name);
            stmt.setString(4,username);
            stmt.setString(5,password);
            stmt.setString(6,email);
            stmt.setString(7,address);
            stmt.setDate(8,new java.sql.Date(dateOfBirth.getTime()));
            stmt.setBoolean(9,gender);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editByAdmin(int userID, String first_name, String last_name, String username, String password, String email, String address, Date dateOfBirth, boolean gender) {
        try {
            String sql = "UPDATE users " + "SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?, address = ?, dateOfBirth = ?, gender = ? WHERE userID LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + first_name + "%");
            stmt.setString(2, "%" + last_name + "%");
            stmt.setString(3, "%" + username + "%");
            stmt.setString(4, "%" + password + "%");
            stmt.setString(5, "%" + email + "%");
            stmt.setString(6, "%" + address + "%");
            stmt.setDate(7, (java.sql.Date) dateOfBirth);
            stmt.setBoolean(8, gender);
            stmt.setInt(9, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByAdmin(String username) {
        try {
            String sql = "DELETE FROM users " + "WHERE username LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + username + "%");
            stmt.executeUpdate();
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

    public boolean unblockByAdmin(int userID) {
        try {
            String sql = "UPDATE users " + "SET banned = false WHERE userID LIKE ?";
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

    public ArrayList<HistoryLogin> getUserLoginHistory(int userID) {
        ArrayList<HistoryLogin> userList = new ArrayList<HistoryLogin>();
        try {
            String sql = ("SELECT * FROM LoginHistory WHERE userID LIKE ?");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public ArrayList<UserModel> getUserFriendList(int userid) {
        ArrayList<UserModel> friends = new ArrayList<UserModel>();
        ArrayList<Integer> friendList = new ArrayList<Integer>();
        try {
            String sql = ("SELECT friendID FROM friendlist WHERE userID LIKE ?");
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int friendID = rs.getInt("friendID");
                friendList.add(friendID);
            }

            sql = "SELECT * FROM users WHERE userID LIKE ?";
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < friendList.size(); i++) {
                stmt.setString(1, "%" + friendList.get(i) + "%");
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
                    friends.add(temp);
                }
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
    public ArrayList<Spam> getAllSpam() {
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
    public ArrayList<Spam> sortSpamByTime() {
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
                    Timestamp time1 = spamList.get(i).timeSpam;
                    Timestamp time2 = spamList.get(j).timeSpam;
                    if (time1.compareTo(time2) > 0) {
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
//    public ArrayList<Spam> filterSpam(String keyword) {
//        ArrayList<Spam> spamList = new ArrayList<Spam>();
//        try {
//            String sql = "SELECT s.userID, s.spammerID, s.timeSpam, u.username, u2.username * FROM spammer s " +
//                    "INNER JOIN users u ON s.userID = u.userID " +
//                    "INNER JOIN users u2 ON s.spammerID = u2.userID " +
//                    "WHERE u.username LIKE ? OR s.timeSpam LIKE ?";;
//            PreparedStatement stmt = conn.prepareStatement(sql);
//            stmt.setString(1, "%" + keyword + "%");
//            stmt.setString(2, "%" + keyword + "%");
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                int userID = rs.getInt("userID");
//                int spammerID = rs.getInt("spammerID");
//                String userName = rs.getString("u.username");
//                String spammerName = rs.getString("u2.username");
//                Timestamp timeSpam = rs.getTimestamp("timeSpam");
//                Spam temp = new Spam(userID, spammerID, userName, spammerName, timeSpam);
//                spamList.add(temp);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return spamList;
//    }

    public static void main(String[] args) {
    }
}
