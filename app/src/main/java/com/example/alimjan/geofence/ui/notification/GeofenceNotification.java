package com.example.alimjan.geofence.ui.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import com.example.alimjan.geofence.R;
import com.example.alimjan.geofence.ui.activity.MainActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Easy to use notification wrapper for geofence notification, reason I use separate class
 * to display a notification is to make implementation concise and clean. And this is also a common
 * pattern in Android.
 */
public class GeofenceNotification {

    // The unique identifier for notification.
    @SuppressWarnings("unused")
    public static final int NOTIFICATION_ID_GEOFENCE_ENTER = 1 << 1;
    @SuppressWarnings("unused")
    public static final int NOTIFICATION_ID_GEOFENCE_EXIT = 1 << 2;

    // The unique identifier for notification channel.
    private static final String CHANNEL_ID = "Geofence Notification";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     */
    public static void notify(final Context context, final String title, final String message, int notificationId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        createNotificationChannel(context);

        // Create PendingIntent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup notification
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(pendingIntent)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        // Notify:)
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    /**
     * Cancels any notifications of this type previously shown using
     */
    @SuppressWarnings("unused")
    public static void cancel(final Context context, final int notificationId) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(notificationId);
        }
    }

    /**
     * Creates notification channel, this method have to be called before you fire notification
     * because without valid {@link NotificationChannel} notification will fail.
     *
     * @param context The Context.
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Resources res = context.getResources();

            CharSequence name = res.getString(R.string.channel_name);
            String description = res.getString(R.string.channel_description);
            final int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
