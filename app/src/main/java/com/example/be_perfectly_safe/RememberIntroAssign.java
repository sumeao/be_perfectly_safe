package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class RememberIntroAssign extends AppCompatActivity {
    ImageButton BtnRem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_intro_assign);

        BtnRem = findViewById(R.id.BtnRememberAssign);

        BtnRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RememberIntroAssign.this,RememberChoice.class);
                startActivity(intent);
                finish();
            }
        });
    }
}