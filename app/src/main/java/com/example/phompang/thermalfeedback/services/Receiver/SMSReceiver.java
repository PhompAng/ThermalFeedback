package com.example.phompang.thermalfeedback.services.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";

    private ReceiverManager mReceiverManager;

    public SMSReceiver(ReceiverManager manager) {
        mReceiverManager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        SmsMessage smsMessage = smsMessages[0];
        String senderNum = smsMessage.getDisplayOriginatingAddress();
        String message = smsMessage.getDisplayMessageBody();

        mReceiverManager.setThermal_warning(4);

        String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.d(TAG, senderNum + ": " + message + " " + timeStart);
        mReceiverManager.setDelay_warning(0);
    }
}
