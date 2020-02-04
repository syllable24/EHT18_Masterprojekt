package com.example.eht18_masterprojekt.Feature_Alarm_Management;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.eht18_masterprojekt.R;

public class AlarmMusicService extends Service {
    MediaPlayer mediaPlayer;
    VolumeAdjusterTask vat;
    AlarmMusicServiceStopBroadcastReceiver stopServiceReceiver = new AlarmMusicServiceStopBroadcastReceiver();

    public static final String ACTION_STOP_ALARM = "Stop_Alarm";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            registerReceiver(stopServiceReceiver, new IntentFilter(ACTION_STOP_ALARM));

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer = MediaPlayer.create(this, R.raw.money_alarm); //TODO: Insert professional alarm
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                vat = new VolumeAdjusterTask();
                vat.execute();
            }

        }
        catch (Exception e) {
            Log.e("APP", "Can't open Alarm-Music File");
            e.printStackTrace();
        }
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
                    Thread.sleep(1000 * 120); // Wait for 2 minutes
                    currVolume += 0.1;
                }
            }
            catch (InterruptedException e){
                Log.e("APP", "AlarmVolumeManagerTask interrupted");
                e.printStackTrace();
            }
            return null;
        }
    }
}
