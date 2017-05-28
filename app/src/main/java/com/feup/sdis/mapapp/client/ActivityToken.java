package com.feup.sdis.mapapp.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by Rui on 27/05/2017.
 */

public class ActivityToken {

    public static Intent passUserToken(Context packageContent, Class<?> cls, String accessToken, String username){

        if (accessToken == null) {
           return new Intent(packageContent, cls);
        }

        Intent intent = new Intent(packageContent, cls);

        Bundle b = new Bundle();
        b.putString("username", username);
        b.putString("accesstoken", accessToken);
        intent.putExtras(b);

        return intent;
    }

    public static Intent passUserTokenMap(Context packageContent, Class<?> cls, String accessToken, String username, String mapName){

        if (accessToken == null) {
            return new Intent(packageContent, cls);
        }

        Intent intent = new Intent(packageContent, cls);

        Bundle b = new Bundle();
        b.putString("username", username);
        b.putString("accesstoken", accessToken);
        b.putString("mapname", mapName);
        intent.putExtras(b);

        return intent;
    }

}
