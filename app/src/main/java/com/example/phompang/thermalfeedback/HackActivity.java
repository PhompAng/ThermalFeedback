package com.example.phompang.thermalfeedback;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.ServiceIO1;
import com.example.phompang.thermalfeedback.view.Seeker;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HackActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.testing)
    TextView testing;
    @BindView(R.id.hack)
    Seeker seeker;

    private CountDownTimer timer;
    private ReceiverManager mReceiverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack);
        ButterKnife.bind(this);
        mReceiverManager = ReceiverManager.getInstance();

        findViewById(R.id.hot).setOnClickListener(this);
        findViewById(R.id.cold).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        int pulse = 0;
        int thermal = 0;
        switch ((String) v.getTag()) {
            case "Hot":
                pulse = 2500 + (seeker.getValue() * 10);
                thermal = 5;
                break;
            case "Cold":
                pulse = 25000 + (seeker.getValue() * 100);
                thermal = 6;
                break;
        }
        Snackbar.make(findViewById(R.id.main), "Testing pulse width " + pulse, Snackbar.LENGTH_SHORT).show();
        mReceiverManager.setThermal_warning(thermal);
        mReceiverManager.setPulseWidth(pulse);
        mReceiverManager.setDelay_warning(0);
        if (timer != null) {
            timer.cancel();
        }

        final int finalPulse = pulse;
        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                testing.setText("Testing pulse: " + finalPulse + ", thermal " + v.getTag());
                duration.setText(String.format(Locale.getDefault(), "Duration: %d sec", + millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mReceiverManager.setThermal_warning(0);
                testing.setText("Testing pulse: ");
                duration.setText("Duration: 15 sec");
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), ServiceIO1.class);
        intent.setAction(ServiceIO1.ACTION_START);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        Intent intent = new Intent(getApplicationContext(), ServiceIO1.class);
        intent.setAction(ServiceIO1.ACTION_STOP);
        startService(intent);
    }
}
