package com.example.phompang.thermalfeedback.services.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    private ReceiverManager mReceiverManager;

    public NotificationReceiver(ReceiverManager manager) {
        mReceiverManager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String tempPacket = intent.getStringExtra("notification_event");
        String tickerText = intent.getStringExtra("notification_tickerText");
        Boolean state = intent.getBooleanExtra("notification_state", true);

        Integer thermal_warning = -1;

        Log.d("tempPacket", tempPacket);
        switch (tempPacket) {
            case "com.facebook.katana":
            case "com.facebook.lite":
                thermal_warning = 3;
                break;
            case "com.facebook.orca":
                thermal_warning = 4;
                break;
            case "jp.naver.line.android":
                thermal_warning = 4;
                break;
            case "com.whatsapp":
                thermal_warning = 4;
                break;
        }

        if (state && (tickerText != null)) {
            mReceiverManager.setThermal_warning(thermal_warning);
            String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
            Log.d(TAG, "START " + tempPacket + ": " + tickerText + " " + timeStart);
            mReceiverManager.setDelay_warning(0);
        } else if (!state && (tickerText != null)) {
            String timeStop = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
            Log.d(TAG, "START " + tempPacket + ": " + tickerText + " " + timeStop);
        }
    }
}
