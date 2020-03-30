package edu.neu.madcourse.gowalk.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        pieChartView = findViewById(R.id.pie_chart);

        //todo: should fetch this data from db or SharedPreference
        int defaultCurrentStep = 0;
        populatePieChart(defaultCurrentStep, getDailyStepGoal(this));

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

    public void directToReport(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        startActivity(intent);
    }

    public void directToDailyRanking(View view) {
        Intent intent = new Intent(this, DailyRankActivity.class);
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
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            populatePieChart( Math.round(event.values[0]), getDailyStepGoal(this));
            Log.d(TAG, "Updating step count to " + event.values[0] + " last updated timestamp is " + event.timestamp);
        } else {
            Log.e(TAG, "Receiving event from sensor type: " + event.sensor.getName());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
