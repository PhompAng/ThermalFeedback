package com.example.phompang.thermalfeedback.services.Receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class PhoneListener extends PhoneStateListener {
    private static final String TAG = "PhoneListener";

    private ReceiverManager mReceiverManager;

    public PhoneListener(ReceiverManager manager) {
        this.mReceiverManager = manager;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        boolean memberState;

        Random random = new Random();
        memberState = random.nextBoolean();

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                mReceiverManager.setThermal_warning(memberState ? 1:2);
                String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeStart);
                mReceiverManager.setDelay_warning(0);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mReceiverManager.setThermal_warning(0);
                String timeOffHook = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeOffHook);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                mReceiverManager.setThermal_warning(0);
                String timeIdle = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeIdle);
                break;
            default:
                break;
        }
    }
}
