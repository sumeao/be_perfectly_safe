package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WuZiQi extends AppCompatActivity {
    Button letgo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wu_zi_qi);

        //綁定元件
        letgo = findViewById(R.id.letgo);

        //設定監聽事件(跳轉至wuziqifinal)
        letgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WuZiQi.this, wuziqifinal.class);
                startActivity(intent);
            }
        });
    }
}
