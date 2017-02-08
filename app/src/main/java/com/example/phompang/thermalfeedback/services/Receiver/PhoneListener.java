package com.example.phompang.thermalfeedback.services.Receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.phompang.thermalfeedback.model.Contact;
import com.example.phompang.thermalfeedback.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class PhoneListener extends PhoneStateListener {
    private static final String TAG = "PhoneListener";

    private ReceiverManager mReceiverManager;
    private List<Contact> contactList;

    public PhoneListener(ReceiverManager manager, String uid) {
        this.mReceiverManager = manager;
        contactList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    contactList.add(snapshot.getValue(Contact.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (mReceiverManager.isPause() || TextUtils.isEmpty(incomingNumber)) {
            return;
        }
        boolean memberState = inContactList(incomingNumber);

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

    private boolean inContactList(String incomingNumber) {
        for (Contact contact: contactList) {
            if (contact.getPhone().equals(incomingNumber)) {
                return true;
            }
        }
        return false;
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
