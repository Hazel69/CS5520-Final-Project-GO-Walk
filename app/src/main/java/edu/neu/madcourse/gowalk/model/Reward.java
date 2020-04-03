package edu.neu.madcourse.gowalk.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rewards")
public class Reward {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int rid;

    private String name;

    private int points;

//    //todo: does the userId needed? cause in local database, it only stores the reward fo one user
//    @ForeignKey(entity = User.class,
//            parentColumns = "uid",
//            childColumns = "userId",
//            onDelete = ForeignKey.CASCADE)
//    private int userId;

    public Reward(String name, int points) {
        this.name = name;
        this.points = points;
//        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }

    //for testing
    @NonNull
    @Override
    public String toString() {
        return this.name + " " + this.points;
    }
}
