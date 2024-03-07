package com.example.be_perfectly_safe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class family extends AppCompatActivity {

    ImageButton curView = null;
    private int countPair = 0;
    int currentPos = -1;
    int bt1 = Integer.MAX_VALUE;
    ArrayList<Integer> check = new ArrayList<>();
    TextView myscore, title;
    Button giveup;
    Bitmap photoArray[] = new Bitmap[16];
    String label[] = new String[16];
    String lLabel[] = new String[8];
    String rLabel[] = new String[8];
    String direction[] = new String[16];
    String lDirection[] = new String[8];
    String rDirection[] = new String[8];
    String name, accountArgs;
    private SQLiteDatabase db;
    ImageButton button[] = new ImageButton[16];
    String selectionArgs[] = new String[1];
    private Handler handler;
    Integer location, count = 0, cvbt;
    ImageView title_iv;
    ArrayList<Bitmap> lPhoto = new ArrayList();
    ArrayList<Bitmap> rPhoto = new ArrayList();
    ArrayList<String> lLabelArray = new ArrayList();
    ArrayList<String> rLabelArray = new ArrayList();
    ArrayList<String> lDirectionArray = new ArrayList();
    ArrayList<String> rDirectionArray = new ArrayList();
    ArrayList<Integer> hint = new ArrayList();
    Integer[][] block = new Integer[4][4];


    //檢查按鈕是否已被翻開
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
        setContentView(R.layout.activity_family);

        int[] pos = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};//設定判斷編號
        int[] bg = {R.drawable.spades, R.drawable.spades, R.drawable.spades, R.drawable.spades, R.drawable.heart2, R.drawable.heart2, R.drawable.heart2, R.drawable.heart2, R.drawable.diamond, R.drawable.diamond, R.drawable.diamond, R.drawable.diamond, R.drawable.clubs, R.drawable.clubs, R.drawable.clubs, R.drawable.clubs};//設定圖片形狀圖

        //取得sharedprefence資料
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        accountArgs = getPrefs.getString("list_name", "123");
        selectionArgs[0] = accountArgs;
        name = "list_name = ?";

        handler = new Handler(Looper.getMainLooper());

        //設定配對顏色
        String color[] = {"#FFAAD5", "#FF8EFF", "#BE77FF", "#AAAAFF", "#84C1FF", "#1AFD9C", "#C2FF68", "#FFBB77", "#FFBB77", "#C2FF68", "#1AFD9C", "#84C1FF", "#AAAAFF", "#BE77FF", "#FF8EFF", "#FFAAD5"};

        setImage();
        checkImage();

        //洗牌
        for (int k = 0; k < 100; k++) {
            for (int i = 0; i < 16; i++) {
                int tmp;
                int j = (int) (Math.random() * 16);
                tmp = pos[i];
                pos[i] = pos[j];
                pos[j] = tmp;

            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int index = i * 4 + j;
                block[i][j] = pos[index];
            }
        }


        //綁定元件
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
        title = findViewById(R.id.textView10);
        title_iv = findViewById(R.id.imageView14);


        Drawable originalBackground = button[0].getBackground();


        giveup = findViewById(R.id.giveup);
        myscore = findViewById(R.id.myscore);


        //設定監聽事件
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(family.this).setTitle("確定要放棄嗎?").setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(family.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }).setNegativeButton("取消", null).create().show();

            }
        });

        for (int i = 0; i < 16; i++) {
            int bt = i;

            button[i].setOnClickListener(new View.OnClickListener() {//設定16張圖的監聽事件


                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    handler.removeCallbacksAndMessages(null);//將habdler計數器歸零

                    if (checkbt(bt)) {//判斷按鈕是否已被翻開
                        if (currentPos < 0) {
                            currentPos = pos[bt];
                            curView = button[bt];
                            cvbt = bt;
                            bt1 = bt;
                            button[bt].setImageBitmap(photoArray[pos[bt]]);//設定圖片
                            button[bt].setBackgroundColor(Color.parseColor("#D5D6D6"));//設定背景顏色
                            title_iv.setImageBitmap(photoArray[pos[bt]]);//設定提示圖片

                            for (int i = 0; i < 16; i++) {
                                if (label[currentPos].equals(label[pos[i]]) && !(direction[currentPos].equals(direction[pos[i]]))) {
                                    if(checkbt(i)){
                                        hint.add(pos[i]);

                                    }
                                }
                            }

                            // 創建一個 Random 對象
                            Random random = new Random();
                            // 隨機生成一個介於 0（包括）和 ArrayList 大小（不包括）之間的數字
                            int randomIndex = random.nextInt(hint.size());

                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 4; j++) {
                                    if(hint.get(randomIndex) == block[i][j]){
                                        location = i;
                                        break;
                                    }

                                }
                            }
                            check.add(bt);

                            // 設置8秒後執行的Runnable
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    switch (location) {
                                        case 0:

                                            // 設定圖片大小為 text size
                                            int textSize = (int) title.getTextSize();
                                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(photoArray[pos[bt]], textSize, textSize, true);

                                            // 將 Bitmap 轉換為 Drawable
                                            Drawable drawable = new BitmapDrawable(getResources(), scaledBitmap);

                                            // 用 "跟(圖片)有家人關係的可能在....." 創建 SpannableString
                                            SpannableString spannableString = new SpannableString("另一張有家人關係的圖可能在黑桃的其中一張喔!!!");

                                            // 將 ImageSpan 添加到 SpannableString 中的 "(圖片)" 部分
                                            ImageSpan imageSpan = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
                                            spannableString.setSpan(imageSpan, 1, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE); // 將 "(圖片)" 的位置設定為 1 到 4

                                            // 設置 TextView 的文字
                                            title.setText(spannableString);
                                            hint.clear();
                                            break;
                                        case 1:
                                            title_iv.setImageBitmap(photoArray[pos[bt]]);

                                            title.setText("另一張有家人關係的圖可能在愛心的其中一張喔!!!");
                                            hint.clear();

                                            break;
                                        case 2:
                                            title_iv.setImageBitmap(photoArray[pos[bt]]);

                                            title.setText("另一張有家人關係的圖可能在菱形的其中一四張喔!!!");
                                            hint.clear();

                                            break;
                                        case 3:
                                            title_iv.setImageBitmap(photoArray[pos[bt]]);

                                            title.setText("另一張有家人關係的圖可能在梅花的其中一張喔!!!");
                                            hint.clear();

                                            break;

                                    }

                                }
                            }, 3000); // 3秒後執行


                        } else {
                                if (currentPos == pos[bt]) {
                                button[bt].setImageBitmap(photoArray[pos[bt]]);
                                button[bt].setBackground(originalBackground);
                                check.remove((Object) bt);
                            } else if (label[currentPos].equals(label[pos[bt]]) && !(direction[currentPos].equals(direction[pos[bt]]))) {
                                button[bt].setImageBitmap(photoArray[pos[bt]]);
                                button[bt].setBackgroundColor(Color.parseColor(color[pos[bt]]));
                                curView.setBackgroundColor(Color.parseColor(color[pos[bt]]));
                                countPair++;
                                check.add(bt);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myscore.setText("目前分數 : " + countPair * 10);
                                        title.setText("請將互為親屬關係的家人配對起來吧");
                                        title_iv.setImageDrawable(getDrawable(R.drawable.question));
                                        hint.clear();
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
                                                    Intent intent = new Intent(family.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }).start();
                                }

                            } else {
                                button[bt].setImageBitmap(photoArray[pos[bt]]);
                                button[bt].setBackgroundColor(Color.parseColor("#D5D6D6"));
                                title_iv.setImageDrawable(getDrawable(R.drawable.question));
                                title.setText("請將互為親屬關係的家人配對起來吧");
//                                button[bt].setImageURI(bmp[bt]); // 設置圖像


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
                                                curView.setImageDrawable(getDrawable(bg[cvbt]));
                                                button[bt].setImageDrawable(getDrawable(bg[bt]));
                                                button[bt].setBackground(originalBackground);
                                                curView.setBackground(originalBackground);
                                                Toast.makeText(getApplicationContext(), "Not match", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).start();
                                hint.clear();
                                check.remove((Object) bt1);
                                check.remove((Object) bt);
                            }
                            currentPos = -1;
                        }
                    }
                }
            });
        }

    }

    public void setImage() {
        // 建立SQLiteOpenHelper物件
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        // 构建查询语句
        String[] projection = {"photo1", "photo2", "photo3", "photo4"};
        String[] projection2 = {"photo5", "photo6", "photo7", "photo8"};
        String[] projection3 = {"photo9", "photo10", "photo11", "photo12"};
        String[] projection4 = {"photo13", "photo14"};
        String[] projection5 = {"photo15", "photo16"};
        String[] projection6 = {"photo1", "photo2", "photo3", "photo4", "photo5", "photo6", "photo7", "photo8"};
        String[] projection7 = {"photo9", "photo10", "photo11", "photo12", "photo13", "photo14", "photo15", "photo16"};

        String sortOrder = ""; // 可以指定排序顺序

        // 执行查询
        Cursor c = db.query("family", projection, name, selectionArgs, null, null, sortOrder);
        Cursor c2 = db.query("family", projection2, name, selectionArgs, null, null, sortOrder);
        Cursor c3 = db.query("family", projection3, name, selectionArgs, null, null, sortOrder);
        Cursor c4 = db.query("family", projection4, name, selectionArgs, null, null, sortOrder);
        Cursor c5 = db.query("family", projection5, name, selectionArgs, null, null, sortOrder);
        Cursor c6 = db.query("label", projection6, name, selectionArgs, null, null, sortOrder);
        Cursor c7 = db.query("label", projection7, name, selectionArgs, null, null, sortOrder);
        Cursor c8 = db.query("direction", projection6, name, selectionArgs, null, null, sortOrder);
        Cursor c9 = db.query("direction", projection7, name, selectionArgs, null, null, sortOrder);


        while (c.moveToNext()) {
            for (int i = 0; i < 4; i++) {
                int columnIndex = c.getColumnIndex(projection[i]);
                byte[] byteArray = c.getBlob(columnIndex);

                if (byteArray != null) {
                    // 將字節數組轉換為 Bitmap
                    photoArray[i] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    lPhoto.add(photoArray[i]);
                }
            }
        }

        while (c2.moveToNext()) {
            for (int i = 0; i < 4; i++) {

                int columnIndex = c2.getColumnIndex(projection2[i]);
                byte[] byteArray = c2.getBlob(columnIndex);

                if (byteArray != null) {
                    // 將字節數組轉換為 Bitmap
                    photoArray[i + 4] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    lPhoto.add(photoArray[i + 4]);
                }

            }
        }
        while (c3.moveToNext()) {
            for (int i = 0; i < 4; i++) {

                int columnIndex = c3.getColumnIndex(projection3[i]);
                byte[] byteArray = c3.getBlob(columnIndex);

                if (byteArray != null) {
                    // 將字節數組轉換為 Bitmap
                    photoArray[i + 8] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    rPhoto.add(photoArray[i + 8]);
                }
            }

        }

        while (c4.moveToNext()) {
            for (int i = 0; i < 2; i++) {
                int columnIndex = c4.getColumnIndex(projection4[i]);
                byte[] byteArray = c4.getBlob(columnIndex);

                if (byteArray != null) {
                    // 將字節數組轉換為 Bitmap
                    photoArray[i + 12] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    rPhoto.add(photoArray[i + 12]);
                }
            }

        }
        while (c5.moveToNext()) {
            for (int i = 0; i < 2; i++) {
                int columnIndex = c5.getColumnIndex(projection5[i]);
                byte[] byteArray = c5.getBlob(columnIndex);

                if (byteArray != null) {
                    // 將字節數組轉換為 Bitmap
                    photoArray[i + 14] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    rPhoto.add(photoArray[i + 14]);
                }
            }

        }
        while (c6.moveToNext()) {
            for (int i = 0; i < 8; i++) {
                String l = c6.getString(i);
                if (l != null) {
                    lLabel[i] = l;
                    lLabelArray.add(l);
                }
            }
        }
        while (c7.moveToNext()) {
            for (int i = 0; i < 8; i++) {
                String l = c7.getString(i);
                if (l != null) {
                    rLabel[i] = l;
                    rLabelArray.add(l);
                }

            }
        }
        while (c8.moveToNext()) {
            for (int i = 0; i < 8; i++) {
                String d = c8.getString(i);
                if (d != null) {
                    lDirection[i] = d;
                    lDirectionArray.add(d);
                }

            }
        }
        while (c9.moveToNext()) {
            for (int i = 0; i < 8; i++) {
                String d = c9.getString(i);
                if (d != null) {
                    rDirection[i] = d;
                    rDirectionArray.add(d);
                }

            }
        }

        c.close();
        c2.close();
        c3.close();
        c4.close();
        c5.close();
        c6.close();
        c7.close();
        c8.close();
        c9.close();
    }

    private void checkImage() {

        for (int i = 0; i < 8; i++) {
            if (photoArray[i] == null || photoArray[i].isRecycled()) {
                // 創建一個 Random 對象
                Random random = new Random();
                // 隨機生成一個介於 0（包括）和 ArrayList 大小（不包括）之間的數字
                int randomIndex = random.nextInt(lPhoto.size());

                photoArray[i] = lPhoto.get(randomIndex);
                photoArray[i + 8] = rPhoto.get(randomIndex);
                lLabel[i] = lLabelArray.get(randomIndex);
                rLabel[i] = rLabelArray.get(randomIndex);
                lDirection[i] = lDirectionArray.get(randomIndex);
                rDirection[i] = rDirectionArray.get(randomIndex);


            }
        }

        System.arraycopy(lLabel, 0, label, 0, 8);
        System.arraycopy(rLabel, 0, label, 8, 8);
        System.arraycopy(lDirection, 0, direction, 0, 8);
        System.arraycopy(rDirection, 0, direction, 8, 8);

    }

}
