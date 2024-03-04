package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
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

public class AD8 extends AppCompatActivity {

    private TextView textQuestionNumber, textQuestion , hint , textExplain1 , textExplain2 , textScores;
    private RadioGroup radioGroup;
    private RadioButton radioYes, radioNo;
    private Button buttonNext , buttonPreviousPage;
    private int currentQuestionNumber = 1;
    private int score = 0;
    private String tableName = "ad_8";
    private String IP;
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad8);

       SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);


        handler = new Handler(Looper.getMainLooper());

       IP = getPrefs.getString("IP", "null");

        setView();

        updateQuestion(currentQuestionNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if(selectedId == -1){
                    Toast.makeText(AD8.this,"未點選答案 !" ,Toast.LENGTH_SHORT).show();
                }else if (selectedId == R.id.radio_yes) {
                    currentQuestionNumber++;
                    score++;
                    hint.setVisibility(View.GONE);

                }else {
                    currentQuestionNumber++;
                    hint.setVisibility(View.GONE);
                }

                if (currentQuestionNumber > 8) {
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
        hint.setVisibility(View.GONE);
        buttonNext.setVisibility(View.GONE);
        textExplain1.setVisibility(View.VISIBLE);
        textScores.setText("您的分數為:"+score);
        if(score>=2){
            textExplain1.setText("可能有存在失智症風險，建議做進一步檢查");
        }else{
            textExplain1.setText("暫無失智症風險");
        }
        textScores.setVisibility(View.VISIBLE);
        buttonPreviousPage.setVisibility(View.VISIBLE);
    }

    private void setView() {
        textQuestionNumber = findViewById(R.id.text_question_number);
        textQuestion = findViewById(R.id.text_question);
        hint = findViewById(R.id.text_question2);
        textExplain1 = findViewById(R.id.explain_1);
        textScores = findViewById(R.id.scores);
        radioGroup = findViewById(R.id.radio_group);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);
        buttonNext = findViewById(R.id.button_next);
        buttonPreviousPage = findViewById(R.id.previous_page);
    }

    private void updateQuestion(int questionNumber) {

        String url = "http://"+IP+"/Scale/Scale_questions?question_number=" + questionNumber + "&" + "table=" + tableName;

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("AD8", "Response: " + response); // 在 onResponse 方法中


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String question = jsonObject.getString("Question");
                            question = android.text.Html.fromHtml(question).toString();

                            textQuestionNumber.setText("問題 " + questionNumber);
                            textQuestion.setText(question);

                            radioGroup.clearCheck();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        handler.removeCallbacksAndMessages(null);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                hint.setVisibility(View.VISIBLE);

                                switch (questionNumber) {
                                    case 1:
                                        hint.setText("小提示:和先前比較，有〝判斷力〞的變差， 例如：容易被詐騙、明顯錯誤的投資、 或過生日卻送『鐘』給對方，對方是男孩卻送裙子，不熟的朋友卻送昂貴的禮物等");
                                        break;
                                    case 2:
                                        hint.setText("小提示:和先前比較，變得不愛出門、對之前從事的活動顯著的興趣缺缺，但需排除因環境變異因素引起或因行動能力所影響。例如：之前常前往活動中心唱卡拉OK，現在卻不願意去，而並非因為卡拉OK設備不佳所影響。");
                                        break;
                                    case 3:
                                        hint.setText("小提示:重複問同樣的問題，或重複述說過去的事件等。");
                                        break;
                                    case 4:
                                        hint.setText("小提示:和先前比較，對於小型器具的使用能 力降低，例如：時常打錯電話或電話 撥不出去，不會使用遙控器開電視。 ※使用器具能力的變化，需過去患者 會使用，但現在卻不會，且有『改變』的情形發生。");
                                        break;
                                    case 5:
                                        hint.setText("小提示:記憶力減退，忘記正確的年月、或說錯自己的年齡");
                                        break;
                                    case 6:
                                        hint.setText("小提示:較複雜的財物處理的活動，例如：過 去皆負責所得稅的申報、水電費的繳 款、信用卡帳單繳費等，現在卻常發 生沒繳費、或多繳或少繳錢的情形， 與過去相比有改變。");
                                        break;
                                    case 7:
                                        hint.setText("小提示:與他人有約卻記不住時間日期，經提 醒也想不起來，常常忘記約會等。");
                                        break;
                                    case 8:
                                        hint.setText("小提示:綜合衡論而言，在過去的半年或一年 來是否有持續性的思考力或記憶力的 障礙，例如：每天大多或多或少有思 考和記憶力的問題");
                                        break;

                                }

                            }
                        }, 10000); // 10秒後執行
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e("AD8", "Error: " + error.getMessage()); // 在 onErrorResponse 方法中

                    }
                });

        queue.add(stringRequest);
    }

    private void uploadScore(int score , String tableName) {

//        192.168.143.250
//        String url = "h`ttp://120.105.161.194/Scale/Scale_score.php";
        String url = "http://" + IP + "/Scale/Scale_score.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("AD8", "Response: " + response); // 在 onResponse 方法中

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AD8", "Error: " + error.getMessage()); // 在 onErrorResponse 方法中

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