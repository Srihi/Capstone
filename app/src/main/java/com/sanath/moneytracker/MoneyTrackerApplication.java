package com.sanath.moneytracker;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by sanathnandasiri on 1/25/17.
 */

public class MoneyTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
