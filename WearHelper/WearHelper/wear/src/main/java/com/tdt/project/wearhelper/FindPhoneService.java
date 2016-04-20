package com.tdt.project.wearhelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * Creates a sound on the paired phone to find it.
 */
public class FindPhoneService extends IntentService {

    public static final String ACTION_TOGGLE_ALARM = "action_toggle_alarm";
    public static final String ACTION_CANCEL_ALARM = "action_alarm_off";
    private static final String TAG = "FIND_PHONE";
    private static final String FIELD_ALARM_ON = "alarm_on";
    private static final String PATH_SOUND_ALARM = "/Alarm_sound";

    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient mGoogleApiClient;

    public FindPhoneService() {
        super(FindPhoneService.class.getSimpleName());
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        GoogleClient googleClient = new GoogleClient(this, intent);
        mGoogleApiClient = googleClient.getGoogleClient(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent();
        GoogleClient googleClient = new GoogleClient(this, intent);
        mGoogleApiClient = googleClient.getGoogleClient(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        Intent broadcastIntent = new Intent();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "FindPhoneService.onHandleIntent");
        }
        if (mGoogleApiClient.isConnected()) {
            // Set the alarm off by default.
            boolean alarmOn = false;
            if (intent.getAction().equals(ACTION_TOGGLE_ALARM)) {
                broadcastIntent.setAction(FindPhoneService.ACTION_TOGGLE_ALARM);
                // Get current state of the alarm.
                DataItemBuffer result = Wearable.DataApi.getDataItems(mGoogleApiClient).await();
                try {
                    if (result.getStatus().isSuccess()) {
                        if (result.getCount() == 2) {
                            alarmOn = DataMap.fromByteArray(result.get(0).getData())
                                    .getBoolean(FIELD_ALARM_ON, false);
                            broadcastIntent.putExtra("AlarmOn", true);
                            sendBroadcast(broadcastIntent);
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
            }
            // Use alarmOn boolean to update the DataItem - phone will respond accordingly
            // when it receives the change.
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_SOUND_ALARM);
            putDataMapRequest.getDataMap().putBoolean(FIELD_ALARM_ON, alarmOn);
            putDataMapRequest.setUrgent();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                    .await();
        } else {
            broadcastIntent.setAction(FindPhoneService.ACTION_CANCEL_ALARM);
            sendBroadcast(broadcastIntent);
            Log.e(TAG, "Failed to toggle alarm on phone - Client disconnected from Google Play "
                    + "Services");
        }
        mGoogleApiClient.disconnect();
    }
}