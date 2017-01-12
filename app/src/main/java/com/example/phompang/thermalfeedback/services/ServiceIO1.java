package com.example.phompang.thermalfeedback.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.phompang.thermalfeedback.app.NLService;
import com.example.phompang.thermalfeedback.services.Receiver.NotificationReceiver;
import com.example.phompang.thermalfeedback.services.Receiver.PhoneListener;
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.Receiver.SMSReceiver;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class ServiceIO1 extends IOIOService {

    private NotificationReceiver mNotificationReceiver;
    private SMSReceiver mSmsReceiver;
    private PhoneListener mPhoneListener;
    private ReceiverManager manager;
    Vibrator v;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d("serviceIO1", "start");

        manager = ReceiverManager.getInstance();

        mNotificationReceiver = new NotificationReceiver(manager);
        mSmsReceiver = new SMSReceiver(manager);
        mPhoneListener = new PhoneListener(manager);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(mSmsReceiver, smsFilter);

        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(NLService.NOTIFICATION_INTENT);
        this.registerReceiver(mNotificationReceiver, notificationFilter);

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("serviceIO1", "destroy");
        this.unregisterReceiver(mSmsReceiver);
        this.unregisterReceiver(mNotificationReceiver);
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new Looper();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Looper extends BaseIOIOLooper {
        private final int mOutHeatPin = 36;    //This pin can serve as PWM output
        private final int mOutCoolPin = 34;    //This pin can serve as PWM output

        private final int dStbyPin = 37;
        private final int dOutPinA = 38; // hot temp
        private final int dOutPinB = 39; // cold temp
        private final int pTempPin = 40;

        private final int mPWMFreq = 100;      //The frequency of the PWM signal

        private DigitalOutput led_;

        private PwmOutput mHeatPWM;
        private PwmOutput mCoolPWM;

        private DigitalOutput dOutA;
        private DigitalOutput dOutB;
        private DigitalOutput dStby;
        private PwmOutput pTemp;

        protected void setup() throws ConnectionLostException {
            led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);

            mHeatPWM = ioio_.openPwmOutput(mOutHeatPin, mPWMFreq);
            mCoolPWM = ioio_.openPwmOutput(mOutCoolPin, mPWMFreq);

            dOutA = ioio_.openDigitalOutput(dOutPinA, true);
            dOutB = ioio_.openDigitalOutput(dOutPinB, true);
            dStby = ioio_.openDigitalOutput(dStbyPin, true);
            pTemp = ioio_.openPwmOutput(pTempPin, mPWMFreq);
        }

        public void loop() throws ConnectionLostException {
            try {
                Log.d("pause", manager.isPause() + "");
                if (manager.isPause()) {
                    manager.setThermal_warning(0);
                    dOutA.write(false);
                    dOutB.write(false);
                    return;
                }
                int neutral_temp = 32;
                int intensity_very = 6;
                int intensity_regular = 3;
                Integer muti_cool = 1000, muti_verycool = 1500, multi_hot = 100, multi_veryhot = 120;
                int feedbackPerieod = 15;

                Log.d("thermal", manager.getThermal_warning() + "");
                switch (manager.getThermal_warning()) {
                    case 1: //very hot
                        Log.d("Warning", "Call from close friends - very hot thermal stumuli");
                        dStby.write(true);
                        dOutA.write(true);
                        dOutB.write(false);
                        //pTemp.setPulseWidth(multi_veryhot * (neutral_temp + intensity_very));
                        pTemp.setPulseWidth(2580);
                        break;
                    case 2: //hot
                        Log.d("Warning", "Call from s/o else - hot thermal stumuli");
                        dStby.write(true);
                        dOutA.write(true);
                        dOutB.write(false);
                        //pTemp.setPulseWidth(multi_hot * (neutral_temp + intensity_regular));
                        pTemp.setPulseWidth(2000);
                        break;
                    case 3: // cold
                        Log.d("Warning", "App notification from fb app - cold thermal stimuli");
                        dStby.write(true);
                        dOutA.write(false);
                        dOutB.write(true);
                        pTemp.setPulseWidth(muti_cool * (neutral_temp - intensity_regular));
                        break;
                    case 4: //very cold
                        Log.d("Warning", "Personal Messaging from SMS or FB Line apps - very cold thermal stumuli");
                        dStby.write(true);
                        dOutA.write(false);
                        dOutB.write(true);
                        pTemp.setPulseWidth(muti_verycool * (neutral_temp - intensity_very));
                        break;
                    default:
                        Log.d("Warning", "set peltier to neutral temperature/not send any electricity to peltier.");
                        dStby.write(false);
                        dOutA.write(false);
                        dOutB.write(false);
                        pTemp.setPulseWidth(0);
                        break;
                }

                Log.d("Warning", "Delay = " + manager.getDelay_warning());
                if (manager.getDelay_warning() != feedbackPerieod) {
                    manager.setDelay_warning(manager.getDelay_warning()+1);
                } else {
                    manager.setThermal_warning(0);
                    dOutA.write(false);
                    dOutB.write(false);
                }

                led_.write(false);
                Thread.sleep(500);
                led_.write(true);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
