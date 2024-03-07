package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Remember_choise_picture extends AppCompatActivity {
    Button choise;
    ListView listView;
    private SQLiteDatabase db;
    Bitmap bitmap[];
    public static String[] nameArray ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_choise_picture);

        //綁定元件
        choise = findViewById(R.id.button);
        listView = findViewById(R.id.listview);

        SqlGetData();
        setListView();

        choise.setOnClickListener(new View.OnClickListener() {//監聽事件(跳轉至RememberUploadImage)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Remember_choise_picture.this, RememberUploadImage.class);
                startActivity(intent);
            }
        });


    }

    public void SqlGetData() {

        // 建立SQLiteOpenHelper物件
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        // 建構查询语句
        String[] projection = {
                "list_name",
                "photo1"
        };

        String selection = "";
        String[] selectionArgs = new String[0]; 
        String sortOrder = ""; 

        //建構查詢語句
        Cursor c = db.query(
                "family",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        int count = c.getCount();//取得資料數量
        nameArray = new String[count];
        bitmap = new Bitmap[count];

        int i = 0;
        while (c.moveToNext()) {//取得資料
            int columnIndex = c.getColumnIndex("photo1");
            byte[] byteArray = c.getBlob(columnIndex);
            nameArray[i] = c.getString(0);
            if (byteArray != null) {
                // 將字節數組轉換為 Bitmap
                bitmap[i] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
            i++;

        }

        c.close();
    }

    public void setListView() {
    // 創建一個自定義的 Adapter 對象，並將它設置到 ListView 上
    Adapter adapter = new Adapter(this, nameArray, bitmap);
    listView.setAdapter(adapter); // listView 是一個 ListView 的實例，設置適配器，將資料和顯示方式綁定到 ListView 上
}

public class Adapter extends BaseAdapter {
    private Context context; // 上下文對象，用於訪問應用程式資源和類別
    private String[] nameArray; // 名字數組
    private Bitmap[] bitmap; // 位圖數組，用於顯示圖片


    public Adapter(Context context, String[] nameArray, Bitmap[] bitmap) {
        // Adapter 的建構子，用於初始化 Adapter 中的成員變量
        this.context = context;
        this.nameArray = nameArray;
        this.bitmap = bitmap;
    }

    @Override
    public int getCount() {
        // 返回數據集的項目數量
        return nameArray.length;
    }

    @Override
    public Object getItem(int position) {
        // 返回指定位置的數據項目
        return nameArray[position];
    }

    @Override
    public long getItemId(int position) {
        // 返回指定位置的項目 ID，通常設置為該位置
        return position;
    }
}

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.picture_list, null);
            }

            ImageView photoList = convertView.findViewById(R.id.photo);
            ImageView all = convertView.findViewById(R.id.all);
            TextView nameList = convertView.findViewById(R.id.list_name);

            photoList.setImageBitmap(bitmap[position]);
            nameList.setText("--"+nameArray[position]+"--");

            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences getPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    // 取得Editor
                    SharedPreferences.Editor editor = getPrefs.edit();
                    // 將version的值設為1
                    editor.putString("list_name", nameArray[position]);
                    editor.apply();
                    Intent intent = new Intent(Remember_choise_picture.this, family.class);
                    startActivity(intent);
                    finish();
                }
            });

            return convertView;
        }
    }

}
