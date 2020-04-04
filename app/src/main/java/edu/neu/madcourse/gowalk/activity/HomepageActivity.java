package edu.neu.madcourse.gowalk.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyStepViewModel;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.getDailyStepGoal;

public class HomepageActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = HomepageActivity.class.getSimpleName();

    private RewardListViewModel rewardListViewModel;
    private DailyStepViewModel dailyStepViewModel;

    private PieChartView pieChartView;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private BottomNavigationView bottomNav;

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

        subscribeToTopic(getString(R.string.goal_completion_topic));
        subscribeToTopic(getString(R.string.steps_topic));

        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rankBtn:
                                directToDailyRanking();
                                break;
                            case R.id.rewardBtn:
                                directToRewards();
                                break;
                            case R.id.goalSettingBtn:
                                directToSettings();
                                break;
                        }
                        return true;
                    }
                });



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
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.homepageBtn).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }

    public void directToReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void directToDailyRanking() {
        Intent intent = new Intent(this, DailyRankActivity.class);
        startActivity(intent);
    }

    public void directToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void directToRewards() {
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

    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Message subscription failed.");
                    }

                });
    }

    public void sendMessageSteps(MenuItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String username = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "A user");
                String msgTitle = String.format(getString(R.string.send_steps_title), username);
                //TODO: use actual steps
                String msgBody = String.format(getString(R.string.send_steps_body), username, 10000);
                FCMUtil.sendMessageToTopic(msgTitle, msgBody, getString(R.string.steps_topic));
            }
        }).start();
    }

}
