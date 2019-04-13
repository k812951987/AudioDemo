package com.example.china.audiodemo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;

import java.util.logging.Logger;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
