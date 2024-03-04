package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class map_registration extends AppCompatActivity {

    private EditText master1, master2, res1, res2, setday;
    Button button_Submit;

    private String res1data, res2data, daydata;

    private TextView textView_Error , textView_LoginNow;

    ProgressBar progressBar;

    private String IP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_registration);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");

        setView();

        if ("wmg109".equals(master1.getText().toString()) && "wmg109".equals(master2.getText().toString())) {
            // master1 和 master2 均为 "wmg109"，执行上传操作
            // ... (之前的上传操作代码)
        } else {
            // master1 或 master2 不是 "wmg109"，显示错误消息或采取其他操作


        }

        textView_LoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), map_login.class);
                startActivity(intent);
                finish();
            }
        });

        button_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                res1data = String.valueOf(res1.getText());
                res2data = String.valueOf(res2.getText());
                daydata = String.valueOf(setday.getText());

                // 检查 master1 和 master2 的值是否都为 "wmg109"
                if ("wmg109".equals(master1.getText().toString()) && "wmg109".equals(master2.getText().toString())) {
                    // master1 和 master2 均为 "wmg109"，执行上传操作
                    RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                    String url1 ="http://"+IP+"/location/log/usersAccount/Register.php";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {


                                    Log.i("狀態", response);

                                    String status = "Success";

                                    if (response.trim().equals("Success")) {

                                        Intent intent = new Intent(getApplicationContext(), map_login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                        }
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> paramV = new HashMap<>();
                            paramV.put("res1data", res1data);
                            paramV.put("res2data", res2data);
                            paramV.put("daydata", daydata);
                            return paramV;
                        }
                    };
                    queue1.add(stringRequest);
                } else {
                    // master1 或 master2 不是 "wmg109"，显示错误消息或采取其他操作


                }
            }
        });




    }

    void setView() {
        master1 = findViewById(R.id.master1);
        master2 = findViewById(R.id.master2);
        res1 = findViewById(R.id.res1);
        res2 = findViewById(R.id.res2);
        setday = findViewById(R.id.setday);
        button_Submit = findViewById(R.id.submit);
//        textView_Error = findViewById(R.id.test_error);

        textView_LoginNow = findViewById(R.id.loggin);

    }
}