package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RememberChoice extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_choice);

        Button picture, family,animal;

        picture = findViewById(R.id.picture);
        family = findViewById(R.id.family);
        animal = findViewById(R.id.animal);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RememberChoice.this, RememberPicture.class);
                startActivity(intent);
                finish();
            }
        });

        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RememberChoice.this, Remember_choise_picture.class);
                startActivity(intent);
                finish();
            }
        });

        animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RememberChoice.this, RememberAnimal.class);
                startActivity(intent);
                finish();
            }
        });


    }
}