package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;

public class scale extends AppCompatActivity {

    TextView test;
    TextView detection_question;
    Button detection_yes , detection_no , detection_uncertain;

    int totalScore = 0 , detectionNumber = 0 , answerYes = 4 , answerNo = 2 , answerNotsure = 1;

    String[] detectionQuestion = {"1.(AD-8)是否有判斷力上的困難，如容易受騙、男生生日卻送裙子等?","2.(AD-8)是否對於以前的喜好有明顯的改變，如變得不愛出門或排除設備天氣等其他因素的興致缺乏?"
            ,"3.(AD-8)是否有重複敘說同一件事，或對於近期的事情沒印象，卻記得很久以前的事情?","4.(AD-8)在學習事務上是否比以往困難，如開關電視、如何打電話等?","5.(心智題)是否記得自己生日是年幾月幾號?"
            ,"6.(心智圖)是否有辦法完成從20減2一直減下去到0，且途中只要有任何錯誤時或無法繼續時即停止?","7.(認知題)是否記得今年幾歲?","8.(認知題)是否記得家人的姓名?","9.(認知題)是否記得自己現在身在何處?"
            ,"10.(定向題)是否清楚知道這裡是哪個縣市?","11.(定向題)是否記得何時剛進食過?","12.(疾病史)是否曾經過腦部重大疾病，如腦中風、腦血管破裂等?"};

    ArrayList<Integer> answerSituation = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        setView();

        detection_question.setText(detectionQuestion[detectionNumber]);

        detection_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerSituation.add(answerYes);
                totalScore += answerYes;
                finishAnswering();
                test.setText(String.valueOf(totalScore));
                Log.i("答案", String.valueOf(answerSituation));
            }
        });

        detection_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerSituation.add(answerNo);
                totalScore -= answerNo;
                finishAnswering();
                test.setText(String.valueOf(totalScore)); //要刪掉
                Log.i("答案", String.valueOf(answerSituation));
            }
        });

        detection_uncertain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAnswering();
                answerSituation.add(answerNotsure);
                totalScore += answerNotsure;
                test.setText(String.valueOf(totalScore)); //要刪掉
                Log.i("答案", String.valueOf(answerSituation));
            }
        });



    }

    void setView(){
        test = findViewById(R.id.Test); //要刪掉
        detection_question = findViewById(R.id.detection_question);
        detection_yes = findViewById(R.id.detection_yes);
        detection_no = findViewById(R.id.detection_no);
        detection_uncertain = findViewById(R.id.detection_uncertain);
    }

    void finishAnswering(){
        if(detectionNumber >= 11){
            Toast.makeText(scale.this,"作答完畢",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(scale.this, DetectionResult.class);
            intent.putExtra("totalScore" , totalScore);
            startActivity(intent);
            finish();
        }
        else detectionNumber += 1;

        detection_question.setText(detectionQuestion[detectionNumber]);

    }

}