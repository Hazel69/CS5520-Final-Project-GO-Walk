package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.viewmodel.DailyRankViewModel;

public class DailyRankActivity extends AppCompatActivity {
    private static final String TAG = DailyRankActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAUV_nk_s:APA91bH9a-CchSZdc_smEYktmBM7-XSkVbgiEAcDchEKrLg6RqaMknNH4rO0id9OYTpvBRLwpCANQiWKaJIc_atgOqI3YhlP4_5AyTM3qAnlcGPQvcGUagavS0COGiKiyA4RO4DF4g97";

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
