package com.example.phompang.thermalfeedback;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class TestActivity extends IOIOActivity {

    TextView status;
    ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        status = (TextView) findViewById(R.id.status);
        toggle = (ToggleButton) findViewById(R.id.toggle);
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }

    class Looper extends BaseIOIOLooper {
        DigitalOutput dio;
        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("connected");
                }
            });
            dio = ioio_.openDigitalOutput(0);
        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {
            boolean isChecked = toggle.isChecked();
            dio.write(!isChecked);
        }

        @Override
        public void disconnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("disconnected");
                }
            });
        }

        @Override
        public void incompatible() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("incompat");
                }
            });
        }
    }
}
