package com.feup.sdis.mapapp.client;

import android.app.Application;
import android.content.Context;

/**
 * Created by Rui on 06/06/2017.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}