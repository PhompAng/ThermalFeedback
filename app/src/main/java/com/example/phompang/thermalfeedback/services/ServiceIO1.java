package com.example.phompang.thermalfeedback.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.devahoy.android.shared.Shared;
import com.example.phompang.thermalfeedback.MainActivity;
import com.example.phompang.thermalfeedback.R;
import com.example.phompang.thermalfeedback.app.NLService;
import com.example.phompang.thermalfeedback.services.Receiver.NotificationReceiver;
import com.example.phompang.thermalfeedback.services.Receiver.PhoneListener;
import com.example.phompang.thermalfeedback.services.Receiver.ReceiverManager;
import com.example.phompang.thermalfeedback.services.Receiver.SMSReceiver;

import org.greenrobot.eventbus.EventBus;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

import static com.example.phompang.thermalfeedback.Constant.DURATION;
import static com.example.phompang.thermalfeedback.Constant.NEUTRAL;
import static com.example.phompang.thermalfeedback.Constant.REGULAR;
import static com.example.phompang.thermalfeedback.Constant.SHARED_NAME;
import static com.example.phompang.thermalfeedback.Constant.VERY;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class ServiceIO1 extends IOIOService {
    public static final String ACTION_START = "action-start";
    public static final String ACTION_STOP = "action-stop";

    private NotificationReceiver mNotificationReceiver;
    private SMSReceiver mSmsReceiver;
    private PhoneListener mPhoneListener;
    private ReceiverManager manager;

    private PowerManager.WakeLock mWakeLock;
    Vibrator v;

    private String uid;
    private int day;
    private Shared shared;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        shared = new Shared(getApplicationContext(), SHARED_NAME);

        Log.d("serviceIO1", "start");
        Log.d("process", Process.myPid() + "");
        Log.d("thread", Process.myTid() + "");

        manager = ReceiverManager.getInstance();

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_START)) {
                showNoti();
            } else if (intent.getAction().equals(ACTION_STOP)) {
                stopForeground(true);
                stopSelf();
            }
        }

        if (intent != null && intent.hasExtra("uid")) {
            uid = intent.getStringExtra("uid");
            Log.d("serviceIO1", uid);
            day = intent.getIntExtra("day", 1);
            manager.setUid(uid);
            manager.setDay(day);

            mNotificationReceiver = new NotificationReceiver(manager);
            mSmsReceiver = new SMSReceiver(manager);
            mPhoneListener = new PhoneListener(manager, uid);

            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            this.registerReceiver(mSmsReceiver, smsFilter);

            IntentFilter notificationFilter = new IntentFilter();
            notificationFilter.addAction(NLService.NOTIFICATION_INTENT);
            this.registerReceiver(mNotificationReceiver, notificationFilter);
        }

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);

        if (this.mWakeLock == null) {
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        }

        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.acquire();
        }

        return START_STICKY;
    }

    private void showNoti() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Thermal Feedback")
                .setContentText("Running Experiment...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Override
    public void onDestroy() {
        Log.d("serviceIO1", "destroy");
        if (this.mWakeLock != null && mWakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
        if (uid != null) {
            this.unregisterReceiver(mSmsReceiver);
            this.unregisterReceiver(mNotificationReceiver);
        }
        super.onDestroy();
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

        public void disconnected() {
            EventBus.getDefault().postSticky("DISCONNECTED");
        }

        public void loop() throws ConnectionLostException {
            try {
                Log.d("state", ioio_.getState().name());
                EventBus.getDefault().postSticky(ioio_.getState().name());
                Log.d("pause", manager.isPause() + "");
                if (manager.isPause()) {
                    manager.setThermal_warning(0);
                    dOutA.write(false);
                    dOutB.write(false);
                    return;
                }
                int neutral_temp = shared.getInt(NEUTRAL, 32);
                int intensity_very = shared.getInt(VERY, 0);
                int intensity_regular = shared.getInt(REGULAR, 0);
                Integer muti_cool = 1000, muti_verycool = 1500, multi_hot = 100, multi_veryhot = 120;
                int feedbackPeriod = shared.getInt(DURATION, 15);

                Log.d("thermal", manager.getThermal_warning() + "");
//                if (day == 1 || day == 3) {
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
//                }

//                if (day >= 2 && manager.getDelay_warning() == 0 && manager.getThermal_warning() != 0) {
//                    v.vibrate(300);
//                    Log.d("vibrate", "vibrate");
//                }

                Log.d("Warning", "Delay = " + manager.getDelay_warning());
                Log.d("Hold", "hold = " + manager.isHold());
                if (manager.isHold() || manager.getDelay_warning() <= feedbackPeriod) {
                    manager.setDelay_warning(manager.getDelay_warning()+1);
                } else {
                    manager.setThermal_warning(0);
                    manager.setDelay_warning(0);
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
