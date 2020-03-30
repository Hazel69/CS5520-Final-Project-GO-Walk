package edu.neu.madcourse.gowalk;

import android.app.Application;
import android.util.Log;

import java.util.UUID;

import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.firstLaunched;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.isFirstLaunch;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.setDailyStepGoal;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.setPointsGainedForDailyGoal;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.setUserId;

public class GoWalkApplication extends Application {

    private static final String TAG = GoWalkApplication.class.getSimpleName();


    private static final int DEFAULT_DAILY_STEP_GOAL = 8000;
    private static final int DEFAULT_POINTS_GAINED_FOR_DAILY_GOAL = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        if (isFirstLaunch(this)) {
            Log.d(TAG, "Launch app for the first time, save default config into SharedPreferences");

            setUserId(this, UUID.randomUUID().toString());
            setDailyStepGoal(this, DEFAULT_DAILY_STEP_GOAL);
            setPointsGainedForDailyGoal(this, DEFAULT_POINTS_GAINED_FOR_DAILY_GOAL);

            firstLaunched(this);
        }
    }
}
