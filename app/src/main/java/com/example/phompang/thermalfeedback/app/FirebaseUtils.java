package com.example.phompang.thermalfeedback.app;

import com.example.phompang.thermalfeedback.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by phompang on 1/17/2017 AD.
 */

public class FirebaseUtils {
    public static void addUser(User u) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(u.getUid()).setValue(u);
    }
}
