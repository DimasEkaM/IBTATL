package com.roof_online.ibtatl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lenovo on 28-Feb-18.
 */

public class DataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ibtatl.db";
    private static final int DATABASE_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table biodata(id integer primary key, nama text null, hp text null, " +
                "perusahaan text null, email text null,produk text null,sales text null,foto text null,notes text null," +
                " created_at DATETIME DEFAULT CURRENT_DATE, status integer);";
        Log.d("Data","onCreate: " +sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  /*      if (newVersion == oldVersion + 1) {
            String sql = "ALTER TABLE biodata ADD COLUMN produk varchar;";
            db.execSQL(sql);
        } */
    }

}
