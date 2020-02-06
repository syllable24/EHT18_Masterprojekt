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

public class AlarmMusicService extends Service {
    private static final int ALARM_NOTIFICATION_ID = 100;
    public static final String ACTION_STOP_ALARM = "Stop_Alarm";

    MediaPlayer mediaPlayer;
    VolumeAdjusterTask vat;
    AlarmMusicServiceStopBroadcastReceiver stopServiceReceiver = new AlarmMusicServiceStopBroadcastReceiver();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getLongExtra(AlarmController.ALARM_INTENT_EXTRA_MED_ID, 0) == 0){
            Log.e("APP", "Received undefined Med");
            this.stopSelf();
        }

        registerReceiver(stopServiceReceiver, new IntentFilter(ACTION_STOP_ALARM));

        displayNotification(intent);

        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mediaPlayer = MediaPlayer.create(this, alarmTone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        vat = new VolumeAdjusterTask();
        vat.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        mediaPlayer.stop();
        vat.cancel(true);
        unregisterReceiver(stopServiceReceiver);
        return super.stopService(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class AlarmMusicServiceStopBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            stopService(intent);
        }
    }

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
                Log.e("APP", "AlarmVolumeManagerTask interrupted");
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Anzeigen der Notification für das Medikament, dass den Alarm ausgelöst hat.
     * @param intent
     */
    private void displayNotification(Intent intent){
        // TODO: Keeps receiving some null meds

        long medID = intent.getLongExtra(AlarmController.ALARM_INTENT_EXTRA_MED_ID, 0);
        String medEinnahmeZeit = intent.getStringExtra(AlarmController.ALARM_INTENT_EXTRA_MED_EINNAHME_ZEIT);

        DatabaseAdapter da = new DatabaseAdapter(this);
        da.open();
        Medikament med = da.retrieveMedikamentWithEinnahmeDosis(medID, medEinnahmeZeit);
        da.close();

        MedikamentEinnahme mea = med.getEinnahmeZeiten();
        String einnahmeDosis = mea.getEinnahmeDosis(LocalTime.parse(medEinnahmeZeit));

        new NotificationController(this).displayAlarmNotification(ALARM_NOTIFICATION_ID, med.getBezeichnung(), einnahmeDosis , med.getEinheit());
    }
}
