package com.example.phompang.thermalfeedback.services;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by phompang on 2/22/2017 AD.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("UpdateToken", token);
        FirebaseDatabase.getInstance().getReference().child("token").setValue(token);
    }
}
