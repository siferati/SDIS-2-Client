package com.feup.sdis.mapapp.client;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class implements the required HTTP communication methods
 */
public class ServerService extends AsyncTask<String, Void, String> {

    /** URL to reach **/
    protected URL url;
    /** HttpURLConnection to make the communication **/
    protected HttpURLConnection urlConnection;
    /** The response returned by the server **/
    String response;

    int responseCode;

    /** Default Constructor, does nothing **/
    public ServerService() {
        this.url = null;
        this.urlConnection = null;
        this.response = null;
    }

    @Override
    public String doInBackground(String... parameters) {

        /** Parameters:
         *  0 - context
         *  1 - Method
         *  2 - Post body (PUT,POST)
         * **/

        try {
            // "http://10.0.2.2:8000/maps?name=mapa1
            url = new URL("http://192.168.1.70:8000/" + parameters[0]);
            String method = parameters[1];

            /** open the connection and set the necessary method (params) **/
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);

            switch(method) {
                case "GET":{
                    getResponse(urlConnection);
                    break;
                }
                case "POST": {
                    String body = parameters[2];
                    postBody(urlConnection, body);
                    break;
                }
                case "DELETE": {
                    getResponse(urlConnection);
                    break;
                }
                case "PUT" : {
                    String body = parameters[2];
                    postBody(urlConnection, body);
                    break;
                }
                default: return null;
            }

            return String.valueOf(this.responseCode) + " - " + this.response;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);

        Log.i("Response", "" + this.response);
    }

    private void getResponse(HttpURLConnection urlConnection) throws IOException{

        InputStreamReader isw = new InputStreamReader(urlConnection.getInputStream());
        BufferedReader reader = new BufferedReader(isw);

        String response = "";
        String line = "";

        while ((line = reader.readLine()) != null) {
            response += line;
        }

        this.response = response;
        this.responseCode = urlConnection.getResponseCode();
    }

    private String postBody(HttpURLConnection urlConnection, String requestBody) throws IOException {

        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);

        DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
        out.write(requestBody.getBytes());

        Log.i("LOGGGG", requestBody);

        getResponse(urlConnection);

        return this.response;

    }

    public static int decodeResponse(String fullResponse) {
        return Integer.parseInt(fullResponse.split(" - ")[0]);
    }

}
