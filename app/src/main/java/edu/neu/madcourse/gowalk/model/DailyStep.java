package edu.neu.madcourse.gowalk.model;

import androidx.annotation.NonNull;

public class DailyStep {
    private String date;
    private int stepCount;
    private String userId;
    private String username;

    public DailyStep() {}

    public DailyStep(String userId, String username, String date, int stepCount) {
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

    @Override
    @NonNull
    public String toString() {
        return String.format("%s on %s: %d steps", username, date, stepCount);
    }
}