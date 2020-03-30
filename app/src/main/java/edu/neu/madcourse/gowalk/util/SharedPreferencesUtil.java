package edu.neu.madcourse.gowalk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public final class SharedPreferencesUtil {

    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();

    private static final String DEFAULT_SHARED_PREFERENCES_NAME = "go-walk";
    private static final String KEY_USER_ID = "user-id";
    private static final String KEY_IS_FIRST_LAUNCH = "is-first-launch";
    private static final String KEY_DAILY_STEP_GOAL = "daily-step-goal";
    private static final String KEY_POINTS_GAINED_FOR_DAILY_GOAL = "points-gained-for-daily-goal";
    private static final String KEY_ACCUMLATE_POINTS = "accumulate-points";

    public static void setUserId(Context context, @Nullable String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String storedValue = sharedPreferences.getString(KEY_USER_ID, /* defValue= */ null);

        if (!TextUtils.isEmpty(storedValue)) {
            Log.w(TAG, "User id exists already!!");
            return;
        }
        sharedPreferences
                .edit()
                .putString(KEY_USER_ID, value)
                .apply();
    }

    @Nullable
    public static String getUserId(Context context) {
        return context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getString(KEY_USER_ID, /* defValue= */ null);
    }

    public static void setDailyStepGoal(Context context, int value) {
        context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putInt(KEY_DAILY_STEP_GOAL, value)
                .apply();
    }

    public static int getDailyStepGoal(Context context) {
        return context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(KEY_DAILY_STEP_GOAL, /* defValue= */ 0);
    }


    public static void setPointsGainedForDailyGoal(Context context, int value) {
        context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putInt(KEY_POINTS_GAINED_FOR_DAILY_GOAL, value)
                .apply();
    }

    public static int getPointsGainedForDailyGoal(Context context) {
        return context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(KEY_POINTS_GAINED_FOR_DAILY_GOAL, /* defValue= */ 0);
    }

    public static boolean isFirstLaunch(Context context) {
        return context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public static void firstLaunched(Context context) {
        context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_IS_FIRST_LAUNCH, true)
                .apply();
    }

    public static int getAccumulatePoints(Context context) {
        return context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .getInt(KEY_ACCUMLATE_POINTS, /* defValue= */ 0);
    }

    public static void setAccumulatePoints(Context context, int value) {
        context
                .getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putInt(KEY_ACCUMLATE_POINTS, value)
                .apply();
    }

}
