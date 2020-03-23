package edu.neu.madcourse.gowalk.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

public class HomepageActivity extends AppCompatActivity {
    private RewardListViewModel rewardListViewModel;

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




    }
}
