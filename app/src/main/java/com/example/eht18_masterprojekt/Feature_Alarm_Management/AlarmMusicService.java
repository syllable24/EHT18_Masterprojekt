package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.eht18_masterprojekt.Core.Medikament;
import com.example.eht18_masterprojekt.Core.NotificationController;
import com.example.eht18_masterprojekt.Feature_Database.DatabaseAdapter;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AlarmMusicService extends Service {
    public static final String ACTION_STOP_ALARM = "Stop_Alarm";
    private static final String ALARM_SERVICE_NOTIFICATION_CHANNEL_ID = "AlarmMusicService";

    MediaPlayer mediaPlayer;
    VolumeAdjusterTask vat;
    AlarmMusicServiceStopBroadcastReceiver stopServiceReceiver = new AlarmMusicServiceStopBroadcastReceiver();
    NotificationController nc;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        nc = new NotificationController(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel( ALARM_SERVICE_NOTIFICATION_CHANNEL_ID, "AlarmMusicServiceNotificationChannel");
        }
        else {/*channel ID not used*/}

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

    private void nextIntent(Intent intent){

    }

    /**
     * Anzeigen der Notification für das Medikament, dass den Alarm ausgelöst hat.
     * @param intent
     */
    private Notification getAlarmTriggeredNotification(Intent intent, NotificationController nc){
        MedikamentEinnahmeGroupAlarm groupAlarm = new MedikamentEinnahmeGroupAlarm(intent.getStringExtra(AlarmController.ALARM_INTENT_EXTRA_MED_EINNAHME_GROUP));

        if (groupAlarm == null){
            throw new RuntimeException("Received empty Group Alarm");
        }
        if (groupAlarm.getAlarmTime() == null){
            throw new RuntimeException("Received Group Alarm without AlarmTime");
        }
        if (groupAlarm.getMedsToTakeIds() == null){
            throw new RuntimeException("Received Group Alarm without medIDs");
        }

        List<Long> medIDs = groupAlarm.getMedsToTakeIds();
        String medEinnahmeZeit = groupAlarm.getAlarmTime().toString();

        DatabaseAdapter da = new DatabaseAdapter(this);

        da.open();
        List<Medikament> medList = da.retrieveMedikamentListWithEinnahmeDosis(medIDs, medEinnahmeZeit);;
        da.close();

        return nc.getMedEinnahmeReminder(medList, medEinnahmeZeit, ALARM_SERVICE_NOTIFICATION_CHANNEL_ID);
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

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName ){
        NotificationChannel chan = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }
}
