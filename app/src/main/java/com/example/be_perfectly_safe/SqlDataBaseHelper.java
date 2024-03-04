package com.example.be_perfectly_safe;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class SqlDataBaseHelper extends SQLiteOpenHelper {


    private static final String DataBaseName = "users.db";
    private static final int DataBaseVersion = 1;
    private static final String DataBaseTable1 = "caregiver";
    private static final String DataBaseTable2 = "patient";
    private static final String DataBaseTable3 = "family";
    private static final String DataBaseTable4 = "food";
    private byte[] convertDrawableToByteArray;
    private final Context context;


    public SqlDataBaseHelper(Context context) {
        super(context, DataBaseName, null, DataBaseVersion);
        this.context = context;
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SqlTable1 = "CREATE TABLE IF NOT EXISTS caregiver (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "account TEXT not null," +
                "name TEXT not null," +
                "password TEXT not null," +
                "email TEXT not null," +
                "phone TEXT not null," +
                "patient1 TEXT ," +
                "patient2 TEXT ," +
                "patient3 TEXT ," +
                "photo BLOB ," +
                "login INTEGER not null" +
                ")";
        db.execSQL(SqlTable1);

        String SqlTable2 = "CREATE TABLE IF NOT EXISTS patient (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT ," +
                "level INTEGER ," +
                "birthday TEXT ," +
                "sex TEXT ," +
                "caregiver TEXT ," +
                "photo BLOB " +
                ")";
        db.execSQL(SqlTable2);

        String SqlTable3 = "CREATE TABLE IF NOT EXISTS family (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_name TEXT not null," +
                "photo1 BLOB not null," +
                "photo2 BLOB ," +
                "photo3 BLOB ," +
                "photo4 BLOB ," +
                "photo5 BLOB ," +
                "photo6 BLOB ," +
                "photo7 BLOB ," +
                "photo8 BLOB ," +
                "photo9 BLOB not null," +
                "photo10 BLOB ," +
                "photo11 BLOB ," +
                "photo12 BLOB ," +
                "photo13 BLOB ," +
                "photo14 BLOB ," +
                "photo15 BLOB ," +
                "photo16 BLOB " +
                ")";
        db.execSQL(SqlTable3);

        String SqlTable5 = "CREATE TABLE IF NOT EXISTS label (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_name TEXT not null," +
                "photo1 TEXT not null," +
                "photo2 TEXT ," +
                "photo3 TEXT ," +
                "photo4 TEXT ," +
                "photo5 TEXT ," +
                "photo6 TEXT ," +
                "photo7 TEXT ," +
                "photo8 TEXT ," +
                "photo9 TEXT not null," +
                "photo10 TEXT ," +
                "photo11 TEXT ," +
                "photo12 TEXT ," +
                "photo13 TEXT ," +
                "photo14 TEXT ," +
                "photo15 TEXT ," +
                "photo16 TEXT " +
                ")";
        db.execSQL(SqlTable5);String SqlTable6 = "CREATE TABLE IF NOT EXISTS direction (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "list_name TEXT not null," +
                "photo1 TEXT not null," +
                "photo2 TEXT ," +
                "photo3 TEXT ," +
                "photo4 TEXT ," +
                "photo5 TEXT ," +
                "photo6 TEXT ," +
                "photo7 TEXT ," +
                "photo8 TEXT ," +
                "photo9 TEXT not null," +
                "photo10 TEXT ," +
                "photo11 TEXT ," +
                "photo12 TEXT ," +
                "photo13 TEXT ," +
                "photo14 TEXT ," +
                "photo15 TEXT ," +
                "photo16 TEXT " +
                ")";
        db.execSQL(SqlTable6);

        String SqlTable4 = "CREATE TABLE IF NOT EXISTS food (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT not null" +
                ")";
        db.execSQL(SqlTable4);
        initializeDefaultData(db, context);
    }

    private void initializeDefaultData(SQLiteDatabase db, Context context) {
        // 在這裡執行初始化數據的操作
        ContentValues defaultValues = new ContentValues();
        ContentValues c = new ContentValues();
        ContentValues d = new ContentValues();
        defaultValues.put("list_name", "預設");
        c.put("list_name", "預設");
        d.put("list_name", "預設");
        Resources resources = context.getResources();
        // 創建一個包含所有預設圖片資源 ID 的數組
        int[] defaultImageResourceIds = {
                R.drawable.c1,
                R.drawable.o1,
                R.drawable.e1,
                R.drawable.p1,
                R.drawable.u1,
                R.drawable.s1,
                R.drawable.f1,
                R.drawable.t1,
                R.drawable.t2,
                R.drawable.f2,
                R.drawable.s2,
                R.drawable.u2,
                R.drawable.p2,
                R.drawable.e2,
                R.drawable.o2,
                R.drawable.c2
        };
        String[] Num = {"1", "2", "3", "4", "5", "6", "7", "8", "8", "7", "6", "5", "4", "3", "2", "1"};
        String[] Dir = {"l", "l", "l", "l", "l", "l", "l", "l", "r", "r", "r", "r", "r", "r", "r", "r"};

        for (int i = 1; i <= 16; i++) {
            int drawableId = defaultImageResourceIds[i - 1];
            byte[] defaultImageBytes = convertDrawableToByteArray(resources, drawableId);
            defaultValues.put("photo" + i, defaultImageBytes);
            c.put("photo" + i, Num[i-1]);
            d.put("photo" + i, Dir[i-1]);
        }

        // 插入數據
        db.insert("family", null, defaultValues);
        db.insert("label", null, c);
        db.insert("direction", null, d);

    }

    // 將 drawable 轉換為字節數組的方法
    private byte[] convertDrawableToByteArray(Resources resources, int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // 删除旧表
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTable1);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTable2);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTable3);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseTable4);
        // 创建新表
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int i, int i1) {
        onUpgrade(db, i, i1);
    }
}
