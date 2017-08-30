package com.example.phompang.thermalfeedback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devahoy.android.shared.Shared;
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.ServiceIO1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.phompang.thermalfeedback.Constant.DURATION;
import static com.example.phompang.thermalfeedback.Constant.NEUTRAL;
import static com.example.phompang.thermalfeedback.Constant.REGULAR;
import static com.example.phompang.thermalfeedback.Constant.SHARED_NAME;
import static com.example.phompang.thermalfeedback.Constant.VERY;

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
    @BindView(R.id.neutral)
    TextView neutral;
    @BindView(R.id.high)
    TextView high;
    @BindView(R.id.normal)
    TextView normal;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.test)
    Button testBtn;
    @BindView(R.id.very_hot_test)
    Button veryHotTest;
    @BindView(R.id.hot_test)
    Button hotTest;
    @BindView(R.id.very_cold_test)
    Button veryColdTest;
    @BindView(R.id.cold_test)
    Button coldTest;

    private CountDownTimer timer;
    private BluetoothAdapter mBluetoothAdapter;
    private ReceiverManager mReceiverManager;
    private Shared shared;
    private List<String> data;
    private int currentTest;
    private int delay = 0;
    private int[] prime = new int[] {5, 7, 11, 13};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connect and Test Device");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shared = new Shared(getApplicationContext(), SHARED_NAME);
        mReceiverManager = ReceiverManager.getInstance();

        duration.setText(getBaseContext().getString(R.string.duration, shared.getInt(DURATION, 15)));
        neutral.setText(getBaseContext().getString(R.string.neutral, shared.getInt(NEUTRAL, 32)));
        high.setText(getBaseContext().getString(R.string.high, shared.getInt(VERY, 0)));
        normal.setText(getBaseContext().getString(R.string.normal, shared.getInt(REGULAR, 0)));

        findViewById(R.id.very_hot).setOnClickListener(this);
        findViewById(R.id.hot).setOnClickListener(this);
        findViewById(R.id.very_cold).setOnClickListener(this);
        findViewById(R.id.cold).setOnClickListener(this);

        veryHotTest.setOnClickListener(new TestButtonClickListener());
        hotTest.setOnClickListener(new TestButtonClickListener());
        veryColdTest.setOnClickListener(new TestButtonClickListener());
        coldTest.setOnClickListener(new TestButtonClickListener());


        setUpSpinner();
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String testNumber = data.get(spinner.getSelectedItemPosition());
//				List<String> seq = Arrays.asList(FirebaseRemoteConfig.getInstance().getString(testNumber, "33333").split("\\s+"));
                List<String> seq = new ArrayList<>();
                delay = 0;
                for (int i = 0; i < 5; i++) {
                    seq.add(String.valueOf(ThreadLocalRandom.current().nextInt(1, 4 + 1)));
                }
                Handler handler = new Handler();
                for (int a = 0; a < seq.size(); a++) {
                    final int ii = Integer.parseInt(seq.get(a));
                    delay += 1000 * prime[ThreadLocalRandom.current().nextInt(0, 3)];
                    Log.d("delay", delay + "");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Integer i = ii;
                            currentTest = ii;
                            Snackbar.make(findViewById(R.id.activity_connection), String.format(Locale.getDefault(), "Testing: %s", i), Snackbar.LENGTH_LONG).show();
                            mReceiverManager.setThermal_warning(i);
                            mReceiverManager.setDelay_warning(0);
                        }
                    }, delay);
                }
            }
        });


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

//        showDevices();
    }

    private void setUpSpinner() {
        data = Arrays.asList(
                "test1",
                "test2",
                "test3",
                "test4",
                "test5");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
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
        int thermalWarning = Integer.parseInt(v.getTag().toString());
        //TODO cancel testing
        Snackbar.make(findViewById(R.id.activity_connection), String.format(Locale.getDefault(), "Testing stimuli: %s", testText), Snackbar.LENGTH_LONG).show();
        mReceiverManager.setThermal_warning(thermalWarning);
        mReceiverManager.setDelay_warning(0);
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(shared.getInt(DURATION, 15) * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                testing.setText(String.format(Locale.getDefault(), "Testing stimuli: %s", testText));
                duration.setText(getBaseContext().getString(R.string.duration, millisUntilFinished / 1000));
            }

            public void onFinish() {
                mReceiverManager.setThermal_warning(0);
                testing.setText("Testing stimuli: ");
                duration.setText(getBaseContext().getString(R.string.duration, shared.getInt(DURATION, 15)));
            }
        }.start();
    }

    private class TestButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int guess = Integer.parseInt(v.getTag().toString());
            if (currentTest == guess) {
                Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(String state) {
        status.setText(state);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        startService(new Intent(getApplicationContext(), ServiceIO1.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("com", "pause");
        stopService(new Intent(getApplicationContext(), ServiceIO1.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
        }
        Log.d("con", "stop");
    }
}
