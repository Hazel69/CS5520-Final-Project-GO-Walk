package edu.neu.madcourse.gowalk.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.StepCountingService;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyStepViewModel;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.getDailyStepGoal;

public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = HomepageActivity.class.getSimpleName();

    private RewardListViewModel rewardListViewModel;
    private DailyStepViewModel dailyStepViewModel;

    private PieChartView pieChartView;
    private BottomNavigationView bottomNav;
    private StepCountingService stepCountingService;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        pieChartView = findViewById(R.id.pie_chart);

        //todo: should fetch this data from db or SharedPreference
        int defaultCurrentStep = 0;
        populatePieChart(defaultCurrentStep, getDailyStepGoal(this));

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

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "StepCountingService connected");
                stepCountingService =
                        ((StepCountingService.StepCountingBinder) service).getService();
                stepCountingService.getCurrentStep().observe(HomepageActivity.this,
                        result -> populatePieChart(result,
                                SharedPreferencesUtil.getDailyStepGoal(HomepageActivity.this)));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "StepCountingService disconnected");
            }
        };
        bindService(new Intent(this, StepCountingService.class), serviceConnection,
                BIND_AUTO_CREATE);
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
//            DailyStep dailyStep = new DailyStep(new Date(Calendar.getInstance().getTimeInMillis
//            ()), 200);
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
                //TODO: need to use username
                String userId = SharedPreferencesUtil.getUserId(getApplicationContext());
                String msgTitle = String.format(getString(R.string.send_steps_title), userId);
                //TODO: use actual steps
                String msgBody = String.format(getString(R.string.send_steps_body), userId, 10000);
                FCMUtil.sendMessageToTopic(msgTitle, msgBody, getString(R.string.steps_topic));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unbindService(serviceConnection);
    }
}
