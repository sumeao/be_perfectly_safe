package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Game extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //綁定元件
        ImageButton button = findViewById(R.id.bt1);
        ImageButton button2 = findViewById(R.id.bt2);
        ImageButton button3 = findViewById(R.id.bt3);

        //設定監聽事件(跳轉至TicTacToeIntroAssign)
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Game.this,TicTacToeIntroAssign.class);
                startActivity(intent);
                finish();
            }
        });

        //設定監聽事件(跳轉至WuziqiIntroAssign)
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Game.this,WuziqiIntroAssign.class);
                startActivity(intent);
                finish();
            }
        });

        //設定監聽事件(跳轉至RememberIntroAssign)
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Game.this,RememberIntroAssign.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
