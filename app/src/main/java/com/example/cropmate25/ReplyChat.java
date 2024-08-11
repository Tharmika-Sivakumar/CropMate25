package com.example.cropmate25;

public class ReplyChat {
    private String timeStamp;
    private String userId;
    private String userName;
    private String key; // Document ID for the reply
    private String threadId;
    private String messageId;
    private String chat;

    public ReplyChat(String chat, String timeStamp, String userId, String userName) {
        this.chat = chat;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.userName = userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getThreadID() {
        return threadId;
    }

    public void setThreadID(String threadId) {
        this.threadId = threadId;
    }

    public String getMessageID() {
        return messageId;
    }

    public void setMessageID(String messageId) {
        this.messageId = messageId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }
}
