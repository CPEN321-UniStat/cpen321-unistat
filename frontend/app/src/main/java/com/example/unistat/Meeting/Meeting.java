package com.example.unistat.Meeting;

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
    private String _id;
    private String name;
    /* startTime and endTime are ISO format time strings */
    private String startTimeString, endTimeString;
    private String mentorEmail, menteeEmail;
    private double paymentAmount;
    private String status;
    private List<MeetingLog> meetingLogs;

    public Meeting(long id, String name, Calendar startTime, Calendar endTime, String mentorEmail,
                   String menteeEmail, double paymentAmount, String status, List<MeetingLog> meetingLogs) {
        super(id, name, startTime, endTime);
        this._id = String.valueOf(id);
        this.name = name;
        this.startTimeString = getUTCDateTimeString(startTime);
        this.endTimeString = getUTCDateTimeString(endTime);
        this.mentorEmail = mentorEmail;
        this.menteeEmail = menteeEmail;
        this.paymentAmount = paymentAmount;
        this.status = status;
        this.meetingLogs = meetingLogs;

    }

    private String getUTCDateTimeString(Calendar calendar) {
        Date date = calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return dateFormat.format(date);
    }

    public String get_name() { return name; }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == "Accepted" || status == "Rejected" || status == "Pending") {
            this.status = status;
        } else {
            throw new IllegalArgumentException("The status can only be Accepted, Rejected, or Pending, not: " + status);
        }
    }

    public List<MeetingLog> getMeetingLogs() {
        return meetingLogs;
    }

    public void setMeetingLogs(List<MeetingLog> meetingLogs) {
        this.meetingLogs = meetingLogs;
    }
}
