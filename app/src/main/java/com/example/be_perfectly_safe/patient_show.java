package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class patient_show extends AppCompatActivity {

    ImageView sethead;
    Button save;
    private Uri selectedImageUri;
    private SQLiteDatabase db;
    Uri imageuri;
    private static final int REQUEST_IMAGE_PICKER = 1;
    private static String patientName;
    private static String patientSex;
    private static String patientBirthday;
    private static String patientLevel;
    private static String patientCare;
    private Uri croppedImageUri; // 全局變量，用於保存裁剪後的圖片路徑


    EditText EditpatientName, EditpatientSex, EditpatientBirthday, EditpatientLevel, EditpatientCare;
    private byte[] imageData;
    Bitmap bitmap;


    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_update);

        EditpatientName = findViewById(R.id.editTextTextPersonName);
        EditpatientSex = findViewById(R.id.editTextTextPersonNumber);
        EditpatientLevel = findViewById(R.id.editTextTextPersonAddress);
        EditpatientCare = findViewById(R.id.editTextTextPersonDisease1);
        EditpatientBirthday = findViewById(R.id.patientBirthday);
        sethead = findViewById(R.id.sethead);
        save = findViewById(R.id.save);

        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        // 取得SharedPreference
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String nameArgs = getPrefs.getString("name", "null");
        String[] selectionArgs = nameArgs.split(",");
        String selection = getPrefs.getString("name=?", "name=?");

        // 构建查询语句
        String[] projection = {
                "name",
                "level",
                "sex",
                "birthday",
                "caregiver",
                "photo"
        };

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

        // 遍历查询结果
        if (c.moveToFirst()) {
            String patientName = c.getString(c.getColumnIndexOrThrow("name"));
            String patientSex = c.getString(c.getColumnIndexOrThrow("sex"));
            String patientBirthday = c.getString(c.getColumnIndexOrThrow("birthday"));
            String patientLevel = c.getString(c.getColumnIndexOrThrow("level"));
            String patientcare = c.getString(c.getColumnIndexOrThrow("caregiver"));
            imageData = c.getBlob(c.getColumnIndexOrThrow("photo"));

//            System.out.println("=================================");
//            System.out.println(imageData);

            if (patientLevel != null || patientName != null || patientcare != null || patientSex != null || patientBirthday != null) {
                if (imageData != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    sethead.setImageBitmap(bitmap);
                    EditpatientLevel.setText(patientLevel);
                    EditpatientName.setText(patientName);
                    EditpatientCare.setText(patientcare);
                    EditpatientSex.setText(patientSex);
                    EditpatientBirthday.setText(patientBirthday);
                } else {
                    sethead.setImageDrawable(getDrawable(R.drawable.head_bg));
                    EditpatientLevel.setText(patientLevel);
                    EditpatientName.setText(patientName);
                    EditpatientCare.setText(patientcare);
                    EditpatientSex.setText(patientSex);
                    EditpatientBirthday.setText(patientBirthday);
                }
            }
        }
        // 关闭游标和数据库连接
        c.close();
    }

}
