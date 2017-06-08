package com.feup.sdis.mapapp.client;

import android.app.Application;
import android.content.Context;

/**
 * Created by Rui on 06/06/2017.
 */

public class MyApp extends Application {
    private static MyApp instance;
    private static String username;
    private static String accessToken;
    private static String mapName;

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    public static String getUsername(){
        return username;
    }

    public static String getAccessToken(){
        return accessToken;
    }

    public static String getMapName(){
        return mapName;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static void setUsername(String name){
        username = name;
    }

    public static void setAccessToken(String accesstoken){
        accessToken = accesstoken;
    }

    public static void setMapName(String mapname){
        mapName=mapname;
    }
}