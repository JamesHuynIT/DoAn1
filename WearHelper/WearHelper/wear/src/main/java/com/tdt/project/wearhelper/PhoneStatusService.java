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

public class PhoneStatusService extends IntentService {

    public static final String GET_PHONE_STATUS = "action_get_status";
    public static final String FAIL_GET_PHONE_STATUS = "action_fail_get_status";
    private static final String TAG = "PHONE_STATUS";
    private static final String PHONE_STATUS = "phone_status";
    private static final String PATH_STATUS = "/PhoneStatus";

    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private GoogleApiClient mGoogleApiClient;

    public PhoneStatusService() {
        super("PhoneStatusService");
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
        Intent broadcastIntentConnnect = new Intent();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "FindPhoneService.onHandleIntent");
        }
        if (mGoogleApiClient.isConnected()) {
            // Set the alarm off by default.
            boolean phoneStatus = false;
            if (intent.getAction().equals(GET_PHONE_STATUS)) {
                broadcastIntentConnnect.setAction(PhoneStatusService.GET_PHONE_STATUS);
                // Get current state of the alarm.
                DataItemBuffer result = Wearable.DataApi.getDataItems(mGoogleApiClient).await();
                try {
                    if (result.getStatus().isSuccess()) {
                        if (result.getCount() == 2) {
                            phoneStatus = DataMap.fromByteArray(result.get(0).getData())
                                    .getBoolean(PHONE_STATUS, false);
                            broadcastIntentConnnect.putExtra("phoneStatus", true);
                            sendBroadcast(broadcastIntentConnnect);
                        } else {
                            Log.e(TAG, "Unexpected number of DataItems found.\n"
                                    + "\tExpected: 1\n"
                                    + "\tActual: " + result.getCount());
                        }
                    } else if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "onHandleIntent: failed to get current phone state");
                    }
                } finally {
                    result.release();
                }
            }
            // Use alarmOn boolean to update the DataItem - phone will respond accordingly
            // when it receives the change.
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_STATUS);
            putDataMapRequest.getDataMap().putBoolean(PHONE_STATUS, phoneStatus);
            putDataMapRequest.setUrgent();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                    .await();
        } else {
            broadcastIntentConnnect.setAction(PhoneStatusService.FAIL_GET_PHONE_STATUS);
            sendBroadcast(broadcastIntentConnnect);
            Log.e(TAG, "Failed to toggle status phone - Client disconnected from Google Play "
                    + "Services");
        }
        mGoogleApiClient.disconnect();
    }
}