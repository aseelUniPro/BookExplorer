package com.aseel.bookexplorer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Wraps creation of the notification channel and shows a notification
 * whenever the API call succeeds or fails, as required by the project.
 */
public class NotificationHelper {

    private static final String CHANNEL_ID = "book_explorer_channel";
    private static final int NOTIFICATION_ID = 101;

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Book Explorer Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifies about API load success or failure");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showSuccess(Context context, int count) {
        show(context, "Books loaded", count + " books loaded successfully");
    }

    public static void showError(Context context, String message) {
        show(context, "Failed to load books", message);
    }

    private static void show(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        try {
            managerCompat.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // POST_NOTIFICATIONS permission not granted on Android 13+; ignore gracefully
        }
    }
}
