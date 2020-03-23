package edu.neu.madcourse.gowalk.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.neu.madcourse.gowalk.dao.RewardDao;
import edu.neu.madcourse.gowalk.db.AppDatabase;
import edu.neu.madcourse.gowalk.model.Reward;

public class RewardRepository {

    private LiveData<List<Reward>> allRewards;
    private edu.neu.madcourse.gowalk.dao.RewardDao RewardDao;

    public RewardRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        RewardDao = db.rewardDao();
        this.allRewards = this.RewardDao.getAllRewards();
    }

    public LiveData<List<Reward>> getAllRewards() {
        return this.allRewards;
    }

    public void insertReward(Reward Reward) {
        new InsertAsyncTask(this.RewardDao).execute(Reward);
    }

    public void deleteReward(Reward Reward) {
        new DeleteAsyncTask(this.RewardDao).execute(Reward);
    }


    private static class InsertAsyncTask extends AsyncTask<Reward, Void, Void> {
        private RewardDao rewardDao;

        InsertAsyncTask(RewardDao dao) {
            this.rewardDao = dao;
        }

        @Override
        protected Void doInBackground(Reward... Rewards) {
            rewardDao.insertReward(Rewards[0]);
            System.out.println("print");
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Reward, Void, Void> {
        private RewardDao rewardDao;
        DeleteAsyncTask(RewardDao dao) {
            this.rewardDao = dao;
        }

        @Override
        protected Void doInBackground(Reward... Rewards) {
            rewardDao.deleteReward(Rewards[0]);
            return null;
        }
    }

}
