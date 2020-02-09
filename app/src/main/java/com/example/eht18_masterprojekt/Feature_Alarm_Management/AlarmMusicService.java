package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.MedikamentEinnahme;
import com.example.eht18_masterprojekt.Core.NotificationController;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AlarmMusicService extends Service {
    private static Integer notificationID = 100;
    public static final String ACTION_STOP_ALARM = "Stop_Alarm";

    MediaPlayer mediaPlayer;
    VolumeAdjusterTask vat;
    AlarmMusicServiceStopBroadcastReceiver stopServiceReceiver = new AlarmMusicServiceStopBroadcastReceiver();
    NotificationController nc;

    ArrayList<Integer> activeNotificationIDs = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(stopServiceReceiver, new IntentFilter(ACTION_STOP_ALARM));

        displayNotification(intent);

        if (mediaPlayer == null) {
            Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer = MediaPlayer.create(this, alarmTone);
            mediaPlayer.setLooping(true);
        }

        if (mediaPlayer.isPlaying() != true) {
            mediaPlayer.start();
            vat = new VolumeAdjusterTask();
            vat.execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(stopServiceReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Alternative zu stopService(). Beendet den Alarm-Ton, unterbricht den VolumeAdjusterTask und
     * löscht die NotificationID der ID aus activeNotificationIDs.
     */
    public class AlarmMusicServiceStopBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Integer notificationIDCancelled = intent.getIntExtra("notificationID", 0);

            if(notificationIDCancelled != 0){
                activeNotificationIDs.remove(notificationIDCancelled);
                Integer max = Collections.max(activeNotificationIDs);
                notificationID = max;
            }

            mediaPlayer.stop();
            vat.cancel(true);
            AlarmMusicService.this.stopSelf();
        }
    }

    /**
     * Graduelles erhöhen der Lautstärke des Alarms. Beendet den Alarm nach 5 Minuten.
     */
    class VolumeAdjusterTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            float maxVolume = 1f;
            float currVolume = 0.1f;

            try {
                for (int i = 0; i < 5; i++) {
                    mediaPlayer.setVolume(currVolume, maxVolume);
                    Thread.sleep(1000 * 60); // Wait for 1 minute
                    currVolume += 0.1;
                }
                AlarmMusicService.this.stopSelf(); // Cancel Alarm after 5 Minutes
            }
            catch (InterruptedException e){
                Log.d("APP", "AlarmVolumeManagerTask interrupted");
            }
            return null;
        }
    }

    /**
     * Anzeigen der Notification für das Medikament, dass den Alarm ausgelöst hat.
     * @param intent
     */
    private void displayNotification(Intent intent){
        long medID = intent.getLongExtra(AlarmController.ALARM_INTENT_EXTRA_MED_ID, 0);
        String medEinnahmeZeit = intent.getStringExtra(AlarmController.ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT);

        if (medID == 0 || medEinnahmeZeit.equals("")){
            throw new RuntimeException("Received undefined Med");
        }

        DatabaseAdapter da = new DatabaseAdapter(this);
        da.open();
        Medikament med = da.retrieveMedikamentWithEinnahmeDosis(medID, medEinnahmeZeit);
        da.close();

        MedikamentEinnahme mea = med.getEinnahmeZeiten();
        String einnahmeDosis = mea.getEinnahmeDosis(LocalTime.parse(medEinnahmeZeit));

        nc = new NotificationController(this);

        activeNotificationIDs.add(new Integer(notificationID++));
        nc.displayAlarmNotification(notificationID, med.getBezeichnung(), einnahmeDosis , med.getEinheit());
    }
}
