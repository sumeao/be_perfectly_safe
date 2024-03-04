package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class map_login extends AppCompatActivity {

    TextView textView_RegisterNow;
    EditText log1, log2;

    Button logsubmit;

    private String logg1, logg2;

    private TextView textView_Error;

    ProgressBar progressBar2;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_login);

        logsubmit = findViewById(R.id.logsubmit);
        logsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(map_login.this, showalldata.class);
                startActivity(intent);
            }
        });
    }
}