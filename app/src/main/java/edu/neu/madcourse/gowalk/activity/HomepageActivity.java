package edu.neu.madcourse.gowalk.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.DailyStep;
import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.viewmodel.DailyStepViewModel;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

public class HomepageActivity extends AppCompatActivity {
    private RewardListViewModel rewardListViewModel;
    private DailyStepViewModel dailyStepViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);

        //todo: these code are for testing, delete when implement the actual logic
        rewardListViewModel = ViewModelProviders.of(this).get(RewardListViewModel.class);
        findViewById(R.id.add_reward).setOnClickListener(view -> {
            Reward reward = new Reward("switch","I want a switch", 200);
            rewardListViewModel.addReward(reward);
        });

        rewardListViewModel.getRewards().observe(this, rewards ->
                System.out.println(Arrays.toString(rewards.toArray())));

        findViewById(R.id.delete_reward).setOnClickListener(view -> {
            rewardListViewModel.deleteReward(rewardListViewModel.getRewards().getValue().get(0));
        });

        dailyStepViewModel = ViewModelProviders.of(this).get(DailyStepViewModel.class);
        findViewById(R.id.add_daily_step).setOnClickListener(view -> {
            DailyStep dailyStep = new DailyStep(new Date(Calendar.getInstance().getTimeInMillis()), 200);
            dailyStepViewModel.addDailyStep(dailyStep);
        });

        dailyStepViewModel.getDailyStepRecords().observe(this, dailySteps ->
                System.out.println(Arrays.toString(dailySteps.toArray())));


    }
}
