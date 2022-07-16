package com.example.unistat.meeting;

public class MeetingLog {
    public enum Action {
        JOINED, LEFT
    }
    private String timestamp;
    private String userEmail;
    private boolean isMentor;
    private Action action;

    public MeetingLog(String timestamp, String userEmail, boolean isMentor, Action action) {
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
