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

public class RememberPicture extends AppCompatActivity {

    ImageButton curView = null;
    private int countPair = 0;
    int currentPos = -1;
    int bt1 = Integer.MAX_VALUE;
    ArrayList<Integer> check = new ArrayList();
    TextView myscore;
    Button giveup;

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
        setContentView(R.layout.activity_remember_picture);
        int imgID[] = {
                R.drawable._1,
                R.drawable._2,
                R.drawable._3,
                R.drawable._4,
                R.drawable._5,
                R.drawable._6,
                R.drawable._7,
                R.drawable._8,
        };
        int[] pos = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7};
        for (int k = 0; k < 100; k++) {
            for (int i = 0; i < 16; i++) {
                int tmp;
                int j = (int) (Math.random() * 16);
                tmp = pos[i];
                pos[i] = pos[j];
                pos[j] = tmp;
            }
        }
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

        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RememberPicture.this)
                        .setTitle("確定要放棄嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(RememberPicture.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).create()
                        .show();

            }
        });

        for (int i = 0; i < 16; i++) {
            int bt = i;

            button[i].setOnClickListener(new View.OnClickListener() {

                @SuppressLint({"UseCompatLoadingForDrawables", "NewApi"})
                @Override
                public void onClick(View v) {

                    if (checkbt(bt)) {
                        if (currentPos < 0) {
                            currentPos = pos[bt];
                            curView = button[bt];
                            check.add(bt);
                            bt1 = bt;
                            button[bt].setImageDrawable(getDrawable(imgID[pos[bt]]));
                        } else {
                            if (curView == button[bt]) {
                                button[bt].setImageDrawable(getDrawable(R.drawable.question));
                                check.remove((Object) "bt");
                                currentPos = -1;
                            } else if (currentPos != pos[bt]) {
                                button[bt].setImageDrawable(getDrawable(imgID[pos[bt]]));

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                curView.setImageDrawable(getDrawable(R.drawable.question));
                                                button[bt].setImageDrawable(getDrawable(R.drawable.question));
                                                Toast.makeText(
                                                        getApplicationContext(), "Not match", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                                check.remove((Object) bt1);
                            } else {
                                button[bt].setImageDrawable(getDrawable(imgID[pos[bt]]));
                                countPair++;
                                check.add(bt);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myscore.setText("目前分數 : "+countPair*10);
                                    }
                                });
                                int sum = pos.length / 2;
                                if (countPair == sum) {
                                    Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
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
                                                    Intent intent = new Intent(RememberPicture.this, RememberChoice.class);
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