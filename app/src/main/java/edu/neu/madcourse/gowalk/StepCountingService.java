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
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import edu.neu.madcourse.gowalk.activity.HomepageActivity;
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
    private final MutableLiveData<Integer> currentStepLiveData = new MutableLiveData<>();
    private final IBinder binder = new StepCountingBinder(this);
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private NotificationManager notificationManager;
    private int currentStep;
    private final BroadcastReceiver shutDownListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SHUTDOWN)) {
                Log.d(TAG, "Device is shutting down");
                // since the sensor will be reset, we set the offset to be the negate of current
                // step,
                // so that we can get the current step back while device is reboot
                SharedPreferencesUtil.setStepOffset(context, -currentStep);
                SharedPreferencesUtil.setLastRecordTime(StepCountingService.this,
                        System.currentTimeMillis());
            }
        }
    };
    private int stepOffset;
    private boolean hasSendGoalCompletionForToday = false;
    /**
     * Last updated timestamp of the sensor event since the device boot in nanoseconds.
     */
    private long lastUpdatedTimestampSinceBootNanos;
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
                if (!hasSendGoalCompletionForToday && currentStep >= SharedPreferencesUtil.getDailyStepGoal(StepCountingService.this)) {
                    onDailyGoalComplete(currentStep);
                }
                //update UI of activity
                currentStepLiveData.setValue(currentStep);

                //update last record date in shared preference
                SharedPreferencesUtil.setLastRecordTime(StepCountingService.this,
                        System.currentTimeMillis());

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

    private int minutesSinceLastSyncWithFirebase = 0;
    private final static int intervalForFirebaseSync = 10;

    private final BroadcastReceiver timeChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_TIME_TICK) ||
                    intent.getAction().equals(ACTION_TIME_CHANGED) ||
                    intent.getAction().equals(ACTION_DATE_CHANGED)) {
                Log.v(TAG, "Time change event received.");

                long lastRecordTime =
                        SharedPreferencesUtil.getLastRecordTime(StepCountingService.this);

                if(lastRecordTime == 0) {
                    return;
                }

                if (!DateUtils.isToday(lastRecordTime)) {
                    Log.d(TAG, "LastRecordTime " + lastRecordTime);
                    //if the last record date is not today
                    LocalDate date =
                            Instant.ofEpochMilli(lastRecordTime).atZone(ZoneId.systemDefault()).toLocalDate();

                    saveToFirebase(date, currentStep);
                    hasSendGoalCompletionForToday = false;
                    //add yesterday's step to offset
                    stepOffset += currentStep;
                    SharedPreferencesUtil.setStepOffset(context, stepOffset);

                    Log.d(TAG, "set current step to 0");
                    currentStep = 0;
                    currentStepLiveData.setValue(currentStep);
                    updateNotification();
                } else {
                    if (minutesSinceLastSyncWithFirebase >= intervalForFirebaseSync) {
                        saveToFirebase(LocalDate.now(), currentStep);
                        minutesSinceLastSyncWithFirebase = 0;
                    } else {
                        minutesSinceLastSyncWithFirebase++;
                    }
                }

            }
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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

            long lastRecordTime = SharedPreferencesUtil.getLastRecordTime(StepCountingService.this);

            if (lastRecordTime != 0) {
                if (DateUtils.isToday(lastRecordTime)) {
                    //if the last record date is today
                    stepOffset = SharedPreferencesUtil.getStepOffset(this);
                } else {
                    //if the last record date is not today
                    LocalDate date =
                            Instant.ofEpochMilli(lastRecordTime).atZone(ZoneId.systemDefault()).toLocalDate();
                    saveToFirebase(date, -SharedPreferencesUtil.getStepOffset(this));
                    SharedPreferencesUtil.setStepOffset(this, 0);
                    SharedPreferencesUtil.setLastRecordTime(StepCountingService.this, System.currentTimeMillis());
                }
            }
            Log.v(TAG, "Retrieved step offset " + stepOffset);
            final boolean result = sensorManager.registerListener(sensorEventListener,
                    stepCountSensor,
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
                .setSmallIcon(R.drawable.footprint)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        SharedPreferencesUtil.setStepOffset(this, -currentStep);
        SharedPreferencesUtil.setLastRecordTime(this, System.currentTimeMillis());

        sensorManager.unregisterListener(sensorEventListener, stepCountSensor);
        unregisterReceiver(timeChangeListener);
    }

    public LiveData<Integer> getCurrentStep() {
        Log.v(TAG, "get current step");
        return currentStepLiveData;
    }

    private void saveToFirebase(LocalDate date, int currentStep) {
        Log.d(TAG, "Saving data into Firebase, current step " + currentStep + " date " +
                date.toString());

        FCMUtil.sendDailyStep(SharedPreferencesUtil.getUserId(this),
                SharedPreferencesUtil.getUsername(this), currentStep, date);
    }

    private void onDailyGoalComplete(int steps) {
        sendGoalCompletionMsgToFirebase(steps);
        SharedPreferencesUtil.setAccumulatePoints(this,
                SharedPreferencesUtil.getAccumulatePoints(this) + SharedPreferencesUtil.getPointsGainedForDailyGoal(this));
    }

    private void sendGoalCompletionMsgToFirebase(int steps) {
        String username;
        if (SharedPreferencesUtil.getUsername(this).equals("")) {
            username = "A user";
        } else {
            username = SharedPreferencesUtil.getUsername(this);
        }

        hasSendGoalCompletionForToday = true;


        String msgTitle = getString(R.string.goal_completion_title, username);
        String msgBody = getString(R.string.goal_completion_body, username, steps,
                SharedPreferencesUtil.getPointsGainedForDailyGoal(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                FCMUtil.sendMessageToTopic(msgTitle, msgBody,
                        getString(R.string.goal_completion_topic));
            }
        }).start();
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
