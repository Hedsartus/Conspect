package com.filenko.conspect.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

public class EditNode extends AppCompatActivity {
    private final Note note = new Note();
    private DataBaseConnection dbconn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_edit_node);

        this.dbconn = new DataBaseConnection(this);


        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("key") > 0) {
            int idNote = (Integer)bundle.getSerializable(Integer.class.getSimpleName());
            if(idNote>=0) {
                this.note.setId(idNote);
                getDataFromDatabase (this.note.getId());
                setFields();
            }
        } else if(bundle!= null && bundle.getInt("parent") > 0) {
            this.note.setParent(bundle.getInt("parent"));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menus, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_menu_save:
                viewToEssence ();
                saveNewNode ();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNewNode () {
        SQLiteDatabase database = this.dbconn.getWritableDatabase();
        ContentValues dataValues = new ContentValues();
        dataValues.put("type", 1);
        dataValues.put("parent", this.note.getParent());
        dataValues.put("name", this.note.getName());
        dataValues.put("description", this.note.getDescription());
        dataValues.put("html", "");

        if (note.getId() > 0) {
            database.update(
            "NOTES", dataValues, "_id = ?",
                    new String[]{String.valueOf(this.note.getId())});
        } else {
            this.note.setId((int) database.insert("NOTES", null, dataValues));
        }

        Intent intent = new Intent();
        intent.putExtra("key", this.note.getParent());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void viewToEssence () {
        this.note.setName(((EditText)findViewById(R.id.nodesName)).getText().toString());
        this.note.setDescription(((EditText)findViewById(R.id.etDescription)).getText().toString());
    }

    private void getDataFromDatabase (int id) {
        try {
            SQLiteDatabase db = this.dbconn.getReadableDatabase();

            String sql = "SELECT * FROM NOTES WHERE _id = "+id+";";

            Cursor query = db.rawQuery(sql, null);

            while(query.moveToNext()){
                this.note.setType(query.getInt(1));
                this.note.setParent(query.getInt(2));
                this.note.setName(query.getString(3));
                this.note.setDescription(query.getString(4));
                this.note.setHtml(query.getString(5));

            }
            query.close();
            db.close();

        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable - fillDataLinkWords - ", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setFields () {
        ((EditText)findViewById(R.id.nodesName)).setText(this.note.getName());
        ((EditText)findViewById(R.id.etDescription)).setText(this.note.getDescription());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //или this.finish или что то свое
        return true;
    }
}
