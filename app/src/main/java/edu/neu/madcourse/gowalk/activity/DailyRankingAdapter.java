package edu.neu.madcourse.gowalk.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.gowalk.R;
import edu.neu.madcourse.gowalk.model.DailyStep;

public class DailyRankingAdapter extends RecyclerView.Adapter<DailyRankingAdapter.ItemViewHolder> {
    private List<DailyStep> dailyRankList;
    private String userId;

    public DailyRankingAdapter(List<DailyStep> dailyRankList, String userId) {
        this.dailyRankList = dailyRankList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public DailyRankingAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_ranking_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyRankingAdapter.ItemViewHolder holder, int position) {
        if (dailyRankList != null && !dailyRankList.isEmpty()) {
            DailyStep target = dailyRankList.get(position);
            holder.firstPlaceView.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
            holder.rankView.setText(Integer.toString(position + 1));
            String username = target.getUsername();
            if (username == null || username.isEmpty()) {
                username = "Anonymous";
            }

            if (target.getUserId().equals(userId)) {
                username = "Me";
            }
            holder.usernameView.setText(username);
            holder.stepCountView.setText(Integer.toString(target.getStepCount()));
        }
    }

    @Override
    public int getItemCount() {
        return dailyRankList == null ? 0 : dailyRankList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView firstPlaceView;
        private final TextView rankView;
        private final TextView usernameView;
        private final TextView stepCountView;

        ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            firstPlaceView = itemView.findViewById(R.id.icon_view);
            rankView = itemView.findViewById(R.id.dailyRank);
            usernameView = itemView.findViewById(R.id.dailyRankUserName);
            stepCountView = itemView.findViewById(R.id.dailyRankStepCount);
        }
    }
}
