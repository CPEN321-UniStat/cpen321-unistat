package com.example.unistat.StatsCardView;

public class StatsCards {

    //Model Class
    private String mentorEmail;
    private String mentorName;
    private String univName;
    private String univMajor;
    private String univGpa;
    private String univEntranceScore;
    private String univBio;
    private String userStatProfileImage;

    //Constructor
    public StatsCards(String mentorEmail, String mentorName, String univName, String univMajor, String univGpa, String univEntranceScore, String univBio, String userStatProfileImage) {
        this.mentorEmail = mentorEmail;
        this.mentorName = mentorName;
        this.univName = univName;
        this.univMajor = univMajor;
        this.univGpa = univGpa;
        this.univEntranceScore = univEntranceScore;
        this.univBio = univBio;
        this.userStatProfileImage = userStatProfileImage;
    }

    //Getters
    public String getMentorEmail() {
        return mentorEmail;
    }

    public String getMentorName() {
        return mentorName;
    }

    public String getUnivName() {
        return univName;
    }

    public String getUnivMajor() {
        return univMajor;
    }

    public String getUnivGpa() {
        return univGpa;
    }

    public String getUnivEntranceScore() {
        return univEntranceScore;
    }

    public String getUnivBio() {
        return univBio;
    }

    public String getUserStatProfileImage() { return userStatProfileImage; }

}
