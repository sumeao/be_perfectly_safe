package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DetectionResult extends AppCompatActivity {

    TextView detectionResult_name , detectionResult_birthday , detectionResult_age,
            detectionResult_gender , detectionResult_evaluate , detectionResult_attentionAndsuggestion , test;

    Button viewAnswer;

    int totalScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_result);
        setView();

        Intent intent = getIntent();

        totalScore = intent.getIntExtra("totalScore",0);


    }

    void setView(){
        detectionResult_name = findViewById(R.id.detectionResult_name);
        detectionResult_birthday = findViewById(R.id.detectionResult_birthday);
        detectionResult_age = findViewById(R.id.detectionResult_age);
        detectionResult_gender = findViewById(R.id.detectionResult_gender);
        detectionResult_evaluate = findViewById(R.id.detectionResult_evaluate);
        detectionResult_attentionAndsuggestion = findViewById(R.id.detectionResult_attentionAndsuggestion);
        viewAnswer = findViewById(R.id.viewAnswer);
    }
}