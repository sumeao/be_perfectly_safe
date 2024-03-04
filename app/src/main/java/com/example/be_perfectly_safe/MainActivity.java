package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String IPAddress = "172.20.10.7";//紀錄ip位置，供其他地方使用

    private static SQLiteDatabase db;//宣告SQLiteDatabase
    private SqlDataBaseHelper sqlDataBaseHelper;//宣告SqlDataBaseHelper
    private byte[] imageData, imageData2;//創建兩個byte陣列
    Bitmap bitmap;


    ImageButton BtnEx, BtnGame, BtnEat, BtnScale, BtnGPS, BtnChatbot;
    ImageView BtnCare, BtnPatient,Btnlogout;

    TextView caregiverName, patientName;

    @SuppressLint("SetTextI18n")
    public boolean SqlAccountCheck(String account, String selectionArgs) {

        sqlDataBaseHelper = new SqlDataBaseHelper(getApplicationContext());
        db = sqlDataBaseHelper.getReadableDatabase(); // 開啟資料庫

        String test = "";
        String[] test2 = {selectionArgs, "1"};
        test = account + " AND login = ?";//查詢是否已經登陸的語句

        Cursor c = db.query("caregiver", null, test, test2, null, null, null);//開始查詢
        c.moveToFirst();


        if (c.getCount() == 0) {
            return true;

        } else {
            return false;
        }
    }


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //綁定元件
        Btnlogout = findViewById(R.id.logout);
        BtnGame = findViewById(R.id.BtnGame);
        BtnEat = findViewById(R.id.BtnEat);
        BtnScale = findViewById(R.id.BtnGame2);
        BtnPatient = findViewById(R.id.patient);
        BtnGPS = findViewById(R.id.BtnGPS);
        BtnChatbot = findViewById(R.id.BtnChatbot);


        // 取得SharedPreference
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putString("IP", IPAddress);//創建一個名為IP，內容是IPAddress的字串

       //啟用editor
        editor.apply();

        String accountArgs = getPrefs.getString("accountArg", "null");
        String[] selectionArgs = accountArgs.split(",");
        String account = getPrefs.getString("account", "account");//查詢是否已經創建過帳號的語句

        if (accountArgs == "null") {//若沒有則跳轉至登入頁面
            Intent intent = new Intent(MainActivity.this, InitialPage.class);
            startActivity(intent);
            finish();
        } else {
            if (SqlAccountCheck(account, accountArgs)) {//若已經有帳號存在，再檢查是否已經登入，若沒登入過，則跳轉至登入頁面
                Intent intent = new Intent(MainActivity.this, InitialPage.class);
                startActivity(intent);
                finish();
            }
        }

        // 建構查询语句
        String[] projection = {
                "photo"
        };

        String[] projection2 = {
                "name",
                "photo"
        };

        String selection = "";
        String[] selectionArgs2 = new String[0];
        String sortOrder = "";

        // 執行查询
        Cursor d = db.query(
                "caregiver",
                projection,
                account,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Cursor e = db.query(
                "patient",
                projection2,
                selection,
                selectionArgs2,
                null,
                null,
                sortOrder
        );

        // 遍歷查询结果
        if (d.moveToFirst()) {
            imageData = d.getBlob(d.getColumnIndexOrThrow("photo"));//取得"photo"的查詢結果

            if (imageData != null) {
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                BtnCare.setImageBitmap(bitmap);
            }

            if (e.moveToFirst()) {
                imageData2 = e.getBlob(d.getColumnIndexOrThrow("photo"));
                if (imageData2 != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageData2, 0, imageData2.length);
                    BtnPatient.setImageBitmap(bitmap);
                }

            }

            Btnlogout.setOnClickListener(new View.OnClickListener() {//設定登出按鈕監聽事件
                @Override
                public void onClick(View view) {

                    ContentValues values = new ContentValues();
                    values.put("login", 0);//將login的資料改為0

                    int rowsAffected = db.update("caregiver", values, account, selectionArgs);//執行更新

                    if (rowsAffected > 0) {//如果更新成功，顯示登出成功的提示字，關閉資料庫，跳轉至登入頁面
                        Toast.makeText(view.getContext(), "登出成功", Toast.LENGTH_SHORT).show();
                        db.close();
                        Intent intent = new Intent(MainActivity.this, login.class);
                        startActivity(intent);
                        finish();
                    } else {// 更新失败，顯示登出失敗提示字
                        Toast.makeText(view.getContext(), "登出失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            BtnEx.setOnClickListener(new View.OnClickListener() {//跳轉至運動頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Exercise.class);
                    startActivity(intent);
                }
            });
            BtnEat.setOnClickListener(new View.OnClickListener() {//跳轉至飲食頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Eat_list.class);
                    startActivity(intent);
                }
            });
            BtnScale.setOnClickListener(new View.OnClickListener() {//跳轉至檢測頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, scale_choise.class);
                    startActivity(intent);
                }
            });

            BtnGame.setOnClickListener(new View.OnClickListener() {//跳轉至社交頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Game.class);
                    startActivity(intent);
                }
            });
            BtnGPS.setOnClickListener(new View.OnClickListener() {//跳轉至安全與提醒頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, map_choose.class);
                    startActivity(intent);

                }
            });

            BtnChatbot.setOnClickListener(new View.OnClickListener() {//跳轉至機器人頁面
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, chatGPT.class);
                    startActivity(intent);
                }
            });

        }
    }
}