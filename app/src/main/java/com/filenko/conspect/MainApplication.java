package com.filenko.conspect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.activity.ActivityTest;
import com.filenko.conspect.activity.EditNode;
import com.filenko.conspect.activity.EditNote;
import com.filenko.conspect.adapters.NodeAdapter;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Objects;

public class MainApplication extends AppCompatActivity {
    private NodeAdapter adapter;
    private DataBaseConnection db;
    private Note rootNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.db = new DataBaseConnection(this);
        this.rootNote = new Note();
        ListView listViewNode = findViewById(R.id.listViewNode);
        this.adapter = new NodeAdapter(this.db, this, listViewNode);
        listViewNode.setAdapter(this.adapter);

        listViewNode.setOnItemClickListener((parent, view, position, id) -> {
            clickOnListView (position);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        clickOnButtonBack (this.rootNote.getParent());
        return true;
    }

    private void clickOnButtonBack (int idParent) {
        if(idParent> 0) {
            this.adapter.getNodesFromDataBaseById(idParent);
            getNoteFromDataBaseById(idParent);
            setTitle(this.rootNote.getName());
        } else {
            this.rootNote.clear();
            this.adapter.getNodesFromDataBaseById(0);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            setTitle("Конспект");
        }
    }



    private void clickOnListView (int position) {
        this.rootNote = (Note)this.adapter.getItem(position);
        //1. Если нажимаю на папку - значит адаптер апдейт
        //2. Если нажимаю на заметку - значит открыть EditNote передать ID note
        if(rootNote.getType() == 2) {
            Intent intent = new Intent(this, EditNote.class);
            Bundle b = new Bundle();
            b.putInt("key", this.rootNote.getId());
            intent.putExtras(b);
            startActivity(intent);
        } else {
            this.adapter.getNodesFromDataBaseById(rootNote.getId());
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            setTitle(rootNote.getName());
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("----------", "onActivityResult");
//        Toast toast1 = Toast.makeText(this,
//                "ВАЩЕ НЕ ПОНЯТНО!" , Toast.LENGTH_LONG);
//        toast1.show();
//
//
//        if (data != null){
//            Toast toast = Toast.makeText(this,
//                    "onActivityResult" , Toast.LENGTH_LONG);
//            toast.show();
//            int idParent = data.getIntExtra("key", 0);
//            clickOnButtonBack(idParent);
//        } else {
//            Toast toast = Toast.makeText(this,
//                    "ОШИБКА!!!" , Toast.LENGTH_LONG);
//            toast.show();
//        }
//    }

    /** Get Note from database by id */
    private void getNoteFromDataBaseById (int id) {
        if(id > 0) {
            SQLiteDatabase database = this.db.getReadableDatabase();
            try (Cursor query = database.rawQuery("SELECT * FROM NOTES WHERE _id = " + id + ";", null)) {
                while (query.moveToNext()) {
                    this.rootNote.setId(id);
                    this.rootNote.setType(query.getInt(1));
                    this.rootNote.setParent(query.getInt(2));
                    this.rootNote.setName(query.getString(3));
                    this.rootNote.setDescription(query.getString(4));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Добавить каталог");
        menu.add(0, 2, 0, "Добавить карточку");
        menu.add(0, 3, 0, "Тестирование");
        menu.add(0, 4, 0, "Сделать копию БД");

        return super.onCreateOptionsMenu(menu);
    }


    /**
    1. Если нажимаем добавить каталог
        - Отправляем на EditNode c параметром родителя
    2. Если нажимаем добавить карточку
        - Проверяем есть ли у папки parent если нет, то нет возможности добавить карточку
        - Отправляем на Edit
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle b;
        switch (item.getItemId()) {
            case 1 :
                this.rootNote.setParent(this.rootNote.getId());
                intent = new Intent(this, EditNode.class);
                b = new Bundle();
                b.putInt("parent", this.rootNote.getId());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case 2 :
                if(this.rootNote.getId()> 0) {
                    this.rootNote.setParent(this.rootNote.getId());
                    intent = new Intent(this, EditNote.class);
                    b = new Bundle();
                    b.putInt("parent", this.rootNote.getId());
                    intent.putExtras(b);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(this,
                            "Добавьте сначала каталог!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case 3 :
                this.rootNote.setParent(this.rootNote.getId());
                intent = new Intent(this, ActivityTest.class);
                b = new Bundle();
                b.putInt("idnote", this.rootNote.getId());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case 4 :

                final String inFileName = this.getDatabasePath("nodebase").getAbsolutePath();
                File dbFile = new File(inFileName);

                try(FileInputStream fis = new FileInputStream(dbFile)) {

                    String outFileName =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            "/database_copy.db";
                    File destFile = new File(outFileName);
                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(destFile);

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    output.flush();
                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clickOnButtonBack (this.rootNote.getParent());
    }
}
