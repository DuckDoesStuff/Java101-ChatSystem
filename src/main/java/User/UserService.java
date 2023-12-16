package User;

import java.sql.*;
import java.util.ArrayList;

public class UserService {
    Connection conn;
    public UserService(Connection conn) {
        this.conn = conn;
    }

    public String registerUser(String username, String password, String email) {
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
            sql = "INSERT INTO users (username, password, email, online_status, opened_time, first_joined) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.username);
            stmt.setString(2, user.password);
            stmt.setString(3, user.email);
            stmt.setBoolean(4, true);
            stmt.setInt(5, 1);
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
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

    public ArrayList<UserModel> findUserWithUsername(String userNameToFind) {
        ArrayList<UserModel> userList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userNameToFind);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("No such user");
                return null;
            }
            do {
                String username = rs.getString("username");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                boolean gender = rs.getBoolean("gender");
                UserModel user = new UserModel(username, firstName, lastName, gender);
                userList.add(user);
            } while(rs.next());
            return userList;
        } catch (Exception e) {
            System.out.println("Error finding user");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }
}
