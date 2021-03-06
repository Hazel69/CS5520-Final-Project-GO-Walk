package edu.neu.madcourse.gowalk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;


public final class SharedPreferencesUtil {

    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();

    private static final String KEY_USER_ID = "user-id";
    private static final String KEY_IS_FIRST_LAUNCH = "is-first-launch";
    private static final String KEY_DAILY_STEP_GOAL = "daily-step-goal";
    private static final String KEY_POINTS_GAINED_FOR_DAILY_GOAL = "points-gained-for-daily-goal";
    private static final String KEY_ACCUMULATE_POINTS = "accumulate-points";
    private static final String KEY_STEP_OFFSET = "step-offset";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LAST_RECORD_TIME = "last-record-time";
    private static final String KEY_TODAY_STEP = "today-step";
    private static final String KEY_HAS_RECEIVED_FIRST_SENSOR_EVENT = "has-received-first-sensor-event";

    public static void setUserId(Context context, @Nullable String value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
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
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USER_ID, /*
        defValue= */ null);
    }

    public static void setDailyStepGoal(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_DAILY_STEP_GOAL, String.valueOf(value))
                .apply();
    }

    public static int getDailyStepGoal(Context context) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_DAILY_STEP_GOAL, /* defValue= */ "0"));
    }


    public static void setPointsGainedForDailyGoal(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_POINTS_GAINED_FOR_DAILY_GOAL, String.valueOf(value))
                .apply();
    }

    public static int getPointsGainedForDailyGoal(Context context) {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_POINTS_GAINED_FOR_DAILY_GOAL, /* defValue= */ "0"));
    }

    public static boolean isFirstLaunch(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public static void firstLaunched(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_IS_FIRST_LAUNCH, false)
                .apply();
    }

    public static int getAccumulatePoints(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(KEY_ACCUMULATE_POINTS, /* defValue= */ 0);
    }

    public static void setAccumulatePoints(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(KEY_ACCUMULATE_POINTS, value)
                .apply();
    }

    public static void setStepOffset(Context context, int value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(KEY_STEP_OFFSET, value).apply();
    }

    public static int getStepOffset(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_STEP_OFFSET, 0);
    }

    public static String getUsername(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USERNAME, "");
    }

    public static void setLastRecordTime(Context context, Long lastRecordTime) {
        Log.d(TAG, "Save last record time: " + lastRecordTime);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(KEY_LAST_RECORD_TIME, lastRecordTime).apply();
    }

    public static Long getLastRecordTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_LAST_RECORD_TIME, 0);
    }

    public static void setTodayStep(Context context, int steps) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(KEY_TODAY_STEP, steps).apply();
    }

    public static int getTodayStep(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TODAY_STEP, 0);
    }

    public static boolean getHasReceivedFirstSensorEvent(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_HAS_RECEIVED_FIRST_SENSOR_EVENT, false);
    }

    public static void setKeyHasReceivedFirstSensorEvent(Context context, boolean val) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(KEY_HAS_RECEIVED_FIRST_SENSOR_EVENT, val).apply();
    }

}
