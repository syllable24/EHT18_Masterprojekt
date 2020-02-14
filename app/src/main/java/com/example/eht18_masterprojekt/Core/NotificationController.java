package com.example.eht18_masterprojekt.Core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eht18_masterprojekt.Feature_Alarm_Management.MedicationAlarm;
import com.example.eht18_masterprojekt.Feature_Med_List.MainActivityView;
import com.example.eht18_masterprojekt.R;

public class NotificationController {

    Context context;
    private static final String NOTIFICATION_CHANNEL_NAME = "medList";
    public static boolean notificationChannelCreated;
    public static final String ACTION_NOTIFICATION_DELETED = "notificationDeleted";
    public static final String EXTRA_NOTIFICATION_ID = "notificationID";
    public static final int NOTIFICATION_BASE_ID = 100;
    private static int notificationID = NOTIFICATION_BASE_ID;


    public NotificationController(Context ctx){
        this.context = ctx;
        if (notificationChannelCreated){
            createNotificationChannel();
        }
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
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_NAME, name, importance);
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
     * @param medName
     * @param medAnzahl
     * @param medEinheit
     */
    public Notification getMedEinnahmeReminder(String medName, String medAnzahl, String medEinheit){
        // TODO: Multiple Notifications get grouped together, leading to cancelling them as one -> only one Broadcast gets sent.
        int notID = getNextNotificationID();
        Intent i = new Intent(context, NotificationDeletedReceiver.class);
        i.putExtra(EXTRA_NOTIFICATION_ID, notID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME);
        builder.setContentTitle(medName + " einnehmen")
                .setContentText("Bitte " + medAnzahl + " " + medEinheit + " " + medName + " einnehmen")
                .setSmallIcon(R.mipmap.ic_info)
                .setDeleteIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true);

        Log.d("APP", "Notification ID: " + notID + " erzeugt");
        return builder.build();
    }

    /**
     * Anzeigen der Notifications für die individuellen Einnahmezeiten des übergebenen Medikaments.
     *
     * @param med
     */
    public void displayAlarmNotifications(Medikament med){

        // TODO: ContentIntent -> Anzeigen der MedListView

        for (Medikament.MedEinnahme medEinnahme : med.getEinnahmeProtokoll()){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_NAME);
            builder.setContentTitle(med.getBezeichnung())
                    .setContentText(medEinnahme.toString())
                    .setSmallIcon(R.mipmap.ic_info)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true);

            NotificationManagerCompat nm = NotificationManagerCompat.from(context);
            nm.notify(medEinnahme.getNotificationID(), builder.build());
        }
    }

    /**
     * Retournieren der aktuellen NotificationID.
     * @return
     */
    public int getCurrentNotificationID(){
        return notificationID;
    }

    /**
     * Retournieren der nächsten freien NorificationID.
     * @return
     */
    private int getNextNotificationID(){
        if (notificationID == Integer.MAX_VALUE){
            notificationID = NOTIFICATION_BASE_ID;
        }
        return notificationID++;
    }
}
