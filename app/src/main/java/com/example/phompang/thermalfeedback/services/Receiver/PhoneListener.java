package com.example.phompang.thermalfeedback.services.Receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.phompang.thermalfeedback.model.Notification;

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
        if (mReceiverManager.isPause() || TextUtils.isEmpty(incomingNumber)) {
            return;
        }
        boolean memberState;

        Random random = new Random();
        memberState = random.nextBoolean();

        Date date = new Date();

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                int thermal_warning = memberState ? 1:2;
                mReceiverManager.setThermal_warning(thermal_warning);
                String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(date);
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeStart);
                mReceiverManager.setDelay_warning(0);
                pushNotification(incomingNumber, memberState, thermal_warning, date.getTime());
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                mReceiverManager.setThermal_warning(0);
                String timeOffHook = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(date);
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeOffHook);
                responseNotification(date.getTime());
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //cannot detect miss call
                mReceiverManager.setThermal_warning(0);
                String timeIdle = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(date);
                Log.d(TAG, incomingNumber + ": " + state + "/" + memberState + " " + timeIdle);
                endCallNotification(date.getTime());
                break;
            default:
                break;
        }
    }

    private void pushNotification(String phone, boolean contact, int stimuli, long startTime) {
        Notification notification = new Notification();
        notification.setPhone(phone);
        notification.setIsContact(contact);
        notification.setIsReal(true);
        notification.setType("Incoming Call");
        notification.setStimuli(stimuli);
        notification.setIsThermal(true);
        notification.setIsVibrate(false);
        notification.setStartTime(startTime);
        mReceiverManager.pushNotification(notification);
    }

    private void responseNotification(long responseTime) {
        mReceiverManager.responseNotification("Incoming Call", responseTime);
    }

    private void endCallNotification(long endTime) {
        mReceiverManager.endCallNotification(endTime);
    }
}
