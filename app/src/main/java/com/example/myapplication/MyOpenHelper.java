package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {

    public static String name = "MyDatabase";
    public static int version = 1;
    public static String TABLE_NAME = "Messages";
    public static String COL_MESSAGE = "Message";
    public static String COL_SEND_OR_RECEIVE = "SendOrReceive";
    public static String COL_TIME_SENT = "timeSent";

    public MyOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MESSAGE + " TEXT,"
                + COL_SEND_OR_RECEIVE + " INTEGER,"
                + COL_TIME_SENT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}
