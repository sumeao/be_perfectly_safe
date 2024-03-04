package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eat_list extends AppCompatActivity {
    ListView listView;
    public static String[] food_name;
    public static int[] food_photo = {
            R.drawable.menu01,
            R.drawable.menu02,
            R.drawable.menu03,
            R.drawable.menu04,
            R.drawable.menu05,
            R.drawable.menu06,
            R.drawable.menu07,
            R.drawable.menu08,
            R.drawable.menu09,
            R.drawable.menu10,
            R.drawable.menu11,
            R.drawable.menu12,
            R.drawable.menu13,
            R.drawable.menu14,
    };
    private SQLiteDatabase db;
    TextView name_list;
    ImageView photo_list;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_eat_list);
        listView = findViewById(R.id.Eat);

        sqlGetData();
        setListView();

    }

    public void sqlGetData() {

        // 建立SQLiteOpenHelper物件
        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        // 构建查询语句
        String[] projection = {
                "name"
        };

        String selection = ""; // 可以使用适当的条件
        String[] selectionArgs = new String[0]; // 如果有条件，提供相应的参数
        String sortOrder = ""; // 可以指定排序顺序
        String[] selectMod = {"food"};

        // 执行查询
        Cursor c = db.query(
                selectMod[0],
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        food_name = new String[c.getCount()];

        int i = 0;
        while (c.moveToNext()) {
            food_name[i] = c.getString(0);
            i++;
        }
        // 关闭游标和数据库连接
        c.close();

    }

    public void ListView_Customer(Context context) {
        listView.setAdapter(null);
        SimpleAdapter adapter = new SimpleAdapter(context, getData(), R.layout.recipe_list, new String[]{"name"}, new int[]{R.id.ExName}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    public List getData() {
        List list = new ArrayList();
        Map map;
        for (int i = 0; i < food_name.length; i++) {
            map = new HashMap();
            map.put("name", food_name[i]);
            map.put("photo",food_photo[i]);
            list.add(map);
        }
        return list;
    }

    public void setListView() {

        ListView_Customer(getApplicationContext());


        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return food_name.length; // 返回numberArray的长度
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
                View ReLayout = View.inflate(Eat_list.this, R.layout.recipe_list, null);

                name_list = ReLayout.findViewById(R.id.ExName);
                photo_list = ReLayout.findViewById(R.id.photo);
                ImageButton video = ReLayout.findViewById(R.id.BtVideo);

                if (food_name[i] != null) {
                    name_list.setText(food_name[i]);
                    photo_list.setImageResource(food_photo[i]);
                }

                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Eat_list.this, eat_ingredients.class);
                        startActivity(intent);
                        finish();
                    }
                });

                return ReLayout;
            }
        };
        listView.setAdapter(baseAdapter);

    }

}