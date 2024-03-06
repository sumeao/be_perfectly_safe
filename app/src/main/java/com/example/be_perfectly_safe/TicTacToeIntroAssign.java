package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TicTacToeIntroAssign extends AppCompatActivity {
    Button BtnTic;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe_intro_assign);

        //綁定元件
        BtnTic = findViewById(R.id.BtnTTTAssign);

        //設定監聽事件(跳轉至TicTacToe)
        BtnTic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicTacToeIntroAssign.this,TicTacToe.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
