package com.tdt.project.wearhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

public class GoogleClient implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // INTEGER
    private static final int CONNECT_FAIL_NOTIFICATION_ID = 1;
    private static Notification.Builder notification;
    private Context mContext;
    private ResponseReceiver receiver = new ResponseReceiver();

    public GoogleClient(Context context, Intent intent) {
        mContext = context;
        getGoogleClient(context);
    }

    public GoogleApiClient getGoogleClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mContext.unregisterReceiver(receiver);
        SpannableString title = new SpannableString("Smart Helper");
        title.setSpan(new RelativeSizeSpan(0.85f), 0, title.length(), Spannable.SPAN_POINT_MARK);
        notification = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText("Lost connect to your phone")
                .setSmallIcon(R.drawable.ic_launcher)
                .setVibrate(new long[]{0, 50})
                .extend(new Notification.WearableExtender()
                        .setContentAction(0)
                        .setHintHideIcon(true))
                .setLocalOnly(true)
                .setPriority(Notification.PRIORITY_MAX);
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(CONNECT_FAIL_NOTIFICATION_ID, notification.build());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mContext.unregisterReceiver(receiver);
        SpannableString title = new SpannableString("Smart Helper");
        title.setSpan(new RelativeSizeSpan(0.85f), 0, title.length(), Spannable.SPAN_POINT_MARK);
        notification = new Notification.WearableExtender()
                .setContentAction(0)
                .setHintHideIcon(true).extend(new Notification.Builder(mContext)
                        .setContentTitle(title)
                        .setContentText("Can't connect to your phone")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setVibrate(new long[]{0, 50}))
                .setLocalOnly(true)
                .setPriority(Notification.PRIORITY_MAX);
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(CONNECT_FAIL_NOTIFICATION_ID, notification.build());
    }
}