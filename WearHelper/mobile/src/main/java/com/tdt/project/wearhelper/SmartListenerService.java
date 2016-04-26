package com.tdt.project.wearhelper;

import android.app.Notification;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

public class SmartListenerService extends WearableListenerService {

    private static final String TAG = "FIND_MY_PHONE";
    private static final String FIELD_ALARM_ON = "alarm_on";
    Battery battery;
    int notificationId = 1;

    private AudioManager mAudioManager;
    private static int mOrigVolume;
    private int mMaxVolume;
    private Uri mAlarmSound;
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mOrigVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        mAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = new MediaPlayer();
        battery = new Battery(this);
    }

    @Override
    public void onDestroy() {
        // Reset the alarm volume to the user's original setting.
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mOrigVolume, 0);
        mMediaPlayer.release();
        super.onDestroy();
    }

    public void sendNotification(String toSend) {
        if (toSend.isEmpty())
            toSend = "You sent an empty notification";
        Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smart Helper")
                .setContentText(toSend)
                .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        notificationManager.notify(notificationId, notification);
        notificationId += 1;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents + " for " + getPackageName());
        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, event + " deleted");
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Boolean alarmOn =
                        DataMap.fromByteArray(event.getDataItem().getData()).get(FIELD_ALARM_ON);
                if (alarmOn) {
                    mOrigVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    mMediaPlayer.reset();
                    // Sound alarm at max volume.
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mMaxVolume, 0);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    try {
                        mMediaPlayer.setDataSource(getApplicationContext(), mAlarmSound);
                        mMediaPlayer.prepare();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to prepare media player to play alarm.", e);
                    }
                    mMediaPlayer.start();
                } else if (!alarmOn)  {
                    // Reset the alarm volume to the user's original setting.
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mOrigVolume, 0);
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                }
            }
        }
    }
}