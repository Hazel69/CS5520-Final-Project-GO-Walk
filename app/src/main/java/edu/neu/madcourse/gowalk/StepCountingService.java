package edu.neu.madcourse.gowalk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.Date;

import edu.neu.madcourse.gowalk.activity.HomepageActivity;
import edu.neu.madcourse.gowalk.model.DailyStep;
import edu.neu.madcourse.gowalk.repository.DailyStepRepository;
import edu.neu.madcourse.gowalk.util.FCMUtil;
import edu.neu.madcourse.gowalk.util.SharedPreferencesUtil;

import static android.content.Intent.ACTION_DATE_CHANGED;
import static android.content.Intent.ACTION_SHUTDOWN;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static android.content.Intent.ACTION_TIME_TICK;

/**
 * A foreground service running all the time.
 */
public class StepCountingService extends Service {

    private static final String TAG = StepCountingService.class.getSimpleName();
    private static final String ID_NOTIFICATION_CHANNEL =
            "gowalk-step-counting-notification-channel";
    private static final int ID_NOTIFICATION = 1001;
    /**
     * Used to launch HomepageActivity when touching the notification.
     */
    private static final int REQUEST_CODE_HOMEPAGE_ACTIVITY = 1002;

    private static DailyStepRepository dailyStepRepository;
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private NotificationManager notificationManager;
    private int currentStep;
    private int stepOffset;
    /**
     * Last updated timestamp of the sensor event since the device boot in nanoseconds.
     */
    private long lastUpdatedTimestampSinceBootNanos;
    private final MutableLiveData<Integer> currentStepLiveData = new MutableLiveData<>();

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (event.timestamp <= lastUpdatedTimestampSinceBootNanos) {
                    Log.w(TAG, "Event has already been processed, skip it.");
                    return;
                }
                Log.d(TAG, "Updating step count to " + event.values[0] +
                        " last updated timestamp is " + event.timestamp);
                currentStep = (int) event.values[0] - stepOffset;

                //if user achieve user's daily goal, use firebase to send notification
                if (shouldSendGoalCompleteNotification(currentStep)) {
                    //TODO: send goal completing message to firebase
                }
                //update UI of activity
                currentStepLiveData.setValue(currentStep);

                //event's timestamp starts from when the phone is reboot
                lastUpdatedTimestampSinceBootNanos = event.timestamp;
                updateNotification();
            } else {
                Log.e(TAG, "Receiving event from sensor type: " + event.sensor.getName());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "Sensor " + sensor.getName() + " has accuracy changed to " + accuracy);
        }
    };

    private final BroadcastReceiver timeChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_TIME_TICK) ||
                    intent.getAction().equals(ACTION_TIME_CHANGED) ||
                    intent.getAction().equals(ACTION_DATE_CHANGED)) {
                Log.v(TAG, "Time change event received.");

                //the time of previous sensor change
                long lastUpdatedTime = System.currentTimeMillis() - SystemClock.elapsedRealtime() +
                        (long) (lastUpdatedTimestampSinceBootNanos / 1e6);

                Log.d(TAG, "CurrentTimeStamp"+ new java.util.Date(System.currentTimeMillis()));
                // lastUpdatedTimestampSinceBootNanos will be great than 0 only if onStartCommand has been called,
                // and at least one SensorEvent has been received.
                Log.d(TAG, "lastUpdatedTimestampSinceBootNanos " + lastUpdatedTimestampSinceBootNanos);
                Log.d(TAG, "is lastUpdatedTime today" +DateUtils.isToday(lastUpdatedTime));

                if (lastUpdatedTimestampSinceBootNanos > 0 && DateUtils.isToday(lastUpdatedTime)) {
                    // we may lose some steps if the SensorEvent has not been received yet
                    Log.d(TAG, "Saving data into DB, current step " + currentStep + " last updated " + lastUpdatedTime);
                    //if the last updated time is the day before, save to db
                    saveDataToDB(new Date(lastUpdatedTime), currentStep);

                    stepOffset = currentStep;
                    SharedPreferencesUtil.setStepOffset(context, stepOffset);
                    currentStep = 0;

                    Log.d(TAG, "Current time " + lastUpdatedTimestampSinceBootNanos);
                    updateNotification();
                }
            }
        }
    };

    private final BroadcastReceiver shutDownListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SHUTDOWN)) {
                Log.d(TAG, "Device is shutting down");
                // since the sensor will be reset, we set the offset to be the negate of current step,
                // so that we can get the current step back while device is reboot
                SharedPreferencesUtil.setStepOffset(context, -currentStep);
            }
        }
    };

    private final IBinder binder = new StepCountingBinder(this);

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        dailyStepRepository = new DailyStepRepository(getApplication());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TIME_TICK);
        intentFilter.addAction(ACTION_TIME_CHANGED);
        intentFilter.addAction(ACTION_DATE_CHANGED);
        registerReceiver(timeChangeListener, intentFilter);
        // TODO: when to unregister shutdown listener?
        registerReceiver(shutDownListener, new IntentFilter(ACTION_SHUTDOWN));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service is bound");
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "StepCountingService starts.");

        if (stepCountSensor != null) {
            Log.v(TAG, "Register listener to SensorManager");
            stepOffset = SharedPreferencesUtil.getStepOffset(this);
            Log.v(TAG, "Retrieved step offset " + stepOffset);
            final boolean result = sensorManager.registerListener(sensorEventListener, stepCountSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
            if (!result) {
                Log.e(TAG, "Failed to register listener to step count sensor");
            }
        } else {
            Log.e(TAG, "Failed to obtain step count sensor!!!");
        }
        createNotificationChannel();
        Notification notification = buildNewNotification();
        startForeground(ID_NOTIFICATION, notification);
        notificationManager.notify(ID_NOTIFICATION, notification);

        return START_STICKY;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(ID_NOTIFICATION_CHANNEL,
                "Current step", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("This notification shows your current step.");

        notificationManager.createNotificationChannel(channel);
    }

    private void updateNotification() {
        notificationManager.notify(ID_NOTIFICATION, buildNewNotification());
    }

    private Notification buildNewNotification() {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        REQUEST_CODE_HOMEPAGE_ACTIVITY,
                        new Intent(this, HomepageActivity.class),
                        PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, ID_NOTIFICATION_CHANNEL)
                .setContentTitle("GO WALK! Your current step is")
                .setContentText(String.valueOf(currentStep))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.homepage_icon)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        sensorManager.unregisterListener(sensorEventListener, stepCountSensor);
        unregisterReceiver(timeChangeListener);
    }

    public LiveData<Integer> getCurrentStep() {
        Log.v(TAG, "get current step");
        return currentStepLiveData;
    }

    private void saveDataToDB(Date date, int currentStep) {
        DailyStep dailyStep = new DailyStep(date, currentStep);
        dailyStepRepository.insertDailyStep(dailyStep);
    }

    private boolean shouldSendGoalCompleteNotification(int steps) {
        Log.i(TAG, "Daily step goal:" + SharedPreferencesUtil.getDailyStepGoal(this));
        return steps >= SharedPreferencesUtil.getDailyStepGoal(this);
    }

    public static class StepCountingBinder extends Binder {

        private StepCountingService stepCountingService;

        StepCountingBinder(StepCountingService stepCountingService) {
            this.stepCountingService = stepCountingService;
        }

        public StepCountingService getService() {
            return stepCountingService;
        }
    }
}
