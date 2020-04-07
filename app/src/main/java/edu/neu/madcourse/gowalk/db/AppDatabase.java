package edu.neu.madcourse.gowalk.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import edu.neu.madcourse.gowalk.dao.RewardDao;
import edu.neu.madcourse.gowalk.model.Reward;

@Database(entities = {Reward.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract RewardDao rewardDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database.db").build();
                }
            }
        }
        return INSTANCE;
    }

}