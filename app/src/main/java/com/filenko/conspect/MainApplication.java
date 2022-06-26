package com.filenko.conspect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.activity.EditNode;
import com.filenko.conspect.activity.EditNote;
import com.filenko.conspect.adapters.NodeAdapter;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Note;

public class MainApplication extends AppCompatActivity {
    private NodeAdapter adapter;
    private DataBaseConnection db;
    private Note rootNote;
    private ListView listViewNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.db = new DataBaseConnection(this);
        this.rootNote = new Note();
        this.listViewNode = findViewById(R.id.listViewNode);
        this.adapter = new NodeAdapter(this.db, this, listViewNode);
        this.listViewNode.setAdapter(this.adapter);

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(rootNote.getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null){
            return;
        }
        int idParent = data.getIntExtra("key", 0);
        clickOnButtonBack (idParent);
    }

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

        return super.onCreateOptionsMenu(menu);
    }


    //1. Если нажимаем добавить каталог
    //- Отправляем на EditNode c параметром родителя
    //2. Если нажимаем добавить карточку
    //- Проверяем есть ли у папки parent если нет но ошибка
    //- Отправляем на Edit
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle b;
        switch (item.getItemId()) {
            case 1 :
                intent = new Intent(this, EditNode.class);
                b = new Bundle();
                b.putInt("parent", this.rootNote.getId());
                intent.putExtras(b);
                startActivityForResult(intent, 1);
                break;
            case 2 :
                if(this.rootNote.getId()> 0) {
                    intent = new Intent(this, EditNote.class);
                    b = new Bundle();
                    b.putInt("parent", this.rootNote.getId());
                    intent.putExtras(b);
                    startActivityForResult(intent, 1);
                } else {
                    Toast toast = Toast.makeText(this,
                            "Добавьте сначала каталог!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
