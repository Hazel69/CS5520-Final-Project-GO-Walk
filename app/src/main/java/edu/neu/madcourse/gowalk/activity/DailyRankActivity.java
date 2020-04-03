package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyRankViewModel;

public class DailyRankActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailyRankViewModel dailyRankViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_rank);

        recyclerView = findViewById(R.id.dailyRankRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        dailyRankViewModel =  ViewModelProviders.of(this).get(DailyRankViewModel.class);
        dailyRankViewModel.getDailyRankListLiveData().observe(this, mDailySteps -> {
            recyclerView.setAdapter(new DailyRankingAdapter(mDailySteps));
        });
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

    public void sendMessageToGoalCompletion(View type) {
        new Thread(FCMUtil::sendMessageToGoalCompletion).start();
    }

}
