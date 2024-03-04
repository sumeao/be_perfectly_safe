package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class showalldata extends AppCompatActivity {
    private Handler handler = new Handler();
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> locationList = new ArrayList<>();
    private String IP;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showalldata);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);
        listView.setAdapter(adapter);

        // 啟動定時
        handler.post(getDataRunnable);
    }

    private Runnable getDataRunnable = new Runnable() {
        @Override
        public void run() {
            // 用JSON
            String url = "http://"+IP+"/location/getdata.php";
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // 解析 JSON
                            locationList.clear(); // 清除舊數據
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    double latitude = jsonObject.getDouble("latitude");
                                    double longitude = jsonObject.getDouble("longitude");
                                    String timestamp = jsonObject.getString("timestamp");

                                    // 將座標添加到列表
                                    String location = "經度: " + latitude + "\n" +"  緯度: " + longitude + "\n" + "  時間: " + timestamp;
                                    locationList.add(location);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // 更新ListView的數據
                            adapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 處裡錯誤
                        }
                    });

            queue.add(jsonArrayRequest);


            handler.postDelayed(this, 60000); // 60秒（1分鐘）一次
        }
    };
}