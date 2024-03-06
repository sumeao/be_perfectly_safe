package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class scale_choise extends AppCompatActivity {

    Button btn1 , btn2 , btn3 , btn4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_choise);

        //綁定元件
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

       //設定監聽事件(跳轉至AD8)
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(scale_choise.this, AD8.class));
            }
        });

        //設定監聽事件(跳轉至ADL)
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(scale_choise.this,ADL.class));
            }
        });

        //設定監聽事件(跳轉至CASI)
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(scale_choise.this,CASI.class));
            }
        });
        
       //設定監聽事件(跳轉至MoCA)
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(scale_choise.this,MoCA.class));
            }
        });

    }
}