package edu.neu.madcourse.gowalk.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.DailyStepF;

public class DailyRankingAdapter extends RecyclerView.Adapter<DailyRankingAdapter.ItemViewHolder> {
    private List<DailyStepF> dailyRankList;

    public DailyRankingAdapter(List<DailyStepF> dailyRankList) {
        this.dailyRankList = dailyRankList;
    }

    @NonNull
    @Override
    public DailyRankingAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_ranking_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyRankingAdapter.ItemViewHolder holder, int position) {
        DailyStepF target = dailyRankList.get(position);
        System.out.println(target);
        if (dailyRankList != null && !dailyRankList.isEmpty()) {
            target = dailyRankList.get(position);
            System.out.println(target);
            holder.rankView.setText(position + "");
            holder.usernameView.setText(target.getUsername());
            holder.stepCountView.setText(target.getStepCount() + "");
        }
    }

    @Override
    public int getItemCount() {
        return dailyRankList == null ? 0 : dailyRankList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView rankView;
        private final TextView usernameView;
        private final TextView stepCountView;

        ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            rankView = itemView.findViewById(R.id.dailyRank);
            usernameView = itemView.findViewById(R.id.dailyRankUserName);
            stepCountView = itemView.findViewById(R.id.dailyRankStepCount);
        }
    }
}
