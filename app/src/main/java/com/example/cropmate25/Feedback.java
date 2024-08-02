package com.example.cropmate25;

public class Feedback {
    private String timeStamp;
    private String feedback;

    public Feedback() {}

    public Feedback(String timeStamp, String feedback) {
        this.timeStamp = timeStamp;
        this.feedback = feedback;
    }

    public String getTimeStamp() {
        return timeStamp;
    }



    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
