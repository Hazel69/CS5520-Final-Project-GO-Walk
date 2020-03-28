package edu.neu.madcourse.gowalk.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import edu.neu.madcourse.gowalk.model.DailyStep;
import edu.neu.madcourse.gowalk.repository.DailyStepRepository;

public class DailyStepViewModel extends AndroidViewModel {
    private DailyStepRepository dailyStepRepository;

    private LiveData<List<DailyStep>> _dailyStepRecords;
    private LiveData<List<DailyStep>> _weeklyStepRecords;
    private LiveData<List<DailyStep>> _monthlyStepRecords;

    public DailyStepViewModel(Application application) {
        super(application);
        dailyStepRepository = new DailyStepRepository(application);
        _dailyStepRecords = dailyStepRepository.getAllDailySteps();
        _weeklyStepRecords = dailyStepRepository.getWeeklyDailySteps();
        _monthlyStepRecords = dailyStepRepository.getMonthlyDailySteps();
    }

    public LiveData<List<DailyStep>> getDailyStepRecords() {
        return _dailyStepRecords;
    }

    public LiveData<List<DailyStep>> getDailyStepRepository() {
        return _dailyStepRecords;
    }

    public LiveData<List<DailyStep>> getWeeklyStepRecords() {
        return _weeklyStepRecords;
    }

    public LiveData<List<DailyStep>> getMonthlyStepRecords() {
        return _monthlyStepRecords;
    }

    public void addDailyStep(DailyStep dailyStep) {
        dailyStepRepository.insertDailyStep(dailyStep);
    }

}
