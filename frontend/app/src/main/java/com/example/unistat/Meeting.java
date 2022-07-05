package com.example.unistat;

import com.alamkanak.weekview.WeekViewEvent;

import org.json.JSONObject;

import java.util.Calendar;

public class Meeting extends WeekViewEvent {
    private long mId;
    private String mName;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mMentorEmail;
    private String mMenteeEmail;
    private double mPaymentAmount;
    private String mStatus;
    private JSONObject mDatalog;

    public Meeting(long id, String name, Calendar startTime, Calendar endTime, String mentorEmail, String menteeEmail, double paymentAmount, String status, JSONObject datalog) {
        mId = id;
        mName = name;
        mStartTime = startTime;
        mEndTime = endTime;
        mMentorEmail = mentorEmail;
        mMenteeEmail = menteeEmail;
        mPaymentAmount = paymentAmount;
        mStatus = status;
        mDatalog = datalog;
    }

    public Long getMeetingID() { return mId; }

    public String getMeetingName() { return mName; }

    public void setMentorID(String mentorID) {
        mMentorEmail = mentorID;
    }

    public String getMentorID() {
        return mMentorEmail;
    }

    public void setMenteeID(String menteeID) {
        mMenteeEmail = menteeID;
    }

    public String getMenteeID() {
        return mMenteeEmail;
    }

    public void setPaymentAmount(double paymentAmountParam) {
        mPaymentAmount = paymentAmountParam;
    }

    public double getPaymentAmount() {
        return mPaymentAmount;
    }

    public void setStatus(String statusInput) {
        if (statusInput == "Accepted" || statusInput == "Rejected" || statusInput == "Pending") {
            mStatus = statusInput;
        } else {
            throw new IllegalArgumentException("The status can only be Accepted, Rejected, or Pending, not: " + statusInput);
        }
    }

    public String getStatus() {
        return mStatus;
    }

    public void setDatalog(JSONObject datalogInput) {
        mDatalog = datalogInput;
    }

    public JSONObject getDatalog() {
        return mDatalog;
    }

}
