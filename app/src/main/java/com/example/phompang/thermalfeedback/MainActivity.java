package com.example.phompang.thermalfeedback;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.devahoy.android.shared.Shared;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Shared shared;

    public static final String TAG = MainActivity.class.getSimpleName();

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
	private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        requestPermissions();

        shared = new Shared(this, "thermal");

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
	    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
			    .setDeveloperModeEnabled(BuildConfig.DEBUG)
			    .build();
	    mFirebaseRemoteConfig.setConfigSettings(configSettings);
	    Log.d(TAG, "onCreate: " + configSettings.isDeveloperModeEnabled());
	    mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
	    mFirebaseRemoteConfig.fetch(10).addOnCompleteListener(this, new OnCompleteListener<Void>() {
		    @Override
		    public void onComplete(@NonNull Task<Void> task) {
			    if (task.isSuccessful()) {
				    Log.d("FETCH", "Succeeded");
				    mFirebaseRemoteConfig.activateFetched();
				    Log.d("aaaaaa", FirebaseRemoteConfig.getInstance().getString("bbb"));
				    Log.d("aaaaaa", FirebaseRemoteConfig.getInstance().getString("test1"));
			    }
		    }
	    });

        FirebaseMessaging.getInstance().subscribeToTopic("fake_noti");

        MainFragment mainFragment = MainFragment.newInstance("test", "test");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, mainFragment).commit();

    }

    private void requestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (!gotPermission(Manifest.permission.READ_CONTACTS)) {
            permissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!gotPermission(Manifest.permission.READ_PHONE_STATE)) {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!gotPermission(Manifest.permission.RECEIVE_SMS)) {
            permissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }
        if (!gotPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionsNeeded.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    private boolean gotPermission(String permission) {
        return ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean using = shared.getBoolean("using", false);
        Log.d(TAG, using + "");
        if (using) {
            String uid = shared.getString("user_id", "U01");
            int day = shared.getInt("day", 1);
            Log.d(TAG, uid + " uid" + day);
            onFragmentInteraction(uid, day);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                for (Map.Entry<String, Integer> perm: perms.entrySet()) {
                    if (perm.getValue() != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, perm.getKey() + " is not granted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onFragmentInteraction(String uid, int day) {
        Intent intent = new Intent(MainActivity.this, ExperimentActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("day", day);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                startActivity(new Intent(MainActivity.this, ConnectionActivity.class));
                return true;
            case R.id.action_admin:
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
