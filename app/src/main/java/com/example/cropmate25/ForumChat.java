package com.example.cropmate25;

public class ForumChat {
    private String timeStamp;
    private String userId;
    private String userName;
    private String key; // message collection Document ID
    private String threadId;
    private String chat;

    public ForumChat(String chat, String timeStamp, String userId, String userName) {
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

    public void setThreadID(String threadID) {
        this.threadId = threadID;
    }

    public String getThreadID() {
        return threadId;
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
