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

        choise = findViewById(R.id.button);
        listView = findViewById(R.id.listview);

        SqlGetData();
        setListView();

        choise.setOnClickListener(new View.OnClickListener() {
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

        // 构建查询语句
        String[] projection = {
                "list_name",
                "photo1"
        };

        String selection = ""; // 可以使用适当的条件
        String[] selectionArgs = new String[0]; // 如果有条件，提供相应的参数
        String sortOrder = ""; // 可以指定排序顺序

        // 执行查询
        Cursor c = db.query(
                "family",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        int count = c.getCount();
        nameArray = new String[count];
        bitmap = new Bitmap[count];

        int i = 0;
        while (c.moveToNext()) {
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
        Adapter adapter = new Adapter(this, nameArray, bitmap);
        listView.setAdapter(adapter);
    }

    public class Adapter extends BaseAdapter {
        private Context context;
        private String[] nameArray;
        private Bitmap[] bitmap;


        public Adapter(Context context, String[] nameArray, Bitmap[] bitmap) {
            this.context = context;
            this.nameArray = nameArray;
            this.bitmap = bitmap;
        }

        @Override
        public int getCount() {
            return nameArray.length;
        }

        @Override
        public Object getItem(int position) {
            return nameArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
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