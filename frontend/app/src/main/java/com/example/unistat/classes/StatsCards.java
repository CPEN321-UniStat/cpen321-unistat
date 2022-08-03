package com.example.unistat.classes;

public class StatsCards {

    //Model Class
    private String mentorEmail;
    private String mentorName;
    private String univName;
    private String univMajor;
    private Double univGpa;
    private int univEntranceScore;
    private String univBio;
    private String userStatProfileImage;

    //Constructor
    public StatsCards(String mentorEmail, String mentorName, String univName, String univMajor, Double univGpa, int univEntranceScore, String univBio, String userStatProfileImage) {
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

    public Double getUnivGpa() {
        return univGpa;
    }

    public int getUnivEntranceScore() {
        return univEntranceScore;
    }

    public String getUnivBio() {
        return univBio;
    }

    public String getUserStatProfileImage() { return userStatProfileImage; }

}
