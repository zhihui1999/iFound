package com.gizwits.opensource.appkit.DiyClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBOpenHelper extends SQLiteOpenHelper {
    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {super(context, "chengyuan.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("数据库", "onCreate: 数据库创建");
        db.execSQL("CREATE TABLE person(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),phone VARCHAR(11))");
        ContentValues values1 = new ContentValues();
        values1.put("name","哈哈哈");
        values1.put("phone","15515511515");
        db.insert("person","",values1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
