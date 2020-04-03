package edu.neu.madcourse.gowalk.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.fragment.ShareFragment;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyStepViewModel;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.getAccumulatePoints;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.getDailyStepGoal;
import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.setAccumulatePoints;

public class HomepageActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = HomepageActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAUV_nk_s:APA91bH9a-CchSZdc_smEYktmBM7-XSkVbgiEAcDchEKrLg6RqaMknNH4rO0id9OYTpvBRLwpCANQiWKaJIc_atgOqI3YhlP4_5AyTM3qAnlcGPQvcGUagavS0COGiKiyA4RO4DF4g97";

    private RewardListViewModel rewardListViewModel;
    private DailyStepViewModel dailyStepViewModel;

    private PieChartView pieChartView;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        pieChartView = findViewById(R.id.pie_chart);

        //todo: should fetch this data from db or SharedPreference
        int defaultCurrentStep = 0;
        populatePieChart(defaultCurrentStep, getDailyStepGoal(this));

        //TODO: may move to service for tracking step when app is killed
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor != null) {
            Log.v(TAG, "Register listener to SensorManager");
            final boolean result = sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
            if (!result) {
                Log.e(TAG, "Failed to register listener to step count sensor");
            }
        } else {
            Log.e(TAG, "Failed to obtain step count sensor!!!");
        }

        subscribeToGoalCompletion();

        //todo: these code are for testing, delete when implement the actual logic
//        rewardListViewModel = ViewModelProviders.of(this).get(RewardListViewModel.class);
//        findViewById(R.id.add_reward).setOnClickListener(view -> {
//            Reward reward = new Reward("switch", "I want a switch", 200);
//            rewardListViewModel.addReward(reward);
//        });
//
//        rewardListViewModel.getRewards().observe(this, rewards ->
//                System.out.println(Arrays.toString(rewards.toArray())));
//
//        findViewById(R.id.delete_reward).setOnClickListener(view -> {
//            rewardListViewModel.deleteReward(rewardListViewModel.getRewards().getValue().get(0));
//        });
//
//        dailyStepViewModel = ViewModelProviders.of(this).get(DailyStepViewModel.class);
//        findViewById(R.id.add_daily_step).setOnClickListener(view -> {
//            DailyStep dailyStep = new DailyStep(new Date(Calendar.getInstance().getTimeInMillis()), 200);
//            dailyStepViewModel.addDailyStep(dailyStep);
//        });
//
//        dailyStepViewModel.getDailyStepRecords().observe(this, dailySteps ->
//                System.out.println(Arrays.toString(dailySteps.toArray())));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.share_menu_item) {
            this.showShareFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShareFragment() {
        ShareFragment shareFragment = new ShareFragment();
        shareFragment.show(getSupportFragmentManager(), "shareFragment");
    }

    public void directToReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void directToDailyRanking(View view) {
        Intent intent = new Intent(this, DailyRankActivity.class);
        startActivity(intent);
    }

    public void directToHomepage(View view) {
    }

    public void directToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void directToRewards(View view) {
        Intent intent = new Intent(this, RewardsActivity.class);
        startActivity(intent);
    }

    private void populatePieChart(int currentStep, int dailyGoal) {
        currentStep = Math.min(currentStep, dailyGoal);
        SliceValue completedSliceValue = new SliceValue(currentStep, ChartUtils.pickColor());
        SliceValue remainingSliceValue =
                new SliceValue(dailyGoal - currentStep, ChartUtils.pickColor());

        List<SliceValue> values = new ArrayList<>();
        values.add(completedSliceValue);
        values.add(remainingSliceValue);

        PieChartData data = new PieChartData(values);
        data.setHasLabels(true);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(24);
        data.setCenterText1(String.valueOf(currentStep));

        pieChartView.setPieChartData(data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //TODO: should calculate the step for today, cause the sensor returns the number of steps taken by the user since the last reboot
        //TODO: should update data in db and update data in Firebase
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            populatePieChart(Math.round(event.values[0]), getDailyStepGoal(this));
            Log.d(TAG, "Updating step count to " + event.values[0] + " last updated timestamp is " + event.timestamp);
        } else {
            Log.e(TAG, "Receiving event from sensor type: " + event.sensor.getName());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void subscribeToGoalCompletion() {
        FirebaseMessaging.getInstance().subscribeToTopic("GoalCompletion")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Message subscription failed.");
                    }

                });
    }

    /**
     * Button Handler; creates a new thread that sends off a message
     *
     * @param type
     */
    public void sendMessageToGoalCompletion(View type) {
        new Thread(FCMUtil::sendMessageToGoalCompletion).start();
    }

}
