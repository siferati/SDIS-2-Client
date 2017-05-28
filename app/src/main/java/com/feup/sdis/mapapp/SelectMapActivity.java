package com.feup.sdis.mapapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.feup.sdis.mapapp.client.ActivityToken;
import com.feup.sdis.mapapp.client.ServerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Utilizador on 21-05-2017.
 */

public class SelectMapActivity extends AppCompatActivity {

    private InputStream certStream;
    private InputStream trustStream;

    private String[] maps;

    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);

        final String username = getIntent().getExtras().getString("username");
        final String accesstoken = getIntent().getExtras().getString("accesstoken");

        Button btnDone = (Button) findViewById(R.id.btn_create_game);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SelectMapActivity.this);
                builder.setTitle("Insert the name of the map where the new game will be played");

                final EditText nameInput = new EditText(SelectMapActivity.this);
                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(nameInput);

                builder.setPositiveButton("Sumbit", new DialogInterface.OnClickListener() {
                    @Override
                        public void onClick(DialogInterface dialog, int i) {

                            String mapname = nameInput.getText().toString();

                            try{

                                refreshStreams();

                                new ServerService(certStream, trustStream){
                                    @Override
                                    public void onResponseReceived(String s){
                                        response = s;
                                    }
                                }.execute("maps?name=" + mapname, "GET").get();
                                if (response.startsWith("404")){
                                    Toast toast = Toast.makeText(SelectMapActivity.this, getText(R.string.map_404), Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }

                                JSONObject gameJSON = new JSONObject();
                                gameJSON.put("username", username);
                                gameJSON.put("accesstoken", accesstoken);
                                gameJSON.put("mapname", mapname);

                                refreshStreams();

                                response = new ServerService(certStream, trustStream){
                                    @Override
                                    public void onResponseReceived(String s){
                                        response = s;
                                    }
                                }.execute("game", "PUT", gameJSON.toString()).get();

                                if (response.startsWith("200")){
                                    Toast toast = Toast.makeText(SelectMapActivity.this, getText(R.string.creation_succ), Toast.LENGTH_LONG);
                                    toast.show();
                                    finish();
                                } else {
                                    Toast toast = Toast.makeText(SelectMapActivity.this, getText(R.string.creation_fail), Toast.LENGTH_LONG);
                                    toast.show();
                                    finish();
                                }

                            } catch( org.json.JSONException e ){
                                e.printStackTrace();
                            } catch (java.util.concurrent.ExecutionException e){
                                e.printStackTrace();
                            } catch( java.lang.InterruptedException e){
                                e.printStackTrace();
                            }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        refreshStreams();

        try {

            response = new ServerService(certStream, trustStream){
                @Override
                public void onResponseReceived(String s){
                    response = s;
                }
            }.execute("game", "GET").get();

            JSONObject gamesJSON = new JSONObject(response.split("200 - ")[1]);

            if (gamesJSON.getJSONArray("games") != null){
                JSONArray gamesArray = gamesJSON.getJSONArray("games");

                for (int i=0; i<gamesArray.length(); i++){

                    JSONObject gameJSON = gamesArray.getJSONObject(i);
                    String owner = gameJSON.getString("owner");
                    final String map = gameJSON.getString("mapname");

                    Button btnMapName = new Button (this);

                    btnMapName.setText("Map: " + map + "\n Created by: " + owner);
                    btnMapName.setTextColor(Color.WHITE);
                    btnMapName.setBackgroundColor(Color.parseColor("#75c4bc"));
                    LinearLayout ll = (LinearLayout) findViewById(R.id.linlayout);

                    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10, 10, 0, 0);
                    btnMapName.setLayoutParams(lp);
                    ll.addView(btnMapName, lp);

                    btnMapName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = ActivityToken.passUserTokenMap(
                                    SelectMapActivity.this,
                                    MazePlayerActivity.class,
                                    getIntent().getExtras().getString("accesstoken"),
                                    getIntent().getExtras().getString("username"),
                                    map);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            } else {
            }


        } catch ( Exception e ){
            e.printStackTrace();
        }
    }

    private void refreshStreams(){
        try {
            certStream = getApplicationContext().getAssets().open("testks.bks");
            trustStream = getApplicationContext().getAssets().open("truststore.bks");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}