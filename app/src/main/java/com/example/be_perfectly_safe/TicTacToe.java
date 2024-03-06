package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TicTacToe extends AppCompatActivity {

    private Button btn_2player , btn_robot;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        //綁定元件
        btn_2player = findViewById(R.id.btn_2player);
        btn_robot = findViewById(R.id.btn_robot);

        //設定監聽事件(跳轉至TicTwoPlayer)
        btn_2player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicTacToe.this,TicTwoPlayer.class));
                finish();
            }
        });

        //設定監聽事件(跳轉至TicOnePlayer)
        btn_robot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TicTacToe.this,TicOnePlayer.class));
                finish();
            }
        });
    }
}
