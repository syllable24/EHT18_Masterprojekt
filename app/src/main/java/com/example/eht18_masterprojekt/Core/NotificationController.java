package com.example.eht18_masterprojekt.Core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eht18_masterprojekt.Feature_Med_List.MainActivityView;
import com.example.eht18_masterprojekt.R;

public class NotificationController {

    Context context;
    static boolean notificationChannelCreated;
    public static final String ACTION_NOTIFICATION_DELETED = "notificationDeleted";
    public static final String EXTRA_NOTIFICATION_ID = "notificationID";

    public NotificationController(Context ctx){
        this.context = ctx;
        if (notificationChannelCreated){
            createNotificationChannel();
        }
    }

    /**
     * Anzeigen der medList Notification.
     */
    public void displayMedListNotification(int notificationID){
        String strContentText = "Dauermedikationsalarme werden ausgelöst.";

        Intent i = new Intent(context, MainActivityView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medList");
        builder.setContentTitle("Dauermedikationsalarme")
                .setContentText(strContentText)
                .setSmallIcon(R.mipmap.ic_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notificationID, builder.build());
    }

    /**
     * Einrichten eines Notification Channels zum Darstellen von Notifications
     * für Android 8.0 und höher.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Dauermedikationsliste";
            String description = "Dauermedikationsliste";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("medList", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationChannelCreated = true;
        }
    }

    /**
     * Anzeigen einer Notification für einen Med-Einnahme Alarm.
     *
     * @param notificationID
     * @param medName
     * @param medAnzahl
     * @param medEinheit
     */
    public void displayAlarmNotification(int notificationID, String medName, String medAnzahl, String medEinheit){

        // TODO: Multiple Notifications get grouped together, leading to cancelling them as one -> only one Broadcast gets sent.

        Intent i = new Intent(context, NotificationDeletedReceiver.class);
        i.putExtra(EXTRA_NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medList");
        builder.setContentTitle(medName + " einnehmen")
                .setContentText("Bitte " + medAnzahl + " " + medEinheit + " " + medName + " einnehmen")
                .setSmallIcon(R.mipmap.ic_info)
                .setDeleteIntent(pendingIntent);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(notificationID, builder.build());

        Log.d("APP", "Notification ID: " + notificationID + " wird angezeigt");
    }
}
