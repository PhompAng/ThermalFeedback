package com.example.phompang.thermalfeedback.app;

import com.example.phompang.thermalfeedback.model.Notification;
import com.example.phompang.thermalfeedback.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by phompang on 1/17/2017 AD.
 */

public class FirebaseUtils {
    public static void addUser(User u) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(u.getUid()).setValue(u);
    }

    public static void updateUser(String uid, User user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).setValue(user);
    }

    public static void deleteUser(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).removeValue();
    }

    public static void addNotification(String uid, int day, final Notification notification) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("notifications").child(uid).child(String.valueOf(day)).push().setValue(notification, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, final DatabaseReference databaseReference) {
                databaseReference.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        databaseReference.child("attempt").setValue(dataSnapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public static void responseNotification(String uid, int day, final String type, final long responseTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("notifications").child(uid).child(String.valueOf(day));
        query.orderByChild("type").equalTo(type).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot latestNoti: dataSnapshot.getChildren()) {
                    latestNoti.getRef().child("responseTime").setValue(responseTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void endCallNotification(String uid, int day, String s, final long endTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("notifications").child(uid).child(String.valueOf(day));
        query.orderByChild("type").equalTo(s).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot latestNoti: dataSnapshot.getChildren()) {
                    latestNoti.getRef().child("endTime").setValue(endTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
