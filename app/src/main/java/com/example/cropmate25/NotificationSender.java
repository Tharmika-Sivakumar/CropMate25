package com.example.cropmate25;

import android.os.AsyncTask;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationSender {

    private static final String FCM_SEND_ENDPOINT = "https://fcm.googleapis.com/v1/projects/YOUR_PROJECT_ID/messages:send";

    public static void sendNotification(String credentialsPath, String title, String message) {
        new SendNotificationTask().execute(credentialsPath, title, message);
    }

    private static class SendNotificationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                String credentialsPath = params[0];
                String title = params[1];
                String message = params[2];

                String accessToken = GoogleAuthHelper.getAccessToken(credentialsPath);

                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", message);

                JSONObject messageObject = new JSONObject();
                messageObject.put("notification", notification);
                messageObject.put("topic", "all");

                JSONObject root = new JSONObject();
                root.put("message", messageObject);

                URL url = new URL(FCM_SEND_ENDPOINT.replace("YOUR_PROJECT_ID", "cropmate25"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);

                OutputStream os = conn.getOutputStream();
                os.write(root.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Successfully sent
                } else {
                    // Failed to send
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
