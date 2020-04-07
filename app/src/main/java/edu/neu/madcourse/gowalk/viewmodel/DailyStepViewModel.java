package edu.neu.madcourse.gowalk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.gowalk.model.DailyStepF;
import edu.neu.madcourse.gowalk.repository.FirebaseQueryLiveData;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;

public class DailyStepViewModel extends AndroidViewModel {
    private static final Query weeklyQuery =
            FirebaseDatabase.getInstance().getReference().child("dailySteps")
            .orderByKey().limitToLast(7);
    private static final Query monthlyQuery =
            FirebaseDatabase.getInstance().getReference().child("dailySteps")
                    .orderByKey().limitToLast(30);

    private List<DailyStepF> _weeklyStepRecords;
    private List<DailyStepF> _monthlyStepRecords;

    public DailyStepViewModel(Application application) {
        super(application);
        _weeklyStepRecords = new ArrayList<>();
        _monthlyStepRecords = new ArrayList<>();
    }

    @NonNull
    public LiveData<List<DailyStepF>> getWeeklyStepRecordsLiveData() {
        FirebaseQueryLiveData weeklyStepLiveData = new FirebaseQueryLiveData(weeklyQuery);
        return Transformations.map(weeklyStepLiveData, new Deserializer(_weeklyStepRecords));
    }

    @NonNull
    public LiveData<List<DailyStepF>> getMonthlyStepRecordsLiveData() {
        FirebaseQueryLiveData monthlyStepLiveData = new FirebaseQueryLiveData(monthlyQuery);
        return Transformations.map(monthlyStepLiveData, new Deserializer(_monthlyStepRecords));
    }

    private class Deserializer implements Function<DataSnapshot, List<DailyStepF>> {
        private List<DailyStepF> data;

        Deserializer(List<DailyStepF> data) {
            this.data = data;
        }

        @Override
        public List<DailyStepF> apply(DataSnapshot dataSnapshot) {
            data.clear();

            String userId = SharedPreferencesUtil.getUserId(getApplication());
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.getKey() != null && snapshot1.getKey().equals(userId)) {
                        DailyStepF dailyStep = snapshot1.getValue(DailyStepF.class);
                        data.add(dailyStep);
                    }
                }
            }

            return data;
        }
    }

}
