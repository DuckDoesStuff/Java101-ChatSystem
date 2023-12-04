package Friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FriendRequestController {
    Connection conn;
    public FriendRequestController(Connection conn) {
        this.conn = conn;
    }

    public boolean sendRequest(String senderID, String receiverID) {
        try {
            //See if a user with the same username already exists
            String sql = "SELECT * FROM friendrequest WHERE senderID = ? AND receiverID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderID);
            stmt.setString(2, receiverID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                System.out.println("Friend request already exists");
                return false;
            }
            //Add the user to the database
            sql = "INSERT INTO friendrequest (senderID, receiverID, accepted) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderID);
            stmt.setString(2, receiverID);
            stmt.setBoolean(3, false);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error adding friend request");
            throw new RuntimeException(e);
        }
    }

    public boolean acceptRequest(String senderID, String receiverID) {
        try {
            //See if a request exists
            String sql = "SELECT * FROM friendrequest WHERE senderID = ? AND receiverID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderID);
            stmt.setString(2, receiverID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("Friend request does not exist");
                return false;
            }
            //Add the user to the database
            sql = "UPDATE friendrequest SET accepted = ? WHERE senderID = ? AND receiverID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, true);
            stmt.setString(2, senderID);
            stmt.setString(3, receiverID);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error accepting friend request");
            throw new RuntimeException(e);
        }
    }

    public boolean removeRequest(String senderID, String receiverID) {
        try {
            //See if there is a request with the sender-receiver ID pair
            String sql = "SELECT * FROM friendrequest WHERE senderID = ? AND receiverID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderID);
            stmt.setString(2, receiverID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                System.out.println("Friend request does not exist");
                return false;
            }

            sql = "DELETE FROM friendrequest WHERE senderID = ? AND receiverID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, senderID);
            stmt.setString(2, receiverID);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error removing friend request");
            throw new RuntimeException(e);
        }
    }
}
