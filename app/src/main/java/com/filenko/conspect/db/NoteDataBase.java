package com.filenko.conspect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.filenko.conspect.essence.Note;

public class NoteDataBase {
    private final DataBaseConnection connection;

    public NoteDataBase(Context context) {
        this.connection = new DataBaseConnection(context);
    }

    public boolean saveNote(Note note) {
        SQLiteDatabase database = this.connection.getWritableDatabase();

        ContentValues dataValues = new ContentValues();
        dataValues.put("type", note.getType());
        dataValues.put("parent", note.getParent());
        dataValues.put("name", note.getName());
        dataValues.put("description", note.getDescription());
        dataValues.put("html", note.getHtml());

        int isCorrect;
        if (note.getId() > 0) {
            isCorrect = database.update("NOTES", dataValues, "_id = ?",
                    new String[]{String.valueOf(note.getId())});
        } else {
            isCorrect = (int) database.insert("NOTES", null, dataValues);
            note.setId(isCorrect);
        }

        return isCorrect>0;
    }

    public boolean getDataFromDatabase (int id, Note note) {
        try {
            SQLiteDatabase sdb = this.connection.getReadableDatabase();

            Cursor query = sdb.rawQuery("SELECT * FROM NOTES WHERE _id = "+id+";", null);
            while(query.moveToNext()){
                note.setId(id);
                note.setType(query.getInt(1));
                note.setParent(query.getInt(2));
                note.setName(query.getString(3));
                note.setDescription(query.getString(4));
                note.setHtml(query.getString(5));
            }
            query.close();
            sdb.close();
            return true;
        } catch(SQLiteException e) {
            Log.d("__NoteDataBase__", e.getMessage());
            return false;
        }
    }
}
