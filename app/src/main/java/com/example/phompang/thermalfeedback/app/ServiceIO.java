package com.example.phompang.thermalfeedback.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class ServiceIO extends IOIOService {

    DatabaseHelper mHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;
    Vibrator v;

    private final int mOutHeatPin = 36;    //This pin can serve as PWM output
    private final int mOutCoolPin = 34;    //This pin can serve as PWM output

    private final int dStbyPin = 37;
    private final int dOutPinA = 38; // hot temp
    private final int dOutPinB = 39; // cold temp
    private final int pTempPin = 40;

    private final int mPWMFreq = 100;      //The frequency of the PWM signal

    Integer neutral_temp, intensity_very, intensity_regular, feedbackPerieod;
    Integer thermal_warning = 0, delay_warning = 0;
    Boolean auto, vibrator = false;
    boolean onRingingState = false;


    Integer muti_cool = 1000, muti_verycool = 1500, multi_hot = 100, multi_veryhot = 120;

    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    String tempTime, tempTickerText;

    private class MyPhoneListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            //This variable is for checking if the incoming number is one of the close friends/recorded numbers. true is a friend, false is someone else
            boolean memberState = false;

            mCursor = mDb.rawQuery("SELECT * FROM "
                    + DatabaseHelper.TABLE_CON + " WHERE " + DatabaseHelper.COL_TELEPHON + "='" + incomingNumber + "'", null);
            if (mCursor.getCount() > 0) {
                memberState = true;
            }


            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // phone ringing...
                    if (!onRingingState) {

                        onRingingState = true;

                        //random to set vibration*********
                        Random r = new Random();
                        int r_int = r.nextInt(4); //random between 0 and 3
                        if (r_int == 1 || r_int == 3) {
                            vibrator = true;
                        } else {
                            vibrator = false;
                        }

                        //check if it is a close friend.
                        String thermalSti;
                        if (memberState) {
                            //Toast.makeText(getApplicationContext(),"เบอร์ที่บันทึกไว้", Toast.LENGTH_SHORT).show();
                            thermal_warning = 1; //very hot
                            thermalSti = "very_hot";
                        } else {
                            //Toast.makeText(getApplicationContext(),"เบอร์ที่ไม่ได้บันทึกไว้", Toast.LENGTH_SHORT).show();
                            thermal_warning = 2; //hot
                            thermalSti = "hot";
                        }

                        delay_warning = 0; //previosuly set at -60

                        //write log to db
                        String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());


                        String sqlString = "INSERT INTO " + DatabaseHelper.TABLE_LOG_EXP1 + " (" + DatabaseHelper.COL_ACTIVITY + ", "+ DatabaseHelper.COL_CAT + ", " + DatabaseHelper.COL_IS_FRIEND +
                                ", " + DatabaseHelper.COL_THERMAL_STI + ", " + DatabaseHelper.COL_VIBRO_STI + ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO + ", " + DatabaseHelper.COL_IDLE_CO +
                                " ) VALUES ('incoming_call' ,'" + incomingNumber + "' ,'" + Boolean.toString(memberState) + "' ,'" + thermalSti + "' ,'" + Boolean.toString(vibrator) +
                                "' ,'" + timeStart + "' ,'NULL' ,'NULL');";
                        Log.d("SQL", sqlString);


                        mDb.execSQL(sqlString);
                        tempTime = timeStart;
                    }

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // one call exists that is dialing, active, or on hold
                    //Toast.makeText(getApplicationContext(),"กดปุ่มรับสาย", Toast.LENGTH_SHORT).show();
                    thermal_warning = 0;
                    String timeStop1 = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                    mDb.execSQL("UPDATE " + DatabaseHelper.TABLE_LOG_EXP1 + " SET "
                            + DatabaseHelper.COL_AC_CO + "='" + timeStop1
                            + "' WHERE " + DatabaseHelper.COL_ST_CO + "='" + tempTime + "';");
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    // in initialization of the class and at the end of phone call
                    //Toast.makeText(getApplicationContext(), "จบการสนทนา", Toast.LENGTH_SHORT).show();
                    thermal_warning = 0;
                    //************************************************************************************
                    String timeStop2 = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                    mDb.execSQL("UPDATE " + DatabaseHelper.TABLE_LOG_EXP1 + " SET "
                            + DatabaseHelper.COL_IDLE_CO + "='" + timeStop2
                            + "' WHERE " + DatabaseHelper.COL_ST_CO + "='" + tempTime + "';");

                    onRingingState = false;

                    break;
                default:
                    break;
            }
        }
    }


    //need to create a UI page for responding the receipt of sms alarm/stumuli/feedback.
    private final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                        /***
                         *
                         * To check whether the sender of SMS is a close friend
                         *
                         * **/
//                        mCursor = mDb.rawQuery("SELECT * FROM "
//                                + DatabaseHelper.TABLE_CON, null);
//                        boolean memberState = false;
//
//                        mCursor.moveToFirst();
//                        while (!mCursor.isAfterLast()) {
//                            if (senderNum.equals("+66" + mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.COL_TELEPHON)).substring(1)))
//                                memberState = true;
//                            mCursor.moveToNext();
//                        }
//
//                        if (memberState) { //senderNum.equals("+66914594430")+66873916181
//                            Random r = new Random();
//                            int r_int = r.nextInt(4);
//                            if (r_int == 1 || r_int == 3) {
//                                vibrator = true;
//                            } else {
//                                vibrator = false;
//                            }
//                            //Toast.makeText(getApplicationContext(),"SMSที่บันทึกไว้", Toast.LENGTH_SHORT).show();
//                            warning = 1;
//                            delay_warning = 0;
//
//                            String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
//                            mDb.execSQL("INSERT INTO " + DatabaseHelper.TABLE_LOG + " (" + DatabaseHelper.COL_CAT +
//                                    ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO +
//                                    " ) VALUES ('" + senderNum + "' ,'" + timeStart + "' ,'NULL');");
//                        } else {
//                            Random r = new Random();
//                            int r_int = r.nextInt(4);
//                            if (r_int == 1 || r_int == 3) {
//                                vibrator = true;
//                            } else {
//                                vibrator = false;
//                            }
//                            //Toast.makeText(getApplicationContext(),"SMSที่ไม่ได้บันทึกไว้", Toast.LENGTH_SHORT).show();
//                            warning = 2;
//                            delay_warning = 0;
//
//                            String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
//                            mDb.execSQL("INSERT INTO " + DatabaseHelper.TABLE_LOG + " (" + DatabaseHelper.COL_CAT +
//                                    ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO +
//                                    " ) VALUES ('" + senderNum + "' ,'" + timeStart + "' ,'NULL');");
//                        }

                        /***
                         *
                         * Don't care if a sender of SMS is a close friend
                         *
                         * **/

                        //random to set vibration*********
                        Random r = new Random();
                        int r_int = r.nextInt(4);
                        if (r_int == 1 || r_int == 3) {
                            vibrator = true;
                        } else {
                            vibrator = false;
                        }
                        //Toast.makeText(getApplicationContext(),"SMSที่ไม่ได้บันทึกไว้", Toast.LENGTH_SHORT).show();
//                        warning = 2;
//                        delay_warning = 0; //
//
//
//                        String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
//                        mDb.execSQL("INSERT INTO " + DatabaseHelper.TABLE_LOG_EXP1 + " (" + DatabaseHelper.COL_CAT +
//                                ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO +
//                                " ) VALUES ('" + senderNum + "' ,'" + timeStart + "' ,'NULL');");;


                        String thermalSti = "very_cold";
                        thermal_warning = 4;
                        delay_warning = 0; //?


                        String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());


                        String sqlString = "INSERT INTO " + DatabaseHelper.TABLE_LOG_EXP1 + " (" + DatabaseHelper.COL_ACTIVITY + ", "+ DatabaseHelper.COL_CAT + ", " + DatabaseHelper.COL_IS_FRIEND +
                                ", " + DatabaseHelper.COL_THERMAL_STI + ", " + DatabaseHelper.COL_VIBRO_STI + ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO + ", " + DatabaseHelper.COL_IDLE_CO +
                                " ) VALUES ('sms' ,'" + senderNum + "' ,'NULL' ,'" + thermalSti + "' ,'" + Boolean.toString(vibrator) +
                                "' ,'" + timeStart + "' ,'NULL' ,'NULL');";
                        Log.d("SQL", sqlString);


                        mDb.execSQL(sqlString);

                    } // end for loop
                } // bundle is null
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
            }
        }
    };

    //need to create a UI page for responding the receipt of sms alarm/stumuli/feedback.
    private final BroadcastReceiver NotificationReceiver = new BroadcastReceiver() {
        //class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String tempPacket = intent.getStringExtra("notification_event");
            String tickerText = intent.getStringExtra("notification_tickerText");
            Boolean state = intent.getBooleanExtra("notification_state", true);

            String tempApp = null;

            if (tempPacket.equals("com.facebook.orca") || tempPacket.equals("com.facebook.lite")) {
                Log.d("tempApp", tempPacket);
                tempApp = "facebook messenger";

            } else if (tempPacket.equals("jp.naver.line.android")) {
                tempApp = "line";

            } else if (tempPacket.equals("com.whatsapp")) {
                tempApp = "whatapp";
            }

            if (state && (tickerText != null)) {
                Random r = new Random();
                int r_int = r.nextInt(4);
                if (r_int == 1 || r_int == 3) {
                    vibrator = true;
                } else {
                    vibrator = false;
                }
                //Toast.makeText(getApplicationContext(),"", Toast.LENGTH_SHORT).show();
                thermal_warning = 3;
                delay_warning = 0;

                String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                if (tempApp != null) {
                    mDb.execSQL("INSERT INTO " + DatabaseHelper.TABLE_LOG_EXP1 + " (" + DatabaseHelper.COL_CAT +
                            ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO +
                            " ) VALUES ('" + tempApp + "' ,'" + timeStart + "' ,'NULL');");
                }
                tempTickerText = tempApp;
                tempTime = timeStart;
            } else if (!state && (tickerText != null)) {

                String timeStop = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                mDb.execSQL("UPDATE " + DatabaseHelper.TABLE_LOG_EXP1 + " SET "
                        + DatabaseHelper.COL_AC_CO + "='" + timeStop
                        + "' WHERE " + DatabaseHelper.COL_CAT + "='" + tempTickerText + "'"
                        + " AND " + DatabaseHelper.COL_ST_CO + "='" + tempTime + "';");
                tempTickerText = "";
            }

            if (tempPacket.equals("com.facebook.catana"))
                tempApp = "facebook notification";

            if (state && (tickerText != null)) {
                Random r = new Random();
                int r_int = r.nextInt(4);
                if (r_int == 1 || r_int == 3) {
                    vibrator = true;
                } else {
                    vibrator = false;
                }
                //Toast.makeText(getApplicationContext(),"facebook", Toast.LENGTH_SHORT).show();
                thermal_warning = 4;
                delay_warning = 0;

                String timeStart = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                if (tempApp != null) {
                    mDb.execSQL("INSERT INTO " + DatabaseHelper.TABLE_LOG_EXP1 + " (" + DatabaseHelper.COL_CAT +
                            ", " + DatabaseHelper.COL_ST_CO + ", " + DatabaseHelper.COL_AC_CO +
                            " ) VALUES ('" + tempApp + "' ,'" + timeStart + "' ,'NULL');");
                }
                tempTickerText = tempApp;
                tempTime = timeStart;
            } else if (!state && (tickerText != null)) {

                String timeStop = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(new Date());
                mDb.execSQL("UPDATE " + DatabaseHelper.TABLE_LOG_EXP1 + " SET "
                        + DatabaseHelper.COL_AC_CO + "='" + timeStop
                        + "' WHERE " + DatabaseHelper.COL_CAT + "='" + tempTickerText + "'"
                        + " AND " + DatabaseHelper.COL_ST_CO + "='" + tempTime + "';");
                tempTickerText = "";
            }

        }
    };

    class Looper extends BaseIOIOLooper {
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

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ServiceIO.this);
                neutral_temp = prefs.getInt("neutral_temp", 32);
                intensity_very = prefs.getInt("intensity_very", 6);
                intensity_regular = prefs.getInt("intensity_regular", 3);
                auto = prefs.getBoolean("auto", false);
                feedbackPerieod = prefs.getInt("feedback_perieod_noncall", 30);

                //Log.d("Warning", "Time Set = "+Integer.toString(time_set));
                //Log.d("Warning", "Auto Set = "+auto.toString());

                if (vibrator && auto) {
                    Log.d("Warning", "vibrate");
                    v.vibrate(1000);
                }

                switch (thermal_warning) {
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
                        /****************************************/
//                        Log.d("Warning", "set peltier to neutral temperature/not send any electricity to peltier.");
//                        dStby.write(false);
//                        dOutA.write(false);
//                        dOutB.write(false);
//                        pTemp.setPulseWidth(0);
                        break;
                }

//                switch (warning) {
//                    case 1: //very hot
//                        if (vibrator && auto) {
//                            Log.d("Warning", "Call vibrate");
//                            v.vibrate(1000);
//                        } else {
//                            Log.d("Warning", "Call very hot Temp");
//                            dStby.write(true);
//                            dOutA.write(true);
//                            dOutB.write(false);
//                            pTemp.setPulseWidth(multi_veryhot * (hot_set + 10));
//                        }
//                        break;
//                    case 2: //hot
//                        if (vibrator && auto) {
//                            Log.d("Warning", "Sms vibrate");
//                            v.vibrate(1000);
//                        } else {
//                            Log.d("Warning", "Sms hot Temp");
//                            dStby.write(true);
//                            dOutA.write(true);
//                            dOutB.write(false);
//                            pTemp.setPulseWidth(multi_hot * (hot_set + 5));
//                        }
//                        break;
//                    case 3: //very cool
//                        if (vibrator && auto) {
//                            Log.d("Warning", "App vibrate");
//                            v.vibrate(1000);
//                        } else {
//                            Log.d("Warning", "App cool Temp");
//                            dStby.write(true);
//                            dOutA.write(false);
//                            dOutB.write(true);
//                            pTemp.setPulseWidth(muti_cool * cool_set);
//                        }
//                        break;
//                    case 4: // cool
//                        if (vibrator && auto) {
//                            Log.d("Warning", "App vibrate");
//                            v.vibrate(1000);
//                        } else {
//                            Log.d("Warning", "App cool Temp");
//                            dStby.write(true);
//                            dOutA.write(false);
//                            dOutB.write(true);
//                            pTemp.setPulseWidth(muti_cool * (cool_set + 6));
//                        }
//                        break;
//                    default:
//                        break;
//                }

                if(!onRingingState) {
                    //Log.d("Warning", "Delay = "+Integer.toString(delay_warning));
                    if (delay_warning != feedbackPerieod) {
                        delay_warning++;
                    } else {
                        thermal_warning = 0;
                        dOutA.write(false);
                        dOutB.write(false);
                    }
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

    protected IOIOLooper createIOIOLooper() {

        return new Looper();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d("serviceIO", "start");

        // add PhoneStateListener for monitoring
        MyPhoneListener phoneListener = new MyPhoneListener();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // receive notifications of telephony state changes
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Get the object of SmsManager
        IntentFilter filter = new IntentFilter(ACTION);
        this.registerReceiver(mReceivedSMSReceiver, filter);

        // Get the object of Notification
        IntentFilter fil = new IntentFilter();
        fil.addAction(NLService.NOTIFICATION_INTENT);
        this.registerReceiver(NotificationReceiver, fil);

        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        mHelper = new DatabaseHelper(this);
        mDb = mHelper.getReadableDatabase();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}




