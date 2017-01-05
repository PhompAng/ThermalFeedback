package com.example.phompang.thermalfeedback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ENABLE_BT = 4545;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.testing)
    TextView testing;

    private CountDownTimer timer;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connect and Test Device");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.very_hot).setOnClickListener(this);
        findViewById(R.id.hot).setOnClickListener(this);
        findViewById(R.id.very_cold).setOnClickListener(this);
        findViewById(R.id.cold).setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("Bluetooth", "Do ot support bluetooth");
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        showDevices();
    }

    private void showDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            setBluetoothText(pairedDevices);
        }
    }

    private void setBluetoothText(Set<BluetoothDevice> pairedDevices) {
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            Log.d("devices", deviceName + "  " + deviceHardwareAddress);
            if (deviceHardwareAddress.equals("00:15:83:0C:BF:EB")) {
                status.setText(R.string.connected);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    showDevices();
                } else {
                    finish();
                }
                break;
        }
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

    @Override
    public void onClick(View v) {
        final String testText = ((TextView) v).getText().toString();
        Snackbar.make(findViewById(R.id.activity_connection), String.format(Locale.getDefault(), "Testing stimuli: %s", testText), Snackbar.LENGTH_LONG).show();
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                testing.setText(String.format(Locale.getDefault(), "Testing stimuli: %s", testText));
                duration.setText(String.format(Locale.getDefault(), "Duration: %d sec", + millisUntilFinished / 1000));
            }

            public void onFinish() {
                testing.setText("Testing stimuli: ");
                duration.setText("Duration: 15 sec");
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
}
