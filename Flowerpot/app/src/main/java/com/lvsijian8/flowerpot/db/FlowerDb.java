package com.lvsijian8.flowerpot.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/4/7.
 */
public class FlowerDb extends SQLiteOpenHelper {
    public FlowerDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "flowerDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE temperature (num int(100));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
