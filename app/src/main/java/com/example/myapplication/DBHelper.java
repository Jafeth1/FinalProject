package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "FavImageDB";
    protected final static int VERSION_NUM = 2;
    public final static String TABLE_NAME = "FAVS";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_DATE = "DATE";
    public final static String COL_FILENAME = "FILENAME";
    public final static String COL_EXPLANATION = "EXPLANATION";
    public final static String COL_HDURL = "HDURL";



    public DBHelper(Context activity) {
        super(activity, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT,"
                + COL_DATE + " TEXT,"
                + COL_FILENAME + " TEXT,"
                + COL_EXPLANATION + " TEXT,"
                + COL_HDURL + " TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//         Drop old table
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

//         Create the new table
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Drop old table
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

//         Create the new table
        onCreate(db);
    }

}