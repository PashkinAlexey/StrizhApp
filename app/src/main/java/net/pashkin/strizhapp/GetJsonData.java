package net.pashkin.strizhapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Алексей on 12.09.2016.
 */

public class GetJsonData {
    final String TAG="myLogs";
    private MainActivity mainActivity;
    public void getJSONFromUrl(MainActivity activ, String url, String cookie){
        mainActivity=activ;
        Async myTask=new Async();
        myTask.execute(url, cookie);
    }

    private class Async extends AsyncTask<Object, Void, JSONObject>
    {
        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground (Object...params){
            JSONObject jsonObject = null;
            try {
                String authURL = (String) params[0];
                String cookie = (String) params[1];
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                String str = "";

                URL targetUrl = new URL(authURL);

                conn = (HttpURLConnection) targetUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("cookie", cookie);
                conn.connect();

                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                str = buffer.toString();
                jsonObject = new JSONObject(str);
            } catch (IOException e) {
                Log.d(TAG, "Ошибка загрузки");
            } catch (JSONException e) {
                Log.d(TAG, "Ошибка данных");
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute (JSONObject jsonObject){
            if (jsonObject != null) {
                Log.d(TAG, "Json: " + jsonObject.toString());
            }
        }
    }
}
