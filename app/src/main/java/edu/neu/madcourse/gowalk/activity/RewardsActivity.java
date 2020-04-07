package edu.neu.madcourse.gowalk.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.fragment.AddRewardFragment;
import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

import static edu.neu.madcourse.gowalk.util.SharedPreferencesUtil.getAccumulatePoints;

public class RewardsActivity extends AppCompatActivity implements AddRewardFragment.AddRewardFragmentListener {

    private RewardListViewModel viewModel;

    private  BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        RecyclerView recyclerView = findViewById(R.id.reward_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RewardListAdapter rewardListAdapter = new RewardListAdapter();
        recyclerView.setAdapter(rewardListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        viewModel = ViewModelProviders.of(this).get(RewardListViewModel.class);
        viewModel.getRewards().observe(this, rewardListAdapter::setRewards);

        FloatingActionButton fab = findViewById(R.id.add_reward_fab);
        fab.setOnClickListener(view -> {
            AddRewardFragment addRewardFragment = new AddRewardFragment();
            addRewardFragment.show(getSupportFragmentManager(), "AddRewardFragment");
        });

        setTitle(getString(R.string.accumulate_points,getAccumulatePoints(this)));


        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.getMenu().findItem(R.id.rewardBtn).setChecked(true);

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rankBtn:
                                directToDailyRanking();
                                break;
                            case R.id.homepageBtn:
                                directToHomepage();
                                break;
                            case R.id.goalSettingBtn:
                                directToSettings();
                                break;
                        }
                        return true;
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.rewardBtn).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_menu, menu);
        return true;
    }

    private void redeemReward(Reward reward) {
        int currentAccumulatePoints = getAccumulatePoints(this);
        if (currentAccumulatePoints < reward.getPoints()) {
            //don't have points to redeem the reward
            Toast.makeText(this, "You don't have enough points to redeem the reward. Please keep walking!", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferencesUtil.setAccumulatePoints(this, currentAccumulatePoints - reward.getPoints());
            viewModel.deleteReward(reward);
            Toast.makeText(this, "Congratulations!! You are doing great!", Toast.LENGTH_LONG).show();
            setTitle(getString(R.string.accumulate_points,getAccumulatePoints(this)));
        }
    }

    @Override
    public void showAddRewardEmptyValueInfo() {
        Toast. makeText(this, "The name and points cannot be empty.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showAddRewardSuccessInfo() {
        Toast.makeText(this, "Add the reward successfully!",
                Toast.LENGTH_LONG).show();
    }

    private class RewardListAdapter extends RecyclerView.Adapter<RewardListAdapter.RewardViewHolder> {

        private List<Reward> rewardList = new ArrayList<>();

        void setRewards(List<Reward> rewards) {
            this.rewardList = rewards;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RewardViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reward_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
            Reward reward = rewardList.get(position);
            holder.rewardName.setText(reward.getName());
            holder.rewardPoints.setText(getString(R.string.reward_points, reward.getPoints()));
            holder.deleteButton.setOnClickListener(view -> {
                viewModel.deleteReward(reward);
            });
            holder.redeemButton.setOnClickListener(view -> {
                redeemReward(reward);
            });
        }

        @Override
        public int getItemCount() {
            return rewardList == null ? 0 : rewardList.size();
        }


        class RewardViewHolder extends RecyclerView.ViewHolder {
            private TextView rewardName;
            private TextView rewardPoints;

            private ImageButton redeemButton;
            private ImageButton deleteButton;

            public RewardViewHolder(@NonNull View itemView) {
                super(itemView);

                rewardName = itemView.findViewById(R.id.reward_name_text);
                rewardPoints= itemView.findViewById(R.id.reward_points_text);

                redeemButton = itemView.findViewById(R.id.redeem_reward_btn);
                deleteButton = itemView.findViewById(R.id.delete_reward_btn);
            }
        }
    }

    public void directToDailyRanking() {
        Intent intent = new Intent(this, DailyRankActivity.class);
        startActivity(intent);
    }

    public void directToHomepage() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
    }

    public void directToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void sendMessageSteps(MenuItem item) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String username = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "A user");
                String msgTitle = String.format(getString(R.string.send_steps_title), username);
                String msgBody = String.format(getString(R.string.send_steps_body), username, SharedPreferencesUtil.getTodayStep(RewardsActivity.this));
                FCMUtil.sendMessageToTopic(msgTitle, msgBody, getString(R.string.steps_topic));
            }
        }).start();
    }



}
