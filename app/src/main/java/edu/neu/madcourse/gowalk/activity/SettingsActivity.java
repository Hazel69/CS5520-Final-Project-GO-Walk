package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.util.FCMUtil;

public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

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
                            case R.id.homepageBtn:
                                directToHomepage();
                                break;
                        }
                        return true;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.goalSettingBtn).setChecked(true);
    }

    public void directToDailyRanking() {
        Intent intent = new Intent(this, DailyRankActivity.class);
        startActivity(intent);
    }

    public void directToHomepage() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    public void directToRewards() {
        Intent intent = new Intent(this, RewardsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private EditTextPreference dailyGoalSetting;
        private EditTextPreference dailyPointSetting;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            dailyGoalSetting = findPreference("daily-step-goal");
            dailyPointSetting = findPreference("points-gained-for-daily-goal");

            setEditTextPreferenceInputType(dailyGoalSetting, InputType.TYPE_CLASS_NUMBER);
            setEditTextPreferenceInputType(dailyPointSetting, InputType.TYPE_CLASS_NUMBER);
        }


        private void setEditTextPreferenceInputType(EditTextPreference editTextPreference, int inputType) {
            editTextPreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(inputType);
                        }
                    }
            );
        }
    }

}