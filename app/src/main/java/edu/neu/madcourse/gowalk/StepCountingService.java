package edu.neu.madcourse.gowalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class StepCountingService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    private static final String TAG = StepCountingService.class.getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "StepCountingService starts.");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor != null) {
            Log.v(TAG, "Register listener to SensorManager");
            final boolean result = sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST);
            if (!result) {
                Log.e(TAG, "Failed to register listener to step count sensor");
            }
        } else {
            Log.e(TAG, "Failed to obtain step count sensor!!!");
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
