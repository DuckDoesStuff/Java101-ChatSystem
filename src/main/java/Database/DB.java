package Database;

import java.sql.*;

public class DB {
    Connection conn;

    public DB() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://ep-falling-resonance-56588782.ap-southeast-1.aws.neon.tech/JavaChat?user=mailtienduc&password=cwESY8fOn6Gs&sslmode=require");
//            conn = DriverManager.getConnection(
//                    "jdbc:postgresql://localhost:5432/javaChat",
//                    "postgres",
//                    "0112");

            System.out.println("Connected to database");

            Statement stmt = conn.createStatement();
            conn.setAutoCommit(false);
            // TODO: QUERY TO INIT DATABASE IN HERE
            String sql1 = "CREATE TABLE IF NOT EXISTS users (" +
                    " userID SERIAL PRIMARY KEY, " +
                    " first_name VARCHAR(255), " +
                    " last_name VARCHAR(255), " +
                    " username VARCHAR(255) NOT NULL UNIQUE, " +
                    " password VARCHAR(255) NOT NULL," +
                    " email VARCHAR(255) NOT NULL UNIQUE, " +
                    " address VARCHAR(255), " +
                    " dateOfBirth DATE," +
                    " gender BOOLEAN)";
            stmt.execute(sql1);
            conn.commit();

            String sql2 = "CREATE TABLE IF NOT EXISTS friendrequest (" +
                    " senderID INT, " +
                    " receiverID INT, " +
                    " accepted boolean," +
                    " FOREIGN KEY (senderID) REFERENCES users(userID)," +
                    " FOREIGN KEY (receiverID) REFERENCES users(userID)," +
                    " PRIMARY KEY (senderID, receiverID))";
            stmt.execute(sql2);
            conn.commit();

            String sql3 = "CREATE TABLE IF NOT EXISTS friendlist (" +
                    " userID INT, " +
                    " friendID INT," +
                    " FOREIGN KEY (userID) REFERENCES users(userID)," +
                    " FOREIGN KEY (friendID) REFERENCES users(userID)," +
                    " PRIMARY KEY (userID, friendID))";
            stmt.execute(sql3);
            conn.commit();

            String sql4 = "CREATE TABLE IF NOT EXISTS username_userid (" +
                    " username VARCHAR(255) NOT NULL UNIQUE, " +
                    " userID INT," +
                    " FOREIGN KEY (userID) REFERENCES users(userID)," +
                    " PRIMARY KEY (username, userID))";
            stmt.execute(sql4);
            conn.commit();

            String sql5 = "CREATE TABLE IF NOT EXISTS Chat (" +
                    " chatID SERIAL PRIMARY KEY, " +
                    " isGroup BOOLEAN NOT NULL, " +
                    " name VARCHAR(255) )";

            stmt.execute(sql5);
            conn.commit();

            String sql6 = "CREATE TABLE IF NOT EXISTS ChatMember (" +
                    " chatID SERIAL, " +
                    " userID INT, " +
                    " FOREIGN KEY (chatID) REFERENCES Chat(chatID), " +
                    " FOREIGN KEY (userID) REFERENCES users(userID), " +
                    " PRIMARY KEY (chatID, userID))";

            stmt.execute(sql6);
            conn.commit();

            String sql7 = "CREATE TABLE IF NOT EXISTS Message (" +
                    " messageID SERIAL PRIMARY KEY, " +
                    " chatID INT, " +
                    " senderID INT, " +
                    " content VARCHAR(255), " +
                    " time TIMESTAMP, " +
                    " FOREIGN KEY (chatID) REFERENCES Chat(chatID), " +
                    " FOREIGN KEY (senderID) REFERENCES users(userID))";
            stmt.execute(sql7);
            conn.commit();

        } catch (Exception e) {
            System.out.println("Error connecting to the database");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection might already be closed");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        DB db = new DB();
        db.closeConnection();
    }
}
