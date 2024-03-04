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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MoCA extends AppCompatActivity {

    private TextView textQuestionNumber, textQuestion;
    private RadioGroup radioGroup;
    private RadioButton radioYes, radioNo;
    private Button buttonNext;
    private int currentQuestionNumber = 1;
    private String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mo_ca);

        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        IP = getPrefs.getString("IP", "null");


        textQuestionNumber = findViewById(R.id.text_question_number);
        textQuestion = findViewById(R.id.text_question);
        radioGroup = findViewById(R.id.radio_group);
        radioYes = findViewById(R.id.radio_yes);
        radioNo = findViewById(R.id.radio_no);
        buttonNext = findViewById(R.id.button_next);

        updateQuestion(currentQuestionNumber);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionNumber++;

                if(currentQuestionNumber>13){
                    finish();
                }

                updateQuestion(currentQuestionNumber);
            }
        });

    }

    private void updateQuestion(int questionNumber) {
//        String url = "http://120.105.161.194/Scale/MoCA?question_number=" + questionNumber;
        String url = "http://"+IP+"/Scale/Scale_questions?question_number=" + questionNumber + "&" + "table=moca";

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
}