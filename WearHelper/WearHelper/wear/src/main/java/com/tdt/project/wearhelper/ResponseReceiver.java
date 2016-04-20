package com.tdt.project.wearhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PhoneStatusService.GET_PHONE_STATUS)) {
            boolean value = intent.getBooleanExtra("phoneStatus", false);
            if(value) {
                Toast.makeText(context, "Status On", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(context, "Status Off", Toast.LENGTH_LONG).show();
            }
        }
        else if (intent.getAction().equals(PhoneStatusService.FAIL_GET_PHONE_STATUS)){
            Toast.makeText(context, "Status Off", Toast.LENGTH_LONG).show();
        }

        if(intent.getAction().equals(FindPhoneService.ACTION_TOGGLE_ALARM)) {
            boolean value = intent.getBooleanExtra("AlarmOn", false);
            if(value) {
                Toast.makeText(context, "Alarm On", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(context, "Alarm Off", Toast.LENGTH_LONG).show();
            }
        }
        else if (intent.getAction().equals(FindPhoneService.ACTION_CANCEL_ALARM)){
            Toast.makeText(context, "Alarm Off", Toast.LENGTH_LONG).show();
        }
    }
}