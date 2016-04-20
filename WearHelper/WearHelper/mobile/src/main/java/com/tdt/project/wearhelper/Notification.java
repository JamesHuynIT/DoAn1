package com.tdt.project.wearhelper;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.IOException;

/**
 * Created by NhanHuynh on 4/17/2016.
 */
public class Notification extends WearableListenerService {
    int notificationId = 1;
    Battery battery;

    private static final String TAG = "PHONE_STATUS";
    private static final String PHONE_STATUS = "phone_status";

    @Override
    public void onCreate() {
        super.onCreate();
        battery = new Battery(Notification.this);
        if(battery.getBatteryLevel(Notification.this) < 20){
            showNotification("Battery is low");
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        battery = new Battery(Notification.this);
        if(battery.getBatteryLevel(Notification.this) < 20){
            showNotification("Battery is low");
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents + " for " + getPackageName());
        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, event + " deleted");
            } else if (event.getType() == DataEvent.TYPE_CHANGED  ) {
                Boolean phoneStatus =
                        DataMap.fromByteArray(event.getDataItem().getData()).get(PHONE_STATUS);
                if(phoneStatus) {
                    String toSend = "Battery now is " + battery.getBatteryLevel(Notification.this) + "%";
                    showNotification(toSend);
                }
                else {
                    Log.e(TAG, "Failed to notification.");
                }
            }
        }
    }

    public void showNotification(String toSend){
        if(toSend.isEmpty())
            toSend = "You sent an empty notification";
        android.app.Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smart Helper")
                .setContentText(toSend)
                .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        notificationManager.notify(notificationId, notification);
        notificationId = notificationId + 1;
    }
}