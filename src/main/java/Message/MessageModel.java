package Message;

import java.time.LocalDateTime;

public class MessageModel {
    int messageID; // Primary Key
    String chatID;
    int sendUserID;
    String content;
    LocalDateTime time;

    public MessageModel(int messageID, String chatID, int sendUserID, String content, LocalDateTime time){
        this.messageID = messageID;
        this.chatID = chatID;
        this.sendUserID = sendUserID;
        this.content = content;
        this.time = time;
    }

    public int getMessageID(){
        return messageID;
    }

    public String getChatID(){
        return chatID;
    }

    public int getSendUserID(){
        return sendUserID;
    }

    public String getContent(){
        return content;
    }

    public LocalDateTime getTime(){
        return time;
    }
}
