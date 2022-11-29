package com.filenko.conspect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Note;
import com.filenko.conspect.essence.Question;


public class DataBaseConnection extends SQLiteOpenHelper {
    private static final String DB_NAME = "nodebase.db"; // Имя базы данных
    private static final int DB_VERSION = 2; // Версия базы данных

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
                + "type INTEGER  NOT NULL, "
                + "idnote INTEGER  NOT NULL, "
                + "title TEXT NOT NULL, "
                + "correct INTEGER);");

        db.execSQL("CREATE TABLE ANSWER ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "idquestion INTEGER  NOT NULL, "
                + "title TEXT NOT NULL,"
                + "correct INTEGER DEFAULT 0 );");

    }

    public int insertNote (Note note, SQLiteDatabase database) {
        ContentValues dataValues = new ContentValues();
        dataValues.put("type", note.getType());
        dataValues.put("parent", note.getParent());
        dataValues.put("name", note.getName());
        dataValues.put("description", note.getDescription());
        dataValues.put("html", note.getHtml());
        return (int) database.insert("NOTES", null, dataValues);
    }

    public int insertQuestion (Question question, SQLiteDatabase database) {
        int correct = question.getCorrect() ? 1 : 0;
        ContentValues dataValues = new ContentValues();
        dataValues.put("idnote", question.getIdNote());
        dataValues.put("type", question.getType());
        dataValues.put("title", question.getTitle());
        dataValues.put("correct", correct);
        return (int) database.insert("QUESTIONS", null, dataValues);
    }

    public int updateQuestion (Question question, SQLiteDatabase database) {
        int correct = question.getCorrect() ? 1 : 0;
        ContentValues dataValues = new ContentValues();
        dataValues.put("idnote", question.getIdNote());
        dataValues.put("type", question.getType());
        dataValues.put("title", question.getTitle());
        dataValues.put("correct", correct);
        return database.update("QUESTIONS", dataValues, "_id = ?",
                new String[]{String.valueOf(question.getId())});
    }

    public int insertAnswer (Answer answer, SQLiteDatabase database) {
        int correct = answer.isCorrect() ? 1 : 0;
        ContentValues dataValues = new ContentValues();
        dataValues.put("idquestion", answer.getIdQuestion());
        dataValues.put("title", answer.getAnswer());
        dataValues.put("correct", correct);
        return (int) database.insert("ANSWER", null, dataValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE QUESTIONS ADD COLUMN correct INTEGER DEFAULT 0");
        }
    }


}
