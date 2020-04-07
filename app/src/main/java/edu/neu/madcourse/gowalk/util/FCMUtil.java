package edu.neu.madcourse.gowalk.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

import edu.neu.madcourse.gowalk.model.DailyStep;

public final class FCMUtil {
    private static final String TAG = FCMUtil.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAUV_nk_s:APA91bH9a-CchSZdc_smEYktmBM7-XSkVbgiEAcDchEKrLg6RqaMknNH4rO0id9OYTpvBRLwpCANQiWKaJIc_atgOqI3YhlP4_5AyTM3qAnlcGPQvcGUagavS0COGiKiyA4RO4DF4g97";

    public static void sendMessageToTopic(String msgTitle, String msgBody, String topic){
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
//            jNotification.put("message", "Jessie Meets Daily Goal!");
            jNotification.put("title", msgTitle);
            jNotification.put("body", msgBody);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/" + topic);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: " + resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public static void sendDailyStep(String userId, String username, int steps, LocalDate date) {
        DatabaseReference stepDatabase = FirebaseDatabase.getInstance().getReference()
                .child("dailySteps");

        Log.d(TAG, "sent daily step to firebase");
        DailyStep dailyStepF = new DailyStep(userId, username, date.toString(), steps);
        stepDatabase.child(date.toString()).child(userId).setValue(dailyStepF);
    }

}
