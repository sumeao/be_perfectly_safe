package com.example.be_perfectly_safe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RememberAnimal extends AppCompatActivity {

    ImageButton curView = null;
    private int countPair = 0;
    int currentPos = -1;
    int bt1 = Integer.MAX_VALUE;
    ArrayList<Integer> check = new ArrayList();
    TextView myscore;
    Button giveup;

    //判斷牌是否已被翻開，若牌已在check的陣列裡，代表已被翻開
    public boolean checkbt(int c) {
        boolean s = true;
        for (int i = 0; i < check.size(); i++) {
            if (c == check.get(i)) {
                s = false;
            }
        }
        return s;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_animal);

         //設定圖片
        int imgID[] = {
                R.drawable.lion,
                R.drawable.monkey,
                R.drawable.rate,
                R.drawable.cat,
                R.drawable.dog,
                R.drawable.cow,
                R.drawable.butterfly,
                R.drawable.bug,
                R.drawable.leave,
                R.drawable.flower,
                R.drawable.gress,
                R.drawable.boon,
                R.drawable.fish,
                R.drawable.cheese,
                R.drawable.banan,
                R.drawable.meat,
        };

        //設定判斷標準
        int[] pos = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};

        //洗牌
        for (int k = 0; k < 100; k++) {
            for (int i = 0; i < 16; i++) {
                int tmp,test;
                int j = (int) (Math.random() * 16);
                tmp = pos[i];
                pos[i] = pos[j];
                pos[j] = tmp;
                test = imgID[i];
                imgID[i] = imgID[j];
                imgID[j] = test;

            }
        }

        //綁定元件
        ImageButton button[] = new ImageButton[16];
        button[0] = findViewById(R.id.bt0);
        button[1] = findViewById(R.id.bt1);
        button[2] = findViewById(R.id.bt2);
        button[3] = findViewById(R.id.bt3);
        button[4] = findViewById(R.id.bt4);
        button[5] = findViewById(R.id.bt5);
        button[6] = findViewById(R.id.bt6);
        button[7] = findViewById(R.id.bt7);
        button[8] = findViewById(R.id.bt8);
        button[9] = findViewById(R.id.bt9);
        button[10] = findViewById(R.id.bt10);
        button[11] = findViewById(R.id.bt11);
        button[12] = findViewById(R.id.bt12);
        button[13] = findViewById(R.id.bt13);
        button[14] = findViewById(R.id.bt14);
        button[15] = findViewById(R.id.bt15);

        giveup = findViewById(R.id.giveup);
        myscore = findViewById(R.id.myscore);

        //設定監聽事件
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RememberAnimal.this)
                        .setTitle("確定要放棄嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RememberAnimal.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).setNegativeButton("取消", null).create()
                        .show();

            }
        });

        for (int i = 0; i < 16; i++) {
            int bt = i;

            button[i].setOnClickListener(new View.OnClickListener() {//設定16張圖的監聽事件

                @SuppressLint({"UseCompatLoadingForDrawables", "NewApi"})
                @Override
                public void onClick(View v) {

                    if (checkbt(bt)) {//判斷圖片是否已被翻開
                        if (currentPos < 0) {//判斷是否為目前翻開的第一張
                            currentPos = pos[bt];//紀錄目前翻開的編號
                            curView = button[bt];//紀錄目前翻開的按鈕
                            check.add(bt);
                            bt1 = bt;
                            button[bt].setImageDrawable(getDrawable(imgID[bt]));//設定圖片
                        } else {
                            if (currentPos == pos[bt]) {//如果翻開同一個按鈕，將圖片翻回問號的樣子
                                button[bt].setImageDrawable(getDrawable(R.drawable.question));
                                check.remove((Object) "bt");//移除arraylist資料
                                currentPos = -1;
                            } else if (currentPos + pos[bt]!=15 && currentPos != pos[bt]) {//如果翻開不同按鈕，但編號相加不為15
                                button[bt].setImageDrawable(getDrawable(imgID[bt]));//設定圖片

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {//延遲0.5秒
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                curView.setImageDrawable(getDrawable(R.drawable.question));
                                                button[bt].setImageDrawable(getDrawable(R.drawable.question));//將兩張圖片翻回來
                                                Toast.makeText(
                                                        getApplicationContext(), "Not match", Toast.LENGTH_SHORT).show();//顯示not mach的提示
                                            }
                                        });
                                    }
                                }).start();
                                check.remove((Object) bt1);
                            } else {
                                button[bt].setImageDrawable(getDrawable(imgID[bt]));//設定圖片
                                countPair++;//已配對數+1
                                check.add(bt);
                                runOnUiThread(new Runnable() {//執行加分
                                    @Override
                                    public void run() {
                                        myscore.setText("目前分數 : " + countPair * 10);
                                    }
                                });
                                int sum = pos.length / 2;
                                if (countPair == sum) {//如果全部配對完成
                                     Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_SHORT).show();//顯示 you win的提示
                                    new Thread(new Runnable() {//延遲2秒
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(RememberPicture.this, RememberChoice.class);//執行跳轉
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                            currentPos = -1;
                        }
                    }
                }
            });
        }

    }
}
