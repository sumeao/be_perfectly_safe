package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ADL extends AppCompatActivity {

    private TextView textQuestionNumber, textQuestion , textScoreMode , textExplain1 , textExplain2 , textExplain3 , textScores;
    private RadioGroup radioGroup;
    private RadioButton radioYes, radioNo;
    private Button buttonNext , buttonPreviousPage;
    private int currentQuestionNumber = 1;
    private int score = 0;
    private String tableName = "adl";
    private String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adl);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");

        setView();

        updateQuestion(currentQuestionNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if(selectedId == -1){
                    Toast.makeText(ADL.this,"未點選答案 !" ,Toast.LENGTH_SHORT).show();
                }else if (selectedId == R.id.radio_yes) {
                    currentQuestionNumber++;
                    score++;
                }else {
                    currentQuestionNumber++;
                }

                if (currentQuestionNumber > 10) {
                    uploadScore(score,tableName);
                    showScores();
                } else {
                    updateQuestion(currentQuestionNumber);
                }
            }

        });

        buttonPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void showScores() {
        textQuestionNumber.setVisibility(View.GONE);
        textQuestion.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        buttonNext.setVisibility(View.GONE);

        textExplain1.setVisibility(View.VISIBLE);
        textScores.setText("您的分數為:"+score);
        if(score>65){
            textExplain1.setText("可能行為上有些許不方便，但尚能自行行動");
        }else if(score>35 && score<60){
            textExplain1.setText("可能行為上有些許不方便，建議前往醫院做進一步追蹤");
        }else{
            textExplain1.setText("可能行為上無法自行行動，建議前往醫院做進一步追蹤");
        }
        textScores.setVisibility(View.VISIBLE);
        buttonPreviousPage.setVisibility(View.VISIBLE);
    }

    private void setView() {
        textQuestionNumber = findViewById(R.id.text_question_number);
        textQuestion = findViewById(R.id.text_question);
        textExplain1 = findViewById(R.id.explain_1);
        textScores = findViewById(R.id.scores);
        radioGroup = findViewById(R.id.radio_group);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);
        buttonNext = findViewById(R.id.button_next);
        buttonPreviousPage = findViewById(R.id.previous_page);
    }

    private void updateQuestion(int questionNumber) {
        String url = "http://"+IP+"/Scale/ADL.php?question_number=" + questionNumber;
//        String url = "http://" + IP + questionNumber + "&" + "table="  + tableName;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String question = jsonObject.getString("Question");
                            question = android.text.Html.fromHtml(question).toString();

                            textQuestionNumber.setText("問題 " + questionNumber);
                            textQuestion.setText(question);

                            // 清除选择
                            radioGroup.clearCheck();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        queue.add(stringRequest);
    }

    private void uploadScore(int score , String tableName) {
        String url = "http://" + IP + "/Scale/Scale_score.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("score", String.valueOf(score));
                params.put("table", tableName);
                return params;
            }
        };
        queue.add(stringRequest);

    }
}