package Chat;
import java.sql.*;
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
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setBoolean(1, isGroup);
            stmt.setString(2, name);

            conn.commit();
            int affectedRow = stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newChatModel.setChatID(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Creating chat failed, no ID obtained.");
                }
            }
            return newChatModel;
        } catch (Exception e) {
            System.out.println("Error adding chat");
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ChatModel> getAllChat(int userid){
        try {
            String sql = "SELECT * FROM chat WHERE chatID IN (SELECT chatID FROM chatmember WHERE userID = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            ArrayList<ChatModel> chats = new ArrayList<>();
            while(rs.next()) {
                ChatModel chat = new ChatModel(
                        rs.getInt("chatID"),
                        rs.getBoolean("isGroup"),
                        rs.getString("name")
                );
                chats.add(chat);
            }
            return chats;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<ChatModel> getAllGroupChat(int userid){
        try {
            String sql = "SELECT * FROM chat WHERE chatID IN (SELECT chatID FROM chatmember WHERE userID = ?) AND isGroup = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userid);
            ResultSet rs = stmt.executeQuery();
            ArrayList<ChatModel> chats = new ArrayList<>();
            while(rs.next()) {
                ChatModel chat = new ChatModel(
                        rs.getInt("chatID"),
                        rs.getBoolean("isGroup"),
                        rs.getString("name")
                );
                chats.add(chat);
            }
            return chats;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Find chat base on senderID and receiverID
    public ChatModel findChat(int senderID, int receiverID) {
        try {
            // Error here
            String sql = "SELECT * FROM chat WHERE chatID IN ((SELECT chatID FROM chatmember WHERE userID = ?) INTERSECT  (SELECT chatID FROM chatmember WHERE userID = ?))";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, senderID);
            stmt.setInt(2, receiverID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                ChatModel chat = new ChatModel(
                        rs.getInt("chatID"),
                        rs.getBoolean("isGroup"),
                        rs.getString("name")
                );
                return chat;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getChatIdFromName(String name){
        try {
            String sql = "SELECT chatID FROM chat WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("chatID");
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
