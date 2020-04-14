package edu.neu.madcourse.gowalk.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.StepCountingService;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = HomepageActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION_PERMISSION = 1002;

    private PieChartView pieChartView;
    private BottomNavigationView bottomNav;
    private StepCountingService stepCountingService;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        pieChartView = findViewById(R.id.pie_chart);

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

        PackageManager packageManager = getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Toast.makeText(this, "Sorry, the step counter sensor is not available on you phone!",
                    Toast.LENGTH_LONG).show();
        } else {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Log.d(TAG, "StepCountingService connected");
                    stepCountingService =
                            ((StepCountingService.StepCountingBinder) service).getService();
                    stepCountingService.getCurrentStep().observe(HomepageActivity.this,
                            result -> {
                                Log.d(TAG, "Receive get current step change");
                                populatePieChart(result,
                                        SharedPreferencesUtil.getDailyStepGoal(HomepageActivity.this));
                                SharedPreferencesUtil.setTodayStep(HomepageActivity.this, result);
                            });
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.d(TAG, "StepCountingService disconnected");
                }
            };

            bindService(new Intent(this, StepCountingService.class), serviceConnection,
                    BIND_AUTO_CREATE);
        }


        //the system auto-grants the permission if API level is lower or equals to API Level 28
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        REQUEST_CODE_ACTIVITY_RECOGNITION_PERMISSION
                );
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.homepageBtn).setChecked(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACTIVITY_RECOGNITION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //restart the service
                    stopService(new Intent(this, StepCountingService.class));
                    startForegroundService(new Intent(this, StepCountingService.class));
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                            REQUEST_CODE_ACTIVITY_RECOGNITION_PERMISSION
                    );
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
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
        int displayCurrentStep = Math.min(currentStep, dailyGoal);
        SliceValue completedSliceValue = new SliceValue(displayCurrentStep, Color.parseColor("#6AC199"));
        SliceValue remainingSliceValue =
                new SliceValue(dailyGoal - displayCurrentStep, Color.parseColor("#FB6734"));

        List<SliceValue> values = new ArrayList<>();
        values.add(completedSliceValue);
        values.add(remainingSliceValue);

        PieChartData data = new PieChartData(values);
        data.setHasLabelsOnlyForSelected(true);
        data.setHasLabelsOutside(true);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(8);
        data.setCenterText1(String.valueOf(currentStep));
        data.setCenterText1FontSize(30);
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
                String username =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "A user");
                String msgTitle = String.format(getString(R.string.send_steps_title), username);
                String msgBody = String.format(getString(R.string.send_steps_body), username,
                        SharedPreferencesUtil.getTodayStep(HomepageActivity.this));
                FCMUtil.sendMessageToTopic(msgTitle, msgBody, getString(R.string.steps_topic));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}
