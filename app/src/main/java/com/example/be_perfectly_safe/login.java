package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

    private SQLiteDatabase db1, db2;
    private String login_account, login_password;
    EditText edit_account, edit_password;
    Button submit;
    TextView reregisterNow;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db1 = dbHelper.getWritableDatabase();//取得寫入資料庫權限
        db2 = dbHelper.getReadableDatabase();//取得寫入資料庫權限

        //綁定元件
        edit_account = findViewById(R.id.account);
        edit_password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        reregisterNow = findViewById(R.id.registerNow);

        //按鈕監聽事件
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取得textview資料
                login_account = edit_account.getText().toString();
                login_password = edit_password.getText().toString();


                String selection = "account = ?";
                String[] selectionArgs = new String[1];
                selectionArgs[0] = login_account;


                if (checkCredentials(login_account, login_password)) {//執行登入確認動作 checkCredentials()，把帳號密碼帶入判斷
                    ContentValues values = new ContentValues();
                    values.put("login", 1);

                    int rowsAffected = db1.update("caregiver", values, selection, selectionArgs);

                    if (rowsAffected > 0) {
                        // 更新成功
                        Toast.makeText(view.getContext(), "登錄成功", Toast.LENGTH_SHORT).show();
                        db1.close();

                        // 取得SharedPreference
                        SharedPreferences getPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());
                        // 取得Editor
                        SharedPreferences.Editor editor = getPrefs.edit();
                        // 將version的值設為1
                        editor.putString("account", selection);
                        editor.putString("accountArg", selectionArgs[0]);

                        // 套用變更，一定要apply才會生效
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // 更新失败
                        Toast.makeText(view.getContext(), "登錄失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "帳號或密碼有誤，請重新輸入!!!", Toast.LENGTH_LONG).show();
                }

            }
        });


        reregisterNow.setOnClickListener(new View.OnClickListener() {//跳轉到註冊頁面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), registration.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean checkCredentials(String credential, String password) {
        String selection = "(account=? OR phone=? OR email=?)";//查詢的資料，可以是帳號或電話或是電子信箱
        String[] selectionArgs = {credential, credential, credential};//因為三種登入方式，所以放三個帳號做判斷  利:{account = ? ==> account = credential} || {phone = ? ==> phone = credential} || {email = ? ==> email = credential}

        Cursor cursor = db2.query("caregiver", null, selection, selectionArgs, null, null, null);//開始查詢 ==> 找caregiver這一張資料表裡 account=? OR phone=? OR email=? 是不是等於 credential
        boolean exists = false;

        while (cursor.moveToNext()) {//因為裡面會有很多組帳號，所以每找完一筆，他會移到下一筆，直到最後從頭開始，當他從頭開始時，結束回圈
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));//用 storedPassword 存從資料庫拿到密碼的質
            if (password.equals(storedPassword)) {//帶段輸入的密碼跟資料庫的密碼是否一樣
                exists = true;
                break;
            }
        }

        cursor.close();
        return exists;
    }

}
