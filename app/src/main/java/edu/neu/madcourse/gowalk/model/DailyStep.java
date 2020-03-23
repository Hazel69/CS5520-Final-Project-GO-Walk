package edu.neu.madcourse.gowalk.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "dailySteps")
public class DailyStep {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private Date date;

    private int stepCount;

    //todo: does the userId needed? cause in local database, if it only stores the reward fo one user
    @ForeignKey(entity = User.class,
                    parentColumns = "uid",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE)
    private int userId;

    public DailyStep(Date date, int stepCount, int userId) {
        this.date = date;
        this.stepCount = stepCount;
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
}
