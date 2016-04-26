package com.tdt.project.wearhelper;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

public class BatteryStatusService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String GET_PHONE_STATUS = "action_get_status";
    public static final String FAIL_GET_PHONE_STATUS = "action_fail_get_status";
    private static final String TAG = "PHONE_STATUS";
    private static final String PHONE_STATUS = "phone_status";
    private static final String PATH_STATUS = "/PhoneStatus";

    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    private static final long CONNECTION_TIME_OUT_MS = 100;
    int notificationId = 1;
    private GoogleApiClient mGoogleApiClient;

    public BatteryStatusService() {
        super(BatteryStatusService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        Intent broadcastIntentConnnect = new Intent();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "BatteryStatusService.onHandleIntent");
        }
        if (mGoogleApiClient.isConnected()) {
            // Set the alarm off by default.
            boolean phoneStatus = false;
            if (intent.getAction().equals(GET_PHONE_STATUS)) {
                broadcastIntentConnnect.setAction(BatteryStatusService.GET_PHONE_STATUS);
                // Get current state of the alarm.
                DataItemBuffer result = Wearable.DataApi.getDataItems(mGoogleApiClient).await();
                try {
                    if (result.getStatus().isSuccess()) {
                        if (result.getCount() == 4) {
                            phoneStatus = DataMap.fromByteArray(result.get(3).getData())
                                    .getBoolean(PHONE_STATUS, false);
                            broadcastIntentConnnect.putExtra("phoneStatus", true);
                            sendBroadcast(broadcastIntentConnnect);
                        } else {
                            Log.e(TAG, "Unexpected number of DataItems found.\n"
                                    + "\tExpected: 1\n"
                                    + "\tActual: " + result.getCount());
                        }
                    } else if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "onHandleIntent: failed to get current alarm state");
                    }
                } finally {
                    result.release();
                }
                // Toggle alarm.
                phoneStatus = !phoneStatus;
            }
            // Use alarmOn boolean to update the DataItem - phone will respond accordingly
            // when it receives the change.
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_STATUS);
            putDataMapRequest.getDataMap().putBoolean(PHONE_STATUS, phoneStatus);
            putDataMapRequest.setUrgent();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                    .await();
        } else {
            broadcastIntentConnnect.setAction(BatteryStatusService.FAIL_GET_PHONE_STATUS);
            sendBroadcast(broadcastIntentConnnect);
            Log.e(TAG, "Failed to get battery status - Client disconnected from Google Play "
                    + "Services");
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smart Helper")
                .setContentText("CONNECT LOST")
                .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());

        notificationManager.notify(notificationId, notification);
        notificationId += 1;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Notification notification = new NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smart Helper")
                .setContentText("CAN'T CONNECT")
                .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());

        notificationManager.notify(notificationId, notification);
        notificationId += 1;
    }
}