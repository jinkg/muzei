package com.yalin.muzei;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * YaLin
 * 2016/11/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
