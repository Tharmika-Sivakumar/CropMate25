package com.example.cropmate25;

public class DataClassForum {
    private String title;
    private String question;
    private String image;
    private String timeStamp;
    private String userId;
    private String userName;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DataClassForum(String title, String question, String image, String timeStamp, String userId, String userName) {
        this.title = title;
        this.question = question;
        this.image = image != null ? image : "";
        this.timeStamp = timeStamp;
        this.userName = userName;
        this.userId = userId;
    }

    public DataClassForum() {}
}
