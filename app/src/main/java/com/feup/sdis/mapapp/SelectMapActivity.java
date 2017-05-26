package com.feup.sdis.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        // TODO: delete this
        /*
        try  {
            String response = new ServerService().execute("maps?name=mapa1", "GET", null).get();
            Log.i("tag", response);
        } catch(Exception e) {e.printStackTrace();}

        JSONObject mapJSON = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        JSONObject singleLine = new JSONObject();
        JSONArray lineArray = new JSONArray();

        try {

            jsonAll.put("username", "user1");
            jsonAll.put("userhash", "ABC");
            mapJSON.put("name", "o_mapa");

            mapJSON.put("startlat", Double.valueOf(48.509).toString());
            mapJSON.put("startlng", Double.valueOf(11.098).toString());

            mapJSON.put("finishlat", Double.valueOf(51.509).toString());
            mapJSON.put("finishlng", Double.valueOf(12.098).toString());

            jsonAll.put("map", mapJSON);

            jsonAll.put("lines",lineArray);

            Log.i("w", jsonAll.toString());
            String mapId = new ServerService().execute("maps", "PUT", jsonAll.toString()).get();
            Log.i("tag", "MapId: " + mapId);

        } catch(org.json.JSONException e){
            e.printStackTrace();
        } catch (Exception e){}
        */
    }
}