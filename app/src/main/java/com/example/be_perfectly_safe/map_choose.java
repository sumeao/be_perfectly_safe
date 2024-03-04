package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class map_choose extends AppCompatActivity {
    Button button1, button2, specialres, speciallog, show, button378;

    @SuppressLint({"NewApi", "MissingInflatedId"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_choose);

//        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
//        specialres = findViewById(R.id.specialres);
        speciallog = findViewById(R.id.speciallog);
        button378 = findViewById(R.id.button378);
//        show = findViewById(R.id.show);

//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(map_choose.this, map.class);
//                startActivity(intent);
//            }
//        });

        button378.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(map_choose.this, Mod.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(map_choose.this, caregiverlocation.class);
                startActivity(intent);
            }
        });
//        specialres.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(map_choose.this, map_registration.class);
//                startActivity(intent);
//            }
//        });
        speciallog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(map_choose.this, map_login.class);
                startActivity(intent);
            }
        });

//        show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(choose.this, showalldata.class);
//                startActivity(intent);
//            }
//        });



    }
}