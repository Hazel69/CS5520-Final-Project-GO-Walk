package edu.neu.madcourse.gowalk;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.neu.madcourse.gowalk.model.DailyStepF;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "REPORT_ACTIVITY";
    private DatabaseReference stepDatabase;

    private Button updateStepsBtn;
    private TextView dailyStepView;
    private EditText stepsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        stepDatabase = FirebaseDatabase.getInstance().getReference().child("dailySteps");
        updateStepsBtn = findViewById(R.id.updateDS);
        dailyStepView = findViewById(R.id.dailySteps);
        stepsEditText = findViewById(R.id.editText);

        updateStepsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStep("zBo3SX3GUP426sjhBLQV");
            }
        });

        ValueEventListener stepListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DailyStepF step = dataSnapshot.getValue(DailyStepF.class);
                dailyStepView.setText(step.getUsername() + " on " + step.getDate() + ": " + step.getStepCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getDailyStep:onCancelled", databaseError.toException());
            }
        };

        stepDatabase.addValueEventListener(stepListener);
    }

    private void updateStep(String id) {
        final String steps = stepsEditText.getText().toString();

        if (steps.isEmpty()) {
            return;
        }

        stepDatabase.child(id).child("steps").setValue(steps);
    }


}
