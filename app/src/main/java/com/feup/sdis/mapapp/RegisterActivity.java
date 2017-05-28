package com.feup.sdis.mapapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.feup.sdis.mapapp.client.ServerService;

import org.json.JSONObject;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rui on 27/05/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private InputStream certStream;

    private InputStream trustStream;

    private String response;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        try {
            this.certStream = this.getApplicationContext().getAssets().open("testks.bks");
            this.trustStream = this.getApplicationContext().getAssets().open("truststore.bks");

        } catch (Exception e){
            e.printStackTrace();
        }

        Button loginBtn = (Button) findViewById(R.id.btn_signup);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signup();
                }catch(Exception e ){

                }
            }
        });

    }

    public void signup(){

        if (!validateFields()){
            Toast.makeText(RegisterActivity.this, getText(R.string.signup_fail), Toast.LENGTH_SHORT).show();
            return;
        }

        EditText userText = (EditText) findViewById(R.id.input_username);
        EditText userPass = (EditText) findViewById(R.id.input_password);

        String username = userText.getText().toString();
        String userpass = userPass.getText().toString();

        JSONObject userJSON = new JSONObject();
        try {
            userJSON.put("username", username);
            userJSON.put("userhash", bin2hex(getHash(userpass)));

            try {
                certStream = getApplicationContext().getAssets().open("testks.bks");
                trustStream = getApplicationContext().getAssets().open("truststore.bks");

            } catch (Exception e){
                e.printStackTrace();
            }

            response = new ServerService(certStream, trustStream){
                @Override
                public void onResponseReceived(String s){
                    response = s;
                }
            }.execute("users", "PUT", userJSON.toString()).get();

            Log.i("Register", response);

            if (response.startsWith("201")){
                Toast.makeText(RegisterActivity.this, getText(R.string.signup_succ), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                this.finish();

            }
            else if (response.startsWith("409")){
                Toast.makeText(RegisterActivity.this, getText(R.string.signup_fail_user), Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                Toast.makeText(RegisterActivity.this, getText(R.string.signup_fail), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e ){
            e.printStackTrace();
        }

        Log.i("Userr", response);

    }

    public byte[] getHash(String password){
        MessageDigest sys = null;
        try {
            sys = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        sys.reset();
        return sys.digest(password.getBytes());
    }

    public String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
    }

    public boolean validateFields(){
        boolean validated = true;

        EditText userText = (EditText) findViewById(R.id.input_username);
        EditText userPass = (EditText) findViewById(R.id.input_password);

        String username = userText.getText().toString();
        String userpass = userPass.getText().toString();

        if (username.isEmpty()){
            userText.setError("Username required!");
            validated = false;
        } else {
            userText.setError(null);
        }
        if (userpass.isEmpty()) {
            userPass.setError("Password required!");
            validated = false;
        } else {
            userPass.setError(null);
        }

        return validated;
    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            this.certStream = this.getApplicationContext().getAssets().open("testks.bks");
            this.trustStream = this.getApplicationContext().getAssets().open("truststore.bks");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
