package com.example.phompang.thermalfeedback;

import static com.example.phompang.thermalfeedback.Constant.DURATION;
import static com.example.phompang.thermalfeedback.Constant.NEUTRAL;
import static com.example.phompang.thermalfeedback.Constant.REGULAR;
import static com.example.phompang.thermalfeedback.Constant.SHARED_NAME;
import static com.example.phompang.thermalfeedback.Constant.VERY;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.devahoy.android.shared.Shared;
import com.example.phompang.thermalfeedback.services.ServiceIO1;
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.view.Seeker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, Seeker.OnProgressChangeListener {

	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.duration)
	TextView duration;
	@BindView(R.id.testing)
	TextView testing;
	@BindView(R.id.neutral)
	Seeker neutral;
	@BindView(R.id.high)
	Seeker high;
	@BindView(R.id.normal)
	Seeker normal;
	@BindView(R.id.stimuli_duration)
	Seeker stimuli_duration;

	private CountDownTimer timer;
	private ReceiverManager mReceiverManager;
	private Shared shared;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Calibrate Device");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		shared = new Shared(getApplicationContext(), SHARED_NAME);
		mReceiverManager = ReceiverManager.getInstance();

		neutral.setOnProgressChangeListener(this);
		high.setOnProgressChangeListener(this);
		normal.setOnProgressChangeListener(this);
		stimuli_duration.setOnProgressChangeListener(this);

		neutral.setProgressValue(shared.getInt(NEUTRAL, 30));
		high.setProgressValue(shared.getInt(VERY, 0));
		normal.setProgressValue(shared.getInt(REGULAR, 0));
		stimuli_duration.setProgressValue(shared.getInt(DURATION, 10));

		duration.setText(getBaseContext().getString(R.string.duration, shared.getInt(DURATION, 15)));

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
		Snackbar.make(findViewById(R.id.activity_setting), String.format(Locale.getDefault(), "Testing stimuli: %s", testText), Snackbar.LENGTH_LONG).show();
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

	@Override
	public void onProgressChanged(Seeker seeker, int i, boolean b) {
		switch (seeker.getId()) {
			case R.id.neutral:
				neutral.setText(getBaseContext().getString(R.string.neutral, i));
				shared.save(NEUTRAL, i);
				break;
			case R.id.high:
				high.setText(getBaseContext().getString(R.string.high, i));
				shared.save(VERY, i);
				break;
			case R.id.normal:
				normal.setText(getBaseContext().getString(R.string.normal, i));
				shared.save(REGULAR, i);
				break;
			case R.id.stimuli_duration:
				stimuli_duration.setText(getBaseContext().getString(R.string.stimuli_duration, i));
				shared.save(DURATION, i);
				duration.setText(getBaseContext().getString(R.string.duration, shared.getInt(DURATION, 15)));
				break;
		}
	}
}
