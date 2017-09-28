package com.example.phompang.thermalfeedback;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by phompang on 3/17/2017 AD.
 */

public class ThermalFeedbackApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
	    OkHttpClient client = new OkHttpClient.Builder()
			    .addNetworkInterceptor(new StethoInterceptor())
			    .build();
    }
}
