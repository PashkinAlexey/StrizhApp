package net.pashkin.strizhapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final String TAG="myLogs";
    private String cookie=null;
    private String preGetURL="https://strizhapp.ru/api/post?filters[feed]=true&conditions[user_id][<>]=1&order[created_at]=desc&page_size=10&page=";
    private String dateURL="&conditions[created_at][<]=";
    private String loginUrl="https://strizhapp.ru/api/auth";
    private JSONObject jObj;
    private boolean loading=false;
    private String lastNoteDate;
    private ArrayList<Map<String, Object>> data;
    String[] from = {"userId", "created", "title", "description" };
    int[] to = { R.id.userName, R.id.created, R.id.title, R.id.description };
    private SimpleAdapter sAdapter;

    private int page=1;
    ListView lvMain;

    public void setCookie(String cokes){
        cookie=cokes;
    }

    public void setJson(JSONObject jsonObject){
        jObj=jsonObject;
    }

    public void getJson(){
        if (!loading) {
            loading=true;
            GetJsonData getJson = new GetJsonData(this);
            if (page==1) {
                Long curentDate = System.currentTimeMillis();
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                lastNoteDate = outputFormat.format(curentDate);
            }
            String getURL = preGetURL+page+dateURL+lastNoteDate;
            Log.d(TAG, "URL: " + getURL);
            getJson.getJSONFromUrl(getURL, cookie);
        }
    }

    public void adapterCreator() throws JSONException {
        JSONArray objects;
        if (page==1) {
            data = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> m;
        objects = jObj.getJSONObject("data").getJSONArray("post");
        String userId,title,description,created="";
        //Парсинг данных о каждом транспорте
        for (int i=0; i<objects.length(); i++){
            m = new HashMap<String, Object>();
            JSONObject objCurrent=(JSONObject) objects.get(i);
            //Парсинг айди пользователя
            userId=objCurrent.getString("user_id");
            //Парсинг заголовка
            title=objCurrent.getString("title");
            //Парсинг описания
            description=objCurrent.getString("description");
            //Парсинг времени
            String inputDateStr=objCurrent.getString("created_at");
            try {
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                DateFormat outputFormat = new SimpleDateFormat("dd MMM yy");
                Date date = inputFormat.parse(inputDateStr);
                created=outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                created=inputDateStr;
                Toast toast = Toast.makeText(this,"Ошибка даты", Toast.LENGTH_SHORT);
                toast.show();
            }
            m.put("userId", userId);
            m.put("created", created);
            m.put("title", title);
            m.put("description", description);
            data.add(m);
            loading=false;
        }

        if (page==1) {
            sAdapter = new SimpleAdapter(this, data, R.layout.list_adapter, from, to);
            lvMain.setAdapter(sAdapter);
        }
        else {
            sAdapter.notifyDataSetChanged();
        }
        page++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvMain=(ListView) findViewById(R.id.lvMain);
        final SwipeRefreshLayout swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
               // Log.d(TAG, Integer.toString(scrollState));
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem+visibleItemCount==totalItemCount)&(totalItemCount>0)){
                        getJson();
                }
            }
        });

        SendLoginData login=new SendLoginData();
        login.sendDataWithPost(this, loginUrl);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page=1;
                getJson();
                swipeRefresh.setRefreshing(false);
            }
        });
    }
}
