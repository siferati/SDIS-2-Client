package com.feup.sdis.mapapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Utilizador on 21-05-2017.
 */

public class CreateMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        Button btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(CreateMapActivity.this, SelectMapActivity.class);
                    startActivity(intent);
                }catch(Exception e ){
                    Toast.makeText(CreateMapActivity.this, getText(R.string.no_internet_error), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}