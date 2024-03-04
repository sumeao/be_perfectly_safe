package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class patient_set extends AppCompatActivity {

    Button new_patient,update_patient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_set);

        new_patient = findViewById(R.id.new_patient);
        update_patient = findViewById(R.id.update_patient);

        new_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(patient_set.this, new_patient.class);
                startActivity(intent);
            }
        });

        update_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(patient_set.this, patient_menu.class);
                startActivity(intent);
            }
        });
    }
}