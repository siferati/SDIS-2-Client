package com.feup.sdis.mapapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.feup.sdis.mapapp.client.ServerService;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Utilizador on 21-05-2017.
 */

public class SelectMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);

        Button btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button btnPlay = (Button) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMapActivity.this, MazePlayerActivity.class);
                startActivity(intent);
            }
        });

        for(int i = 0; i < 3; i++){

            Button btnMapName = new Button (this);
            btnMapName.setText("nome do mapa");
            btnMapName.setTextColor(Color.WHITE);
            btnMapName.setBackgroundColor(Color.parseColor("#75c4bc"));
            LinearLayout ll = (LinearLayout) findViewById(R.id.linlayout);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 0, 0);
            btnMapName.setLayoutParams(lp);
            ll.addView(btnMapName, lp);
        }
    }

}