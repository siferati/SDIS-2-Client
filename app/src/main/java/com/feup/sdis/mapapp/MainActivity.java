package com.feup.sdis.mapapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * This class implements the main activity.
 * It is the entry point of the app
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        { super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button btnStart = (Button) findViewById(R.id.btn_start);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected()) {
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, getText(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button btn_how = (Button) findViewById(R.id.btn_how);
            btn_how.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected()) {
                        Intent intent = new Intent(MainActivity.this, HowToActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, getText(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e){

        }
    }




    /**
     * Checks whether network connectivity exists and it is possible to establish connections and pass data.
     *
     * @return true if network connectivity exists, false otherwise.
     */
    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null && activeNetwork.isConnected());
    }
}
