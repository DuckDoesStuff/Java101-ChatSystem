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




}
