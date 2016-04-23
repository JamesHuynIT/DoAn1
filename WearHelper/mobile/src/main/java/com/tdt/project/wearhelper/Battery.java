package com.tdt.project.wearhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class Battery extends Service {

    public float batteryLevel;

    public Battery(Context context){
        getBatteryLevel(context);
    }

    public float getBatteryLevel(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        assert batteryStatus != null;
        int currentLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (currentLevel >= 0 && scale > 0) {
            batteryLevel = (currentLevel * 100) / scale;

        }
        return batteryLevel;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
