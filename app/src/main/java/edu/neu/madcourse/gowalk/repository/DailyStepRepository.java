package edu.neu.madcourse.gowalk.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.neu.madcourse.gowalk.dao.DailyStepDao;
import edu.neu.madcourse.gowalk.db.AppDatabase;
import edu.neu.madcourse.gowalk.model.DailyStep;

public class DailyStepRepository {

    private LiveData<List<DailyStep>> allDailySteps;
    private LiveData<List<DailyStep>> weeklyDailySteps;
    private LiveData<List<DailyStep>> monthlyDailySteps;
    private DailyStepDao dailyStepDao;

    public DailyStepRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        dailyStepDao = db.dailyStepDao();
        this.allDailySteps = this.dailyStepDao.getAllDailySteps();
        this.weeklyDailySteps = this.dailyStepDao.getWeeklySteps();
        this.monthlyDailySteps = this.dailyStepDao.getMonthlySteps();
    }

    public LiveData<List<DailyStep>> getAllDailySteps() {
        return this.allDailySteps;
    }

    public LiveData<List<DailyStep>> getWeeklyDailySteps() {
        return this.weeklyDailySteps;
    }

    public LiveData<List<DailyStep>> getMonthlyDailySteps() {
        return this.monthlyDailySteps;
    }

    public void insertDailyStep(DailyStep dailyStep) {
        new InsertAsyncTask(this.dailyStepDao).execute(dailyStep);
    }

    private static class InsertAsyncTask extends AsyncTask<DailyStep, Void, Void> {
        private DailyStepDao dailyStepDao;

        InsertAsyncTask(DailyStepDao dao) {
            this.dailyStepDao = dao;
        }

        @Override
        protected Void doInBackground(DailyStep... dailySteps) {
            dailyStepDao.insertDailyStep(dailySteps[0]);
            return null;
        }
    }

}
