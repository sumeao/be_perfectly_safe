//package com.example.be_perfectly_safe;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//
//public class chatGPT_introduction extends AppCompatActivity {
//
//    ImageButton go;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_gpt_introduction);
//
//        go = findViewById(R.id.go);
//
//        go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(chatGPT_introduction.this, chatGPT.class);
//                startActivity(intent);
//            }
//        });
//    }
//}