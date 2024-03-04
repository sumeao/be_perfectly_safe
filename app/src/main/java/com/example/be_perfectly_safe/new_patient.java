package com.example.be_perfectly_safe;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class new_patient extends AppCompatActivity {

    ImageView sethead;
    Button save;
    private Uri selectedImageUri;
    private SQLiteDatabase db;
    private static final int REQUEST_IMAGE_PICKER = 1;
    private static String patientName;
    private static String patientSex;
    private static String patientBirthday;
    private static String patientLevel;
    private static String patientCare;
    private Uri croppedImageUri; // 全局變量，用於保存裁剪後的圖片路徑


    EditText EditpatientName, EditpatientSex, EditpatientBirthday, EditpatientLevel, EditpatientCare;
    String check;
    private byte[] imageData;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        EditpatientName = findViewById(R.id.editTextTextPersonName);
        EditpatientSex = findViewById(R.id.editTextTextPersonNumber);
        EditpatientLevel = findViewById(R.id.editTextTextPersonAddress);
        EditpatientCare = findViewById(R.id.editTextTextPersonDisease1);
        EditpatientBirthday = findViewById(R.id.patientBirthday);
        sethead = findViewById(R.id.sethead);
        save = findViewById(R.id.save);

        SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase(); // 開啟資料庫

        sethead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICKER);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                patientName = EditpatientName.getText().toString();
                patientBirthday = EditpatientBirthday.getText().toString();
                patientSex = EditpatientSex.getText().toString();
                patientCare = EditpatientCare.getText().toString();
                patientLevel = EditpatientLevel.getText().toString();

                if (patientName == null || patientBirthday == null || patientSex == null || patientCare == null || patientLevel == null) {
                    System.out.println("您還有尚未填寫的欄位喔!!!");
                } else {
                    // 將裁剪後的圖片轉換為位元組數組
                    if (croppedImageUri != null) {
                        imageData = getByteArrayFromUri(croppedImageUri);
                    }else{

                    }
                    System.out.println("====================================================");
                    System.out.println(croppedImageUri);
                    System.out.println(imageData);
                    // 將位元組數組插入到資料庫的 photo 欄位
                    ContentValues values = new ContentValues();
                    values.put("name", patientName);
                    values.put("sex", patientSex);
                    values.put("birthday", patientBirthday);
                    values.put("caregiver", patientCare);
                    values.put("level", patientLevel);
                    values.put("photo", imageData);

                    String tableName = "patient";

                    long rowsAffected = db.insert(tableName, null, values);

                    if (rowsAffected != -1) {
                        Toast.makeText(view.getContext(), "病患資料新增成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(new_patient.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "病患資料新增失敗", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                // 啟動圖片裁剪
                startImageCrop(selectedImageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    croppedImageUri = result.getUri();
                    // 在這裡處理裁剪後的圖片
                    sethead.setImageURI(croppedImageUri);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                System.out.println("----------------------------------------");
                System.out.println(error);
                // 處理裁剪錯誤
            }
        }
    }

    private void startImageCrop(Uri sourceUri) {
        // 建立裁剪後的圖片保存路徑
        File cropFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "crop_image.jpg");
        croppedImageUri = Uri.fromFile(cropFile);
        File pictureDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String picturePath = pictureDir.getAbsolutePath();
        Log.d("ImagePath", "Picture Directory: " + picturePath);


        // 跳轉至裁剪界面
        CropImage.activity(sourceUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1) // 設定裁剪比例為 1:1，即圓形裁剪
                .setCropShape(CropImageView.CropShape.OVAL) // 設定裁剪形狀為圓形
                .setFixAspectRatio(true) // 固定裁剪比例
                .start(this);
    }

    private byte[] getByteArrayFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("============================");
            e.printStackTrace();
            return null;
        }
    }
}