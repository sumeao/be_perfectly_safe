package com.example.be_perfectly_safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RememberUploadImage extends AppCompatActivity {
    int bt = Integer.MAX_VALUE;
    private byte[][] imageByteArray = new byte[16][];
    Boolean isempty = true;
    String photoname;

    private SQLiteDatabase db;
    private SqlDataBaseHelper dbHelper;
    ImageButton button[] = new ImageButton[16];
    final String[] btArray = {"photo1", "photo2", "photo3", "photo4", "photo5", "photo6", "photo7", "photo8", "photo9", "photo10", "photo11", "photo12", "photo13", "photo14", "photo15", "photo16"};
    final String[] btNum = {"1", "2", "3", "4", "5", "6", "7", "8", "1", "2", "3", "4", "5", "6", "7", "8"};//設定編號
    final String[] Dir = {"l", "l", "l", "l", "l", "l", "l", "l", "r", "r", "r", "r", "r", "r", "r", "r"};//設定圖片的左右邊
    private int currentButtonIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_upload_image);


        Button confirm;

        //綁定元件
        button[0] = findViewById(R.id.one_1);
        button[8] = findViewById(R.id.one_2);
        button[1] = findViewById(R.id.two_1);
        button[9] = findViewById(R.id.two_2);
        button[2] = findViewById(R.id.three_1);
        button[10] = findViewById(R.id.three_2);
        button[3] = findViewById(R.id.four_1);
        button[11] = findViewById(R.id.four_2);
        button[4] = findViewById(R.id.five_1);
        button[12] = findViewById(R.id.five_2);
        button[5] = findViewById(R.id.six_1);
        button[13] = findViewById(R.id.six_2);
        button[6] = findViewById(R.id.seven_1);
        button[14] = findViewById(R.id.seven_2);
        button[7] = findViewById(R.id.eight_1);
        button[15] = findViewById(R.id.eight_2);


        confirm = findViewById(R.id.confirm);

        //設定監聽事件(開啟相簿) 
        for (int i = 0; i < 16; i++) {
            final int currentButtonIndex = i;
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bt = currentButtonIndex;
                    openGallery(bt);
                }
            });

        }

      confirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        // 當按鈕被點擊時執行的操作
        if (imageByteArray[0] != null && imageByteArray[8] != null) {
            isempty = false; // 如果第一張和最後一張圖片都不為空，則設置 isempty 為 false
        } else {
            isempty = true; // 否則設置 isempty 為 true
        }

        // 如果 isempty 為 true，顯示警告對話框
        if (isempty) {
            new AlertDialog.Builder(RememberUploadImage.this)
                    .setTitle("至少新增兩張圖片喔!!!")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else {
            // 如果 isempty 為 false，顯示輸入名稱對話框
            dbHelper = new SqlDataBaseHelper(getApplicationContext());
            db = dbHelper.getWritableDatabase();

            new AlertDialog.Builder(RememberUploadImage.this)
                    .setTitle("為這一個模式取一個名字吧 ! !")
                    .setView(R.layout.dialog_input) // 載入自定義的佈局檔案，用來顯示輸入框
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = ((AlertDialog) dialog).findViewById(R.id.editText);
                            String userInput = editText.getText().toString();

                            if (!userInput.isEmpty()) { // 如果使用者輸入不為空
                                photoname = userInput;
                                saveByteArrayToDatabase(imageByteArray);
                            } else {
                                // 使用者未輸入字串，顯示提示訊息
                                Toast.makeText(RememberUploadImage.this, "請輸入名稱", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }
    }
});
    }

    public void openGallery(int buttonIndex) {
        currentButtonIndex = buttonIndex; // 保存當前按鈕索引
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }


    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        startImageCrop(selectedImageUri);
                    }
                }
            });


    // 啟動裁切
    public void startImageCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setOutputCompressQuality(100)
                .setRequestedSize(256, 256)
                .start(this);
    }

    // ActivityResultCallback for cropLauncher
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri croppedImageUri = result.getUri();
            Bitmap croppedBitmap = getBitmapFromUri(croppedImageUri);
            if (croppedBitmap != null) {

                // 將裁剪後的圖片轉換為字節數組
                byte[] croppedImageByteArray = getByteArrayFromBitmap(croppedBitmap);

                // 將字節數組存儲到 imageByteArray 中，假設 currentButtonIndex 為按鈕的索引
                imageByteArray[currentButtonIndex] = croppedImageByteArray;

                // 在對應的 ImageButton 上顯示裁剪後的圖片
                button[currentButtonIndex].setImageBitmap(croppedBitmap);
            }
        }
    }

    // 將 Bitmap 轉換為字節數組
    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    // 根據 Uri 取得 Bitmap
    public Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveByteArrayToDatabase(byte[][] byteArray) {
        dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        ContentValues c = new ContentValues();
        ContentValues d = new ContentValues();
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != null && byteArray[i].length > 0) {
                values.put(btArray[i], byteArray[i]);
                c.put(btArray[i], btNum[i]);
                d.put(btArray[i], Dir[i]);
            }

        }

        values.put("list_name", photoname);
        c.put("list_name", photoname);
        d.put("list_name", photoname);

        String tableName = "family";
        String tableName2 = "label";
        String tableName3 = "direction";
        db.insert(tableName, null, values);
        db.insert(tableName2, null, c);
        db.insert(tableName3, null, d);

        db.close();

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // 取得Editor
        SharedPreferences.Editor editor = getPrefs.edit();
        // 將version的值設為1
        editor.putString("list_name", photoname);
        editor.apply();

        Intent intent = new Intent(RememberUploadImage.this, family.class);
        startActivity(intent);
        finish();
    }

}

