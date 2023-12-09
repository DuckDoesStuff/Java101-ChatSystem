package Chat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
public class ChatService {
    Connection conn;

//    TODO Add a new chat to the chat db
    public ChatService(Connection conn) {
        this.conn = conn;
    }

    public ChatModel addChat(boolean isGroup, String name) {
        try {
            ChatModel newChatModel = new ChatModel(isGroup, name);
            String sql = "INSERT INTO chat (isGroup, name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setBoolean(1, isGroup);
            stmt.setString(2, name);

            conn.commit();
            ResultSet rs = stmt.executeQuery();
            newChatModel.setChatID(rs.getInt(1));
            return newChatModel;
        } catch (Exception e) {
            System.out.println("Error adding chat");
            throw new RuntimeException(e);
        }
    }

}
