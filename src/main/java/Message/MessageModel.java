package Message;

public class MessageModel {
    String messageID; // Primary Key
    String chatID;
    int sendUserID;
    int receiveUserID;
    String content;
    String time;

    public MessageModel(String messageID, String chatID, int sendUserID, int receiveUserID, String content, String time){
        this.messageID = messageID;
        this.chatID = chatID;
        this.sendUserID = sendUserID;
        this.receiveUserID = receiveUserID;
        this.content = content;
        this.time = time;
    }

    public String getMessageID(){
        return messageID;
    }

    public String getChatID(){
        return chatID;
    }

    public int getSendUserID(){
        return sendUserID;
    }

    public int getReceiveUserID(){
        return receiveUserID;
    }

    public String getContent(){
        return content;
    }

    public String getTime(){
        return time;
    }
}
