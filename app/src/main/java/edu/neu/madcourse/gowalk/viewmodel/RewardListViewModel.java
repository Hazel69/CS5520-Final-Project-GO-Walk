package edu.neu.madcourse.gowalk.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.neu.madcourse.gowalk.model.Reward;
import edu.neu.madcourse.gowalk.repository.RewardRepository;

public class RewardListViewModel extends AndroidViewModel {
    private RewardRepository rewardRepository;
    private LiveData<List<Reward>> _rewards;

    public RewardListViewModel(Application application) {
        super(application);
        rewardRepository = new RewardRepository(application);
        _rewards = rewardRepository.getAllRewards();
    }

    public void addReward(Reward reward) {
        rewardRepository.insertReward(reward);
    }

    public void deleteReward(Reward reward) {
        rewardRepository.deleteReward(reward);
    }

    public LiveData<List<Reward>> getRewards() {
        return _rewards;
    }

}
