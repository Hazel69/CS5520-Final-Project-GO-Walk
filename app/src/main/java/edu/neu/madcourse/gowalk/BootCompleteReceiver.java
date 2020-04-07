package edu.neu.madcourse.gowalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

/**
 * A BroadcastReceiver which listens to {@link ACTION_BOOT_COMPLETED} after the device boot. So
 * every time the system boot, the service will start to run
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private static final String TAG = BootCompleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(ACTION_BOOT_COMPLETED)) {
            Log.w(TAG, "Should not receive intent " + intent.getAction());
            return;
        }
        Log.d(TAG, "BootCompleted broadcast received, starting StepCountingService");
        Intent serviceIntent = new Intent(context, StepCountingService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
