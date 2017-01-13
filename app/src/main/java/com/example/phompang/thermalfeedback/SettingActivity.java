package com.example.phompang.thermalfeedback;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.ServiceIO1;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.testing)
    TextView testing;

    private CountDownTimer timer;
    private ReceiverManager mReceiverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calibrate Device");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReceiverManager = ReceiverManager.getInstance();

        findViewById(R.id.very_hot).setOnClickListener(this);
        findViewById(R.id.hot).setOnClickListener(this);
        findViewById(R.id.very_cold).setOnClickListener(this);
        findViewById(R.id.cold).setOnClickListener(this);
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

    @OnClick(R.id.users)
    public void users() {
        startActivity(new Intent(SettingActivity.this, UsersActivity.class));
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
        timer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                testing.setText(String.format(Locale.getDefault(), "Testing stimuli: %s", testText));
                duration.setText(String.format(Locale.getDefault(), "Duration: %d sec", + millisUntilFinished / 1000));
            }

            public void onFinish() {
                mReceiverManager.setThermal_warning(0);
                testing.setText("Testing stimuli: ");
                duration.setText("Duration: 15 sec");
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(getApplicationContext(), ServiceIO1.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        stopService(new Intent(getApplicationContext(), ServiceIO1.class));
    }
}
