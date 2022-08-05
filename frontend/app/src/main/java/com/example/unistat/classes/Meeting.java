package com.example.unistat.classes;

import android.graphics.Color;
import com.alamkanak.weekview.WeekViewEvent;
import java.util.Calendar;
import java.util.List;

public class Meeting extends WeekViewEvent {

    private String mentorEmail;
    private String mentorName;
    private String menteeName;
    private String menteeEmail;
    private double paymentAmount;
    private Status status;
    private List<MeetingLog> meetingLogs;

    public enum Status {
        ACCEPTED, REJECTED, PENDING
    }

    public static class User {
        public String name;
        public String email;
        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    public Meeting(long id, String name, User mentee, User mentor, Calendar startTime, Calendar endTime,
                   double paymentAmount, Status status, List<MeetingLog> meetingLogs) {
        super(id, name, startTime, endTime);
        this.mentorEmail = mentor.email;
        this.mentorName = mentor.name;
        this.menteeName = mentee.name;
        this.menteeEmail = mentee.email;
        this.paymentAmount = paymentAmount;
        setStatus(status);
        this.meetingLogs = meetingLogs;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getMentorName() {
        return mentorName;
    }

    public String getMenteeName() {
        return menteeName;
    }

    public String getMenteeEmail() {
        return menteeEmail;
    }

    public void setMenteeEmail(String menteeEmail) {
        this.menteeEmail = menteeEmail;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.ACCEPTED){
            this.setColor(Color.rgb(15,157,88));
        }
        else if (status == Status.REJECTED) {
            this.setColor(Color.rgb(219,68,55));
        }
    }

    public List<MeetingLog> getMeetingLogs() {
        return meetingLogs;
    }

    public void setMeetingLogs(List<MeetingLog> meetingLogs) {
        this.meetingLogs = meetingLogs;
    }
}
