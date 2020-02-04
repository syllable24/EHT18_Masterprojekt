package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eht18_masterprojekt.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startMusicService = new Intent(context, AlarmMusicService.class);
        context.startService(startMusicService);

        displayMedAlarmNotification(context ,intent.getExtras().getString("medName"));
        Log.d("APP", "Med Alarm für: " + intent.getExtras().getString("medName") + " erhalten");
    }

    /**
     * Anzeigen der medList Notification.
     */
    private void displayMedAlarmNotification(Context context, String medBezeichnung){
        String strContentText = "Med Alarm für " + medBezeichnung + " ausgelöst";

        Intent i = new Intent(AlarmMusicService.ACTION_STOP_ALARM);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medList");
        builder.setContentTitle("Dauermedikationsalarme")
                .setContentText(strContentText)
                .setSmallIcon(R.mipmap.ic_info)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
