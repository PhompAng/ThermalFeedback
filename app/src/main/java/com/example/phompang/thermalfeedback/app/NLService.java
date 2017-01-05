package com.example.phompang.thermalfeedback.app;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by phompang on 1/5/2017 AD.
 */

public class NLService extends NotificationListenerService {

    public static final String NOTIFICATION_INTENT = "com.example.phompang.thermalfeedback.app.NLService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("NotificationPosted","ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent(NOTIFICATION_INTENT);
        i.putExtra("notification_state",true);
        i.putExtra("notification_tickerText",sbn.getNotification().tickerText);
        i.putExtra("notification_event",sbn.getPackageName());
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationRemoved","ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent(NOTIFICATION_INTENT);
        i.putExtra("notification_state",false);
        i.putExtra("notification_tickerText",sbn.getNotification().tickerText);
        i.putExtra("notification_event",sbn.getPackageName());
        sendBroadcast(i);
    }
}
