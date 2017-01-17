package com.example.phompang.thermalfeedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.username)
    TextInputEditText username;
    @BindView(R.id.password)
    TextInputEditText password;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @OnClick(R.id.submit)
    public void validate() {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Should not empty");
            cancel = true;
            focusView = password;
        }
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("Should not empty");
            cancel = true;
            focusView = username;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            login();
        }
    }

    public void login() {
        databaseReference.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String adminUser = dataSnapshot.child("username").getValue(String.class);
                String adminPass = dataSnapshot.child("password").getValue(String.class);
                if (adminUser.equals(username.getText().toString()) && adminPass.equals(password.getText().toString())) {
                    startActivity(new Intent(AdminActivity.this, SettingActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong credential!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
