package com.example.be_perfectly_safe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

public class wuziqifinal extends AppCompatActivity {

    private WuziqiPanel panel;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog alertDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wuziqifinal);


        alertBuilder = new AlertDialog.Builder(wuziqifinal.this);
        alertBuilder.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                panel.restartGame();
            }
        });
//        alertBuilder.setNegativeButton("退出遊戲", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                wuziqifinal.finish();
//            }
//        }).setCancelable(false);
//        alertBuilder.setTitle("此局結束");


        panel = (WuziqiPanel) findViewById(R.id.wuziqi_panel);
        panel.setOnGameStatusChangeListener(new OnGameStatusChangeListener() {
            @Override
            public void onGameOver(int gameWinResult) {
                switch (gameWinResult) {
                    case WuziqiPanel.WHITE_WIN:
                        alertBuilder.setMessage("白棋勝利!");
                        break;
                    case WuziqiPanel.BLACK_WIN:
                        alertBuilder.setMessage("黑棋勝利!");
                        break;
                    case WuziqiPanel.NO_WIN:
                        alertBuilder.setMessage("和棋!");
                        break;
                }
                alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });
    }
}