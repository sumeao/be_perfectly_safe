package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class InitialPage extends AppCompatActivity {

    ImageView image;
    Button login,register;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        //綁定元件
        image = findViewById(R.id.imageView38);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        //設定圖片漸顯效果
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1000); //單位毫秒
        image.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
        image.startAnimation(animation);
        login.startAnimation(animation);
        register.startAnimation(animation);

        login.setOnClickListener(new View.OnClickListener() {//跳轉至登入頁面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitialPage.this, login.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {//跳轉至註冊頁面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitialPage.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

    }
}