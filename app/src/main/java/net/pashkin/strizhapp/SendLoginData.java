package net.pashkin.strizhapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Алексей on 07.10.2016.
 */

public class SendLoginData{
    final String TAG = "myLogs";
    private MainActivity mainActivity;
    public void sendDataWithPost(MainActivity activ, String url){
        mainActivity = activ;
        Async myTask=new Async();
        myTask.execute(url);
    }
    private class Async extends AsyncTask<Object, Void, String>
        {
            @Override
            protected void onPreExecute () {
            super.onPreExecute();
        }

            @Override
            protected String doInBackground (Object...params){
            String AuthURL = (String) params[0];
            String parammeters =
                    "{\"phone\": \"79045173703\", " +
                            "\"device_type\": \"android\", " +
                            "\"device_token\": \"xxxxxxxxxxxxxxxxx1\", " +
                            "\"type\": \"password\", " +
                            "\"application\": \"com.strizhapp\", " +
                            "\"system_version\": \"9.3.1\", " +
                            "\"application_version\": \"1.0.0\", " +
                            "\"password\": \"123456\"}";
            //Log.d(TAG, parammeters);
            byte[] data = null;
            InputStream is = null;
            BufferedReader reader = null;
            String resultString = null;

            try {
                URL url = new URL(AuthURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                //conn.setDoInput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Suppress-HTTP-Code", "true");
                OutputStream os = conn.getOutputStream();
                data = parammeters.getBytes("UTF-8");
                os.write(data);

                conn.connect();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, Integer.toString(responseCode));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //if (responseCode == 200) {
                    /*is = conn.getInputStream();

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }*/

                resultString = conn.getHeaderField("Set-Cookie");
                //}
            } catch (IOException e) {
                Log.d(TAG, "Ошибка авторизации:");
            }
            return resultString;
        }

            @Override
            protected void onPostExecute (String resultString){
            if (resultString != null) {
                Log.d(TAG, "Результат: " + resultString);
                mainActivity.setCookie(resultString);
                mainActivity.getJson();
            }
        }
    }
}
