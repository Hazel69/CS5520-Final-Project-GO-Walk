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

    public static void setUserId(Context context, @Nullable String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_USER_ID, /* defValue= */ null);
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

}
