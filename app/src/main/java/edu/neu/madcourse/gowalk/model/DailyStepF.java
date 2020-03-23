package edu.neu.madcourse.gowalk.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDate;

@Entity(tableName = "dailySteps")
@IgnoreExtraProperties
public class DailyStepF {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private LocalDate date;
    private int stepCount;
    private int userId;
    private String username;

    public DailyStepF() {}

    public DailyStepF(LocalDate date, int stepCount, int userId) {
        this.date = date;
        this.stepCount = stepCount;
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}