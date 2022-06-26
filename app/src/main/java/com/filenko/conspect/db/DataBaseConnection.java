package com.filenko.conspect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseConnection extends SQLiteOpenHelper {
    private static final String DB_NAME = "nodebase"; // Имя базы данных
    private static final int DB_VERSION = 1; // Версия базы данных

    public DataBaseConnection(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE NOTES ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "type INTEGER  NOT NULL, "
                + "parent INTEGER, "
                + "name TEXT NOT NULL, "
                + "description TEXT, "
                + "html TEXT);");

        db.execSQL("CREATE TABLE QUESTIONS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "idnote INTEGER  NOT NULL, "
                + "title TEXT);");

        db.execSQL("CREATE TABLE ANSWER ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "idquestion INTEGER  NOT NULL, "
                + "title TEXT NOT NULL,"
                + "correct INTEGER NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
