package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.fragment.ShareFragment;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyRankViewModel;

public class DailyRankActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailyRankViewModel dailyRankViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_rank);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.dailyRankRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        dailyRankViewModel =  ViewModelProviders.of(this).get(DailyRankViewModel.class);
        dailyRankViewModel.getDailyRankListLiveData().observe(this, mDailySteps -> {
            recyclerView.setAdapter(new DailyRankingAdapter(mDailySteps));
        });
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

    public void directToDailyRanking(View view) {
    }

    public void directToHomepage(View view) {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    public void directToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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

}
