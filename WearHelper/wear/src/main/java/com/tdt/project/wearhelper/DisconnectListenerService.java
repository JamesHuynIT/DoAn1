package com.tdt.project.wearhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.Set;

public class DisconnectListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "FIND_PHONE";

    private static final int FORGOT_PHONE_NOTIFICATION_ID = 1;

    /* the capability that the phone app would provide */
    private static final String FIND_ME_CAPABILITY_NAME = "find_me";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onConnectedNodes(List<Node> connectedNodes) {
        // After we are notified by this callback, we need to query for the nodes that provide the
        // "find_me" capability and are directly connected.
        if (mGoogleApiClient.isConnected()) {
            setOrUpdateNotification();
        } else if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    private void setOrUpdateNotification() {
        Wearable.CapabilityApi.getCapability(
                mGoogleApiClient, FIND_ME_CAPABILITY_NAME,
                CapabilityApi.FILTER_REACHABLE).setResultCallback(
                new ResultCallback<CapabilityApi.GetCapabilityResult>() {
                    @Override
                    public void onResult(CapabilityApi.GetCapabilityResult result) {
                        if (result.getStatus().isSuccess()) {
                            updateFindMeCapability(result.getCapability());
                        } else {
                            Log.e(TAG,
                                    "setOrUpdateNotification() Failed to get capabilities, "
                                            + "status: "
                                            + result.getStatus().getStatusMessage());
                        }
                    }
                });
    }

    private void updateFindMeCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();
        if (connectedNodes.isEmpty()) {
            setupLostConnectivityNotification();
        } else {
            for (Node node : connectedNodes) {
                // we are only considering those nodes that are directly connected
                if (node.isNearby()) {
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .cancel(FORGOT_PHONE_NOTIFICATION_ID);
                }
            }
        }
    }

    /**
     * Creates a notification to inform user that the connectivity to phone has been lost (possibly
     * left the phone behind).
     */
    private void setupLostConnectivityNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.left_phone_title))
                .setContentText(getString(R.string.left_phone_content))
                .setVibrate(new long[]{0, 200})  // Vibrate for 200 milliseconds.
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLocalOnly(true)
                .setPriority(Notification.PRIORITY_MAX);
        Notification card = notificationBuilder.build();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(FORGOT_PHONE_NOTIFICATION_ID, card);
    }

    @Override
    public void onConnected(Bundle bundle) {
        setOrUpdateNotification();
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }
}