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

    public static void deleteUser(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).removeValue();
    }

    public static void addNotification(String uid, final Notification notification) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("users").child(uid).child("notificationsList").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().push().setValue(notification, new DatabaseReference.CompletionListener() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void responseNotification(String uid, String type, final long responseTime) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users").child(uid).child("notificationsList").limitToLast(1).getRef().orderByChild("type").equalTo(type).limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().child("responseTime").setValue(responseTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
