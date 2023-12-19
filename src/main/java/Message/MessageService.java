package Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.time.LocalDateTime;

public class MessageService {
    Connection conn;
    //    TODO Put a sent msg to the msg db
    public boolean addMessage(int chatID, int userID, String content) {
        try {
            String sql = "INSERT INTO message (chatID, senderid, content, time) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chatID);
            stmt.setInt(2, userID);
            stmt.setString(3, content);
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            stmt.setTimestamp(4, timestamp);
            stmt.executeUpdate();
            conn.commit();
            return true;
        } catch (Exception e) {
            System.out.println("Error adding message");
            throw new RuntimeException(e);
        }
    }

    //    TODO Get all msg history from the db base on ChatID
    public ArrayList<MessageModel> getMessages(String chatID) {
        try {
            String sql = "SELECT * FROM message WHERE chatID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, chatID);
            ResultSet rs = stmt.executeQuery();
            ArrayList<MessageModel> messages = new ArrayList<>();
            while(rs.next()) {
                MessageModel message = new MessageModel(
                        rs.getInt("messageID"),
                        rs.getString("chatID"),
                        rs.getInt("userID"),
                        rs.getString("content"),
                        rs.getTimestamp("time").toLocalDateTime()
                );
                messages.add(message);
            }
            return messages;
        } catch (Exception e) {
            System.out.println("Error getting messages");
            throw new RuntimeException(e);
        }
    }
    public MessageService(Connection conn) {
        this.conn = conn;
    }

}
