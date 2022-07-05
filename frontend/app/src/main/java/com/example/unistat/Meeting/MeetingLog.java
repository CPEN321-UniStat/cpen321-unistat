package com.example.unistat.Meeting;

public class MeetingLog {
    private String timestamp;
    private String userEmail;
    private boolean isMentor;
    private String action;

    public MeetingLog(String timestamp, String userEmail, boolean isMentor, String action) {
        this.timestamp = timestamp;
        this.userEmail = userEmail;
        this.isMentor = isMentor;
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isMentor() {
        return isMentor;
    }

    public void setMentor(boolean mentor) {
        isMentor = mentor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
