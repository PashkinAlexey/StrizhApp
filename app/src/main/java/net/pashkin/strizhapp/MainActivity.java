package net.pashkin.strizhapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final String TAG="myLogs";
    private String cookie=null;
    private String getURL="https://strizhapp.ru/api/post?filters[feed]=true&conditions[user_id][<>]=1";
    private String loginUrl="https://strizhapp.ru/api/auth";
    private JSONObject jObj;

    public void setCookie(String cokes){
        cookie=cokes;
    }

    public void setJson(JSONObject jsonObject){
        jObj=jsonObject;
    }

    public void getJson(){
        GetJsonData getJson=new GetJsonData();
        getJson.getJSONFromUrl(this, getURL, cookie);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendLoginData login=new SendLoginData();
        login.sendDataWithPost(this, loginUrl);
    }
}
