package edu.neu.madcourse.gowalk.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;
import edu.neu.madcourse.gowalk.viewmodel.RewardListViewModel;

public class RewardsActivity extends AppCompatActivity {

    private RewardListViewModel viewModel;

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
            //TODO: open fragment
        });

    }

    private void redeemReward(Reward reward) {
        int currentAccumulatePoints = SharedPreferencesUtil.getAccumulatePoints(this);
        if (currentAccumulatePoints < reward.getPoints()) {
            //don't have points to redeem the reward
            Toast.makeText(this, "You don't have enough points to redeem the reward. Please keep walking!", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferencesUtil.setAccumulatePoints(this, currentAccumulatePoints - reward.getPoints());
            viewModel.deleteReward(reward);
            Toast.makeText(this, "Congratulations!! You are doing great!", Toast.LENGTH_LONG).show();
        }
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
            holder.rewardPoints.setText(reward.getPoints());
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


}
