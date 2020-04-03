package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.fragment.ShareFragment;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
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

    public void directToDailyRanking(View view) {
        Intent intent = new Intent(this, DailyRankActivity.class);
        startActivity(intent);
    }

    public void directToHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    public void directToSettings(View view) {
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

    public void sendMessageSteps(MenuItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = SharedPreferencesUtil.getUserId(getApplicationContext());
                String msgTitle = String.format(getString(R.string.send_steps_title), userId);
                //TODO: use actual steps
                String msgBody = String.format(getString(R.string.send_steps_body), userId, 10000);
                FCMUtil.sendMessageToTopic(msgTitle, msgBody, getString(R.string.steps_topic));
            }
        }).start();
    }

}