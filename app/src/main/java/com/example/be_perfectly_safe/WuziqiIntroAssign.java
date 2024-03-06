package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class WuziqiIntroAssign extends AppCompatActivity {
    Button BtnWuziqi;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuziqi_intro_assign);

        //綁定元件
        BtnWuziqi = findViewById(R.id.BtnWuziqiAssign);

        //設定監聽事件(跳轉至WuZiQi)
        BtnWuziqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WuziqiIntroAssign.this,WuZiQi.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
