package com.filenko.conspect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.filenko.conspect.activity.ActivityTest;
import com.filenko.conspect.activity.EditNode;
import com.filenko.conspect.activity.EditNote;
import com.filenko.conspect.activity.ReadAll;
import com.filenko.conspect.adapters.NoteRecyclerViewAdapter;
import com.filenko.conspect.classes.FilesWorker;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Note;
import com.filenko.conspect.essence.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainApplication extends AppCompatActivity {
    private DataBaseConnection db;
    private static final int ACTIVITY_CHOOSE_FILE = 787;
    private NoteRecyclerViewAdapter adapter;
    private String tempPath;

    @SuppressLint("SetWorldReadable")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DataBaseConnection(this);

        RecyclerView listViewNote = findViewById(R.id.listViewNote);
        listViewNote.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider1));
        listViewNote.addItemDecoration(itemDecorator);

        this.adapter = new NoteRecyclerViewAdapter(db,this);
        listViewNote.setAdapter(this.adapter);

        this.adapter.setOnClickListener((view, position) -> {
            clickOnListView (position);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        clickOnButtonBack (this.adapter.getRootNote().getParent());
        return true;
    }

    private void clickOnButtonBack (int idParent) {
        if(idParent> 0) {
            this.adapter.getNodesFromDataBaseByIdPrent(idParent);
            this.adapter.getNoteFromDataBaseById(idParent);
            setTitle(this.adapter.getRootNote().getName());
        } else {
            this.adapter.getRootNote().clear();
            this.adapter.getNodesFromDataBaseByIdPrent(0);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            setTitle("Конспект");
        }
    }

    private void clickOnListView (int position) {
        Note note = this.adapter.getItem(position);
        //1. Если нажимаю на папку - значит адаптер апдейт
        //2. Если нажимаю на заметку - значит открыть EditNote передать ID note
        if(note.getType() == 2) {
            Intent intent = new Intent(this, EditNote.class);
            Bundle b = new Bundle();
            b.putInt("key", note.getId());
            intent.putExtras(b);
            startActivity(intent);
        } else {
            this.adapter.getNodesFromDataBaseByIdPrent(note.getId());
            this.adapter.setRootNote(note);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            setTitle(this.adapter.getRootNote().getName());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Добавить каталог");
        menu.add(0, 2, 0, "Добавить карточку");
        menu.add(0, 3, 0, "Тестирование");
        menu.add(0, 4, 0, "Сделать копию БД");
//        menu.add(0, 5, 0, "Загрузить БД");
        menu.add(0, 6, 0, "Открыть ветку");
        menu.add(0, 7, 0, "Сохранить ветку");
        menu.add(0, 8, 0, "Загрузить ветку");

        return super.onCreateOptionsMenu(menu);
    }


    /**
    1. Если нажимаем добавить каталог
        - Отправляем на EditNode c параметром родителя
    2. Если нажимаем добавить карточку
        - Проверяем есть ли у папки parent если нет, то нет возможности добавить карточку
        - Отправляем на Edit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle b;
        switch (item.getItemId()) {
            case 1 :
                intent = new Intent(this, EditNode.class);
                b = new Bundle();
                b.putInt("parent", this.adapter.getRootNote().getId());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case 2 :
                if(this.adapter.getRootNote().getId()> 0) {
                    intent = new Intent(this, EditNote.class);
                    b = new Bundle();
                    b.putInt("parent", this.adapter.getRootNote().getId());
                    intent.putExtras(b);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(this,
                            "Добавьте сначала каталог!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case 3 :
                intent = new Intent(this, ActivityTest.class);
                b = new Bundle();
                b.putInt("idnote", this.adapter.getRootNote().getId());
                intent.putExtras(b);
                startActivity(intent);
                break;
            case 4 :
                copyDataBase();
                break;
            case 5:
                //loadDataBase();
                break;
            case 6:
                intent = new Intent(this, ReadAll.class);
                b = new Bundle();
                b.putInt("idnote", this.adapter.getRootNote().getId());
                intent.putExtras(b);
                startActivity(intent);
                break;

            //Сохранить ветку
            case 7: exportJsonFile(); break;

            //Загрузить ветку
            case 8: onBrowse(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void exportJsonFile() {
        String outFilePath =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS) +"/Conspect/";

        File fPath = new File(outFilePath);
        if(!fPath.exists()) {
            fPath.mkdir();
        }

        String outFileName =  this.adapter.getRootNote().getName()+".json";
        SQLiteDatabase database = this.db.getReadableDatabase();
        List<Note> listNote = loadNotesFromDatabase(this.adapter.getRootNote().getId(), database);
        loadQuestions(listNote, database);
        database.close();

        String json = FilesWorker.listToJson(listNote);
        FilesWorker.writeString(json, outFilePath+outFileName);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clickOnButtonBack(this.adapter.getRootNote().getId());
    }

    private void downloadNotes (List<Note> listNote) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        Map<Integer, Integer> idOldNewNote = new HashMap<>();
        for(int i = 0; i < listNote.size(); i++) {
            Note note = listNote.get(i);
            if(i == 0) {
                note.setParent(this.adapter.getRootNote().getId());
                idOldNewNote.put(note.getId(), this.db.insertNote(note, database));

                for(Question question : note.getListQuestion()) {
                    question.setIdNote(idOldNewNote.get(note.getId()));
                    question.setId(this.db.insertQuestion(question, database));

                    for(Answer answer : question.getListAnswers()) {
                        answer.setIdQuestion(question.getId());
                        answer.setId(this.db.insertAnswer(answer, database));
                    }
                }

            } else {
                if(idOldNewNote.containsKey(note.getParent())) {
                    note.setParent(idOldNewNote.get(note.getParent()));
                    idOldNewNote.put(note.getId(), this.db.insertNote(note, database));

                    for(Question question : note.getListQuestion()) {
                        question.setIdNote(idOldNewNote.get(note.getId()));
                        question.setId(this.db.insertQuestion(question, database));

                        for(Answer answer : question.getListAnswers()) {
                            answer.setIdQuestion(question.getId());
                            answer.setId(this.db.insertAnswer(answer, database));
                        }
                    }
                } else {
                    note.setParent(this.adapter.getRootNote().getId());
                    idOldNewNote.put(note.getId(), this.db.insertNote(note, database));

                    for(Question question : note.getListQuestion()) {
                        question.setIdNote(idOldNewNote.get(note.getId()));
                        question.setId(this.db.insertQuestion(question, database));

                        for(Answer answer : question.getListAnswers()) {
                            answer.setIdQuestion(question.getId());
                            answer.setId(this.db.insertAnswer(answer, database));
                        }
                    }
                }
            }
        }
        database.close();

    }

    private void loadQuestions(List<Note> listNote, SQLiteDatabase database) {
        for(Note note : listNote) {
            String sql = "SELECT * FROM QUESTIONS WHERE idnote = "+note.getId();
            try (Cursor q = database.rawQuery(sql, null)) {
                while (q.moveToNext()) {
                    Question question = new Question(
                            q.getInt(0),
                            q.getInt(1),
                            q.getInt(2),
                            q.getString(3),
                            q.getInt(4));
                    loadAnswers(question, database);
                    note.addChild(question);
                }
            }
        }
    }

    private List<Note> loadNotesFromDatabase(int id, SQLiteDatabase database) {
        List<Note> listNote = new ArrayList<>();

        String sql = "WITH recursive " +
                "  Parrent_Id(n) AS ( " +
                "    VALUES("+id+") " +
                "    UNION " +
                "    SELECT _id FROM NOTES, Parrent_Id WHERE NOTES.parent = Parrent_Id.n) " +
                "SELECT _id, type, parent, name, description, html FROM NOTES " +
                "WHERE NOTES._id IN Parrent_Id AND NOTES.parent != 0 ORDER BY _id";

        try (Cursor q = database.rawQuery(sql, null)) {
            while (q.moveToNext()) {
                listNote.add(new Note(q.getInt(0),
                        q.getInt(1),
                        q.getInt(2),
                        q.getString(3),
                        q.getString(4),
                        q.getString(5)));
            }
            return listNote;
        }
    }

    private void loadAnswers(Question question, SQLiteDatabase database) {
        try (Cursor query = database.rawQuery(
                "SELECT * FROM ANSWER WHERE idquestion = " + question.getId() + ";", null)) {

            while (query.moveToNext()) {
                question.addAnswer(
                        new Answer(
                                query.getInt(0),
                                query.getInt(1),
                                query.getString(2),
                                query.getInt(3))
                );
            }

        }
    }

    public void onBrowse() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("application/json");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (resultCode != RESULT_OK || data == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            String fileName = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS)+"/Conspect/"+getFileName(uri);
            String jsonJava = FilesWorker.readStringJson(fileName, this);
            List<Note> listNote = FilesWorker.jsonToList(jsonJava, Note.class);
            if(listNote != null) {
                downloadNotes (listNote);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri contentUri) {
        String result = null;
        if (contentUri.getScheme() != null && contentUri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(contentUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = contentUri.getPath();
            if (result == null) {
                return null;
            }
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void copyDataBase() {
        String inFileName = this.getDatabasePath("nodebase.db").getAbsolutePath();
        String outFiles = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS)+"/Database/nodebase.db";

        boolean sdff = FilesWorker.copyFiles(inFileName, outFiles);
        //Log.d("--------------", "Копирование БД : "+sdff);
        Toast toast = Toast.makeText(this,"Копирование БД : "+sdff, Toast.LENGTH_LONG);
        toast.show();
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void loadDataBase() {
//        String outFiles = this.getDatabasePath("nodebase.db").getAbsolutePath();
//        String inFileName = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOCUMENTS)+"/Database/nodebase.db";
//
//        boolean sdff = FilesWorker.copyFiles(inFileName, outFiles);
//        Log.d("--------------", "Загрузка БД : "+sdff);
//
//        Toast toast = Toast.makeText(this,"Загрузка БД : "+sdff, Toast.LENGTH_LONG);
//        toast.show();
//    }

    /** LoadJson */
    class LoadJson extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


}
