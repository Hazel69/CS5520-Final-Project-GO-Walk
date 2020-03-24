package edu.neu.madcourse.gowalk.model;

public class DailyStepF {
    private String date;
    private int stepCount;
    private String userId;
    private String username;

    public DailyStepF() {}

    public DailyStepF(String userId, String username, String date, int stepCount) {
        this.date = date;
        this.stepCount = stepCount;
        this.userId = userId;
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}