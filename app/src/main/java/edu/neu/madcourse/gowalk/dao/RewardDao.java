package edu.neu.madcourse.gowalk.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.neu.madcourse.gowalk.model.Reward;

@Dao
public interface RewardDao {
    @Insert
    void insertReward(Reward reward);

    @Delete
    void deleteReward(Reward reward);

    @Query("SELECT * FROM rewards")
    LiveData<List<Reward>> getAllRewards();
}
