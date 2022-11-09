package com.example.swishbirthday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BirthDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BirthContract.BirthEntry.TABLE_NAME + " (" +
                    BirthContract.BirthEntry._ID + " INTEGER PRIMARY KEY," +
                    BirthContract.BirthEntry.COLUMN_NAME_NOMBRE + " TEXT," +
                    BirthContract.BirthEntry.COLUMN_NAME_FECHA + " TEXT," +
                    BirthContract.BirthEntry.COLUMN_NAME_HORA + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BirthContract.BirthEntry.TABLE_NAME;

    public BirthDbHelper(Context context, String database_name) {
        super(context, database_name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}