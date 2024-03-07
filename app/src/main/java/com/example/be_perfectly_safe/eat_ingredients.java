package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class eat_ingredients extends AppCompatActivity {

    TextView ingredients, ExName;
    ImageButton imgbt;
    ImageView photo;
    ArrayList<Integer> RecipePicture = new ArrayList<Integer>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_ingredients);

        //綁定元件
        ingredients = findViewById(R.id.ingredients);
        imgbt = findViewById(R.id.BtVideo);
        ExName = findViewById(R.id.ExName);
        photo = findViewById(R.id.photo);

        //將圖片放到RecipePicture的arraylist裡
        RecipePicture.add(R.drawable.menu01);
        RecipePicture.add(R.drawable.menu02);
        RecipePicture.add(R.drawable.menu03);
        RecipePicture.add(R.drawable.menu04);
        RecipePicture.add(R.drawable.menu05);
        RecipePicture.add(R.drawable.menu06);
        RecipePicture.add(R.drawable.menu07);
        RecipePicture.add(R.drawable.menu08);
        RecipePicture.add(R.drawable.menu09);
        RecipePicture.add(R.drawable.menu10);
        RecipePicture.add(R.drawable.menu11);
        RecipePicture.add(R.drawable.menu12);
        RecipePicture.add(R.drawable.menu13);
        RecipePicture.add(R.drawable.menu14);

        //設定菜單文字
        String Ingredients;
        Ingredients =
                "1. 大白菜1/2顆對切洗淨瀝乾，切大塊" + "\n" +
                        "2. 雕魚肉3片" + "\n" +
                        "3. 花椒粒1小匙、薑末1小匙" + "\n" +
                        "4. 蒜頭2瓣洗淨去皮切末、香菜1棵去根洗淨切末" + "\n" +
                        "5. 鮮辣椒1條洗淨切片、青蔥1根洗淨切末" + "\n" +
                        "6. 油3大匙、米酒1大匙" + "\n"
        ;

        //取得sharedprefence的資料
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        //取得名為name的資料，若沒有資料則為null
        String name = getPrefs.getString("name", "null");     
        
        //取得名為RecipePicture的資料，若沒有資料則為null
        String img = getPrefs.getString("RecipePicture", "null");

        //設定圖片及文字
        ExName.setText(name);
        photo.setImageResource(RecipePicture.get(Integer.parseInt(img)));
        ingredients.setText(Ingredients);

        //設定監聽事件(跳轉至eat_video)
        imgbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(eat_ingredients.this, eat_video.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
