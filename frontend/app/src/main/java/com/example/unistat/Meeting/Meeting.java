package com.example.unistat.Meeting;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alamkanak.weekview.WeekViewEvent;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class Meeting extends WeekViewEvent {
    public enum Status {
        ACCEPTED, REJECTED, PENDING
    }

    private String mentorEmail, menteeEmail;
    private double paymentAmount;
    private Status status;
    private List<MeetingLog> meetingLogs;

    public Meeting(long id, String name, Calendar startTime, Calendar endTime, String mentorEmail,
                   String menteeEmail, double paymentAmount, Status status, List<MeetingLog> meetingLogs) {
        super(id, name, startTime, endTime);
        this.mentorEmail = mentorEmail;
        this.menteeEmail = menteeEmail;
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
