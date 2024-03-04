package com.example.be_perfectly_safe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class registration extends Activity {

    private EditText edit_Account, edit_Password, edit_Name, edit_Email, edit_Phone;
    private String edit_Account_Text, edit_Password_Text, edit_Email_Text, edit_Phone_Text, edit_name_Text;
    private Button btn_Insert;
    private SqlDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    TextView loginNow;
    Integer login = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //綁定元件
        edit_Account = findViewById(R.id.account);
        edit_Password = findViewById(R.id.password);
        edit_Email = findViewById(R.id.mail);
        edit_Phone = findViewById(R.id.phone);
        edit_Name = findViewById(R.id.name);
        btn_Insert = findViewById(R.id.submit);
        loginNow = findViewById(R.id.loginNow);

        btn_Insert.setOnClickListener(new View.OnClickListener() {//設定按鈕監聽事件
            @Override
            public void onClick(View view) {
                edit_name_Text = edit_Name.getText().toString();//取得textview上的資料並轉成字串
                edit_Account_Text = edit_Account.getText().toString();
                edit_Password_Text = edit_Password.getText().toString();
                edit_Email_Text = edit_Email.getText().toString();
                edit_Phone_Text = edit_Phone.getText().toString();

                if (edit_name_Text != null && !edit_name_Text.equals("") &&
                        edit_Account_Text != null && !edit_Account_Text.equals("") &&
                        edit_Password_Text != null && !edit_Password_Text.equals("") &&
                        edit_Phone_Text != null && !edit_Phone_Text.equals("") &&
                        edit_Email_Text != null && !edit_Email_Text.equals("")
                ) {//如果每一欄都有輸入資料或是不是空的
                    if (SqlAccountCheck(getApplicationContext(), edit_Account_Text) > 0) {//查詢帳號是否以在資料庫裡
                        Toast.makeText(view.getContext(), "此帳號已存在!!", Toast.LENGTH_LONG).show();//顯示"此帳號已存在!!"的提示字
                    } else {//新增帳號資料
                        dbHelper = new SqlDataBaseHelper(getApplicationContext());
                        db = dbHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", edit_name_Text);
                        contentValues.put("account", edit_Account_Text);
                        contentValues.put("password", edit_Password_Text);
                        contentValues.put("email", edit_Email_Text);
                        contentValues.put("phone", edit_Phone_Text);
                        contentValues.put("login", login);//將資料放入對應的欄位
                        long rowId = db.insert("caregiver", null, contentValues);//執行寫入
                        if (rowId != -1) {
                            // 數據成功寫入
                            Toast.makeText(view.getContext(), "帳號新增成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(registration.this, login.class);
                            startActivity(intent);
                        } else {
                            // 數據寫入失敗
                            Toast.makeText(view.getContext(), "帳號新增失敗", Toast.LENGTH_LONG).show();
                        }
                        System.out.println("-----------------------------------");
                        System.out.println(edit_Account_Text);

                        db.close();
                    }
                } else {
                    Toast.makeText(view.getContext(), "您還有尚未填寫之欄位", Toast.LENGTH_LONG).show();
                }
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {//跳轉至登入頁面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public Integer SqlAccountCheck(Context context, String newAccount) {//檢查帳號是否已被註冊過
        String DataBaseTable = "caregiver";//資料表名稱
        dbHelper = new SqlDataBaseHelper(context);
        db = dbHelper.getReadableDatabase();
        String selection = "account=?";//查詢欄位
        String[] selectionArgs = {newAccount};
        Cursor cursor = db.query(DataBaseTable, null, selection, selectionArgs, null, null, null);//執行查詢
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

}