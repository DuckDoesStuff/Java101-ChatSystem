package ChatMember;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ChatMemberService {
    Connection conn;

    //    TODO ChatSerivce constructor (of 1v1 chat and group chat)
    public ChatMemberService(Connection conn) {
        this.conn = conn;
    }

    public int addChatMember(int chatID, int userID) {
        try {
            String sql = "INSERT INTO chatmember (chatID, userID) VALUES (?, ?) RETURNING chatID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            stmt.setInt(2, userID);
            ResultSet rs = stmt.executeQuery();
            conn.commit();
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            System.out.println("Error adding chat member");
            throw new RuntimeException(e);
        }
    }


    public ArrayList<String> getGroupMembers(int chatID) {
        try {
            String sql = "SELECT username FROM users WHERE userID IN (SELECT userID FROM chatmember WHERE chatID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> members = new ArrayList<>();
            while(rs.next()) {
                members.add(rs.getString("username"));
            }
            return members;
        } catch (Exception e) {
            System.out.println("Error getting group members");
            throw new RuntimeException(e);
        }
    }

    public boolean deleteChatMember(int userIDFromUsername, int userIDFromUsername1) {
        try {
            String sql = "DELETE FROM chatmember WHERE userID = ? AND chatID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userIDFromUsername);
            stmt.setInt(2, userIDFromUsername1);
            int rows = stmt.executeUpdate();
            conn.commit();
            return rows != 0;
        } catch (Exception e) {
            System.out.println("Error deleting chat member");
            throw new RuntimeException(e);
        }

    }
}
