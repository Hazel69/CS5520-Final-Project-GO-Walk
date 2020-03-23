package edu.neu.madcourse.gowalk.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

import edu.neu.madcourse.gowalk.model.DailyStep;

@Dao
public interface DailyStepDao {

    @Insert
    void insertDailyStep(DailyStep record);

    @Query("SELECT * FROM dailySteps")
    LiveData<List<DailyStep>> getAllDailySteps();

}
