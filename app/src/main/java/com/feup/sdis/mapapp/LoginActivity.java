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

public class LoginActivity extends AppCompatActivity {

    private InputStream certStream;

    private InputStream trustStream;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        try {
            this.certStream = this.getApplicationContext().getAssets().open("testks.bks");
            this.trustStream = this.getApplicationContext().getAssets().open("truststore.bks");

        } catch (Exception e){
            e.printStackTrace();
        }

        Button loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                }catch(Exception e ){

                }
            }
        });

        TextView signUp = (TextView) findViewById(R.id.signupText);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }catch(Exception e ){

                }
            }
        });

    }

    public void login() {
        if (!validateFields()){
            Toast.makeText(LoginActivity.this, getText(R.string.login_fail_missing), Toast.LENGTH_SHORT).show();
            return;
        }

        EditText userText = (EditText) findViewById(R.id.input_username);
        EditText userPass = (EditText) findViewById(R.id.input_password);

        String username = userText.getText().toString();
        String userpass = userPass.getText().toString();

        // Do Login
        JSONObject userJSON = new JSONObject();
        String response = null;
        try {
            userJSON.put("username", username);
            userJSON.put("userhash", bin2hex(getHash(userpass)));

            response = new ServerService(certStream, trustStream).execute("users", "POST", userJSON.toString()).get();
        } catch (Exception e ){
            e.printStackTrace();
        }

        if (response != null){
            if (response.startsWith("303")){
                Log.i("asd", response);
            } else if (response.startsWith("403")){
                Toast.makeText(LoginActivity.this, getText(R.string.login_fail_userinvalid), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            return;
        }

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

}
