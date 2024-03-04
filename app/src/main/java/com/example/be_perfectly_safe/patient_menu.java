package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class patient_menu extends AppCompatActivity {


    private SQLiteDatabase db;
    Bitmap bitmap;
    public static String[] numberArray, nameArray;
    public static Byte[] photoArray;
    TextView numberList, nameList;
    ImageView photoList;
    private static ListView listView;
    Boolean noPhoto = false;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_menu);
        listView = findViewById(R.id.listview);
        SqlGetData();
        ListView_Customer(getApplicationContext());


        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return numberArray.length; // 返回numberArray的长度
            }

            @Override
            public Object getItem(int i) {
                return i;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup container) {
                View ReLayout = View.inflate(patient_menu.this, R.layout.patient_list, null);

                nameList = ReLayout.findViewById(R.id.name);
                numberList = ReLayout.findViewById(R.id.number);
                photoList = ReLayout.findViewById(R.id.photo);
                ImageView imageView = ReLayout.findViewById(R.id.all);

                if (nameArray[i] != null) {
                    nameList.setText(nameArray[i]);
                }
                if (numberArray[i] != null) {
                    numberList.setText(numberArray[i]);
                }


//                photoList.setImageBitmap(photoArray[i]);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String choiseName = nameArray[i];

                        SharedPreferences getPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());
                        // 取得Editor
                        SharedPreferences.Editor editor = getPrefs.edit();
                        // 將version的值設為1
                        editor.putString("name", choiseName);
                        editor.apply();

                        Intent intent = new Intent(patient_menu.this,patient_update.class);
                        startActivity(intent);
                    }
                });

                return ReLayout;
            }
        };
        listView.setAdapter(baseAdapter);
    }

    public void SqlGetData() {

        // 建立SQLiteOpenHelper物件
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        // 构建查询语句
        String[] projection = {
                "name",
                "photo"
        };

        String selection = ""; // 可以使用适当的条件
        String[] selectionArgs = new String[0]; // 如果有条件，提供相应的参数
        String sortOrder = ""; // 可以指定排序顺序

        // 执行查询
        Cursor c = db.query(
                "patient",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );



        numberArray = new String[c.getCount()];
        nameArray = new String[c.getCount()];
        byte[][] photoArray = new byte[c.getCount()][];

        int i = 0;
        while (c.moveToNext()) {
            numberArray[i] = c.getString(1);
            nameArray[i] = c.getString(0);

            byte[] blobData = c.getBlob(c.getColumnIndexOrThrow("photo"));
            if (blobData != null) {
                photoArray[i] = blobData.clone();
                bitmap = BitmapFactory.decodeByteArray(photoArray[i], 0, photoArray[i].length);
            }else{
                System.out.println("----------------------------------------");
                System.out.println(nameArray[i]);
                noPhoto = true;
            }

            i++;
        }
        // 关闭游标和数据库连接
        c.close();
    }

    public void ListView_Customer(Context context) {
        listView.setAdapter(null);
        SimpleAdapter adapter = new SimpleAdapter(context, getData(), R.layout.patient_list, new String[]{"number", "photo", "name"}, new int[]{R.id.number, R.id.photo, R.id.name}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                ImageView photoImageView = view.findViewById(R.id.photo);
//                byte[] photoData = (byte[]) ((Map<?, ?>) getItem(position)).get("photo");
//                Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
//                photoImageView.setImageBitmap(photoBitmap);
                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    public List getData() {
        List list = new ArrayList();
        Map map;
        for (int i = 0; i < numberArray.length; i++) {
            map = new HashMap();
            map.put("number", i+1);
            if(noPhoto){
                map.put("photo", R.drawable.head_bg);
            }else{
                map.put("photo", photoArray[i]);
            }

            map.put("name", nameArray[i]);
            list.add(map);
        }
        return list;
    }


}