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

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import edu.neu.madcourse.gowalk.model.DailyStep;
import edu.neu.madcourse.gowalk.repository.FirebaseQueryLiveData;

public class DailyRankViewModel extends AndroidViewModel {
    private List<DailyStep> mQueryDailyStepList;

    public DailyRankViewModel(@NonNull Application application) {
        super(application);
        mQueryDailyStepList = new LinkedList<>();
    }

    @NonNull
    public LiveData<List<DailyStep>> getDailyRankListLiveData() {
        Query mQuery =
                FirebaseDatabase.getInstance().getReference().child("dailySteps")
                        .child(LocalDate.now().toString()).orderByChild("stepCount");
        FirebaseQueryLiveData mLiveData = new FirebaseQueryLiveData(mQuery);
        return Transformations.map(mLiveData, new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, List<DailyStep>> {

        @Override
        public List<DailyStep> apply(DataSnapshot dataSnapshot) {
            mQueryDailyStepList.clear();

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                DailyStep dailyStep = snapshot.getValue(DailyStep.class);
                mQueryDailyStepList.add(0, dailyStep);
            }

            return mQueryDailyStepList;
        }
    }

}
