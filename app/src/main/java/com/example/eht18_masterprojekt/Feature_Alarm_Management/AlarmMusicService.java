package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.Notification;
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
import com.example.eht18_masterprojekt.Core.NotificationController;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class AlarmMusicService extends Service {
    public static final String ACTION_STOP_ALARM = "Stop_Alarm";

    MediaPlayer mediaPlayer;
    VolumeAdjusterTask vat;
    AlarmMusicServiceStopBroadcastReceiver stopServiceReceiver = new AlarmMusicServiceStopBroadcastReceiver();
    NotificationController nc;
    Queue<Intent> startIntentQueue = new ArrayDeque<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startIntentQueue.add(intent);

        // TODO: if service gets started multiple times, only last notification is shown
        // either implement intent queue or check notificationID assignment

        nc = new NotificationController(this);
        Notification n = getAlarmTriggeredNotification(intent, nc);
        this.startForeground(nc.getCurrentNotificationID(), n);

        registerReceiver(stopServiceReceiver, new IntentFilter(ACTION_STOP_ALARM));

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
        this.stopForeground(false);

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
     * Anzeigen der Notification für das Medikament, dass den Alarm ausgelöst hat.
     * @param intent
     */
    private Notification getAlarmTriggeredNotification(Intent intent, NotificationController nc){
        long medID = intent.getLongExtra(AlarmController.ALARM_INTENT_EXTRA_MED_ID, 0);
        String medEinnahmeZeit = intent.getStringExtra(AlarmController.ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT);

        if (medID == 0 || medEinnahmeZeit.equals("")){
            throw new RuntimeException("Received undefined Med");
        }

        DatabaseAdapter da = new DatabaseAdapter(this);
        da.open();
        Medikament med = da.retrieveMedikamentWithEinnahmeDosis(medID, medEinnahmeZeit);
        da.close();

        Medikament.MedEinnahme medEinnahme = med.getEinnahmeProtokoll().getEinnahmeAt(LocalTime.parse(medEinnahmeZeit));
        return nc.getMedEinnahmeReminder(med.getBezeichnung(), medEinnahme.getEinnahmeDosis(), med.getEinheit());
    }

    /**
     * Alternative zu stopService(). Beendet den Alarm-Ton, und unterbricht den VolumeAdjusterTask.
     */
    public class AlarmMusicServiceStopBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mediaPlayer.stop();
            vat.cancel(true);
            AlarmMusicService.this.stopSelf();
        }
    }

    /**
     * Graduelles erhöhen der Lautstärke des Alarms.
     */
    class VolumeAdjusterTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            float maxVolume = 1.0f;
            float currVolume = 0.1f;

            try {
                while (true) {
                    Thread.sleep(1000 * 30); // Wait for 30 sec
                    if (currVolume < maxVolume){
                        currVolume += 0.3;
                        mediaPlayer.setVolume(currVolume, maxVolume);
                        Log.d("APP", "Volume increased.");
                    }
                }
            }
            catch (InterruptedException e){
                Log.d("APP", "AlarmVolumeManagerTask interrupted");
            }
            return null;
        }
    }
}
