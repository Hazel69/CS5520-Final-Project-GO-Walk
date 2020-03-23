package edu.neu.madcourse.gowalk.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//todo: does the local db need the user table? cause there is only one user
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private int uid;

    private String username;
    private int currPoints;

    //todo: these two fields are only key-value, can be stored in SharedPreference
    private int dailyStepGoal;
    private int pointsGainedPerGoal;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrPoints() {
        return currPoints;
    }

    public void setCurrPoints(int currPoints) {
        this.currPoints = currPoints;
    }

    public int getDailyStepGoal() {
        return dailyStepGoal;
    }

    public void setDailyStepGoal(int dailyStepGoal) {
        this.dailyStepGoal = dailyStepGoal;
    }

    public int getPointsGainedPerGoal() {
        return pointsGainedPerGoal;
    }

    public void setPointsGainedPerGoal(int pointsGainedPerGoal) {
        this.pointsGainedPerGoal = pointsGainedPerGoal;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
