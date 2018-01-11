package com.example.phompang.thermalfeedback.app;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by phompang on 5/26/2017 AD.
 */

public class ServiceUtils {
    public static boolean isRunning(Context ctx, String name) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            String serv = service.service.getClassName();
            Log.d("Service", serv);
            if (name.equals(serv)) {
                return true;
            }
        }
        return false;
    }
}
