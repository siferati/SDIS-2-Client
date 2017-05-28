package com.feup.sdis.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.feup.sdis.mapapp.client.ActivityToken;
import com.feup.sdis.mapapp.client.ServerService;

/**
 * Created by Utilizador on 21-05-2017.
 */

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnSelect = (Button) findViewById(R.id.btn_select_map);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getIntent().getExtras().getString("accesstoken") == null
                            || getIntent().getExtras().getString("username") == null) return;
                    Intent intent = ActivityToken.passUserToken(
                            StartActivity.this, SelectMapActivity.class,
                            getIntent().getExtras().getString("accesstoken"), getIntent().getExtras().getString("username"));
                    startActivity(intent);
                }catch(Exception e ){
                    Toast.makeText(StartActivity.this, getText(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnCreate = (Button) findViewById(R.id.btn_create_map);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getIntent().getExtras().getString("accesstoken") == null
                            || getIntent().getExtras().getString("username") == null) return;
                    Intent intent = ActivityToken.passUserToken(
                            StartActivity.this, MazeBuilderActivity.class,
                            getIntent().getExtras().getString("accesstoken"), getIntent().getExtras().getString("username"));
                    startActivity(intent);
                }catch(Exception e) {
                    Toast.makeText(StartActivity.this, getText(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

}