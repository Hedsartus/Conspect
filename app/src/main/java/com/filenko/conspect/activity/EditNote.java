package com.filenko.conspect.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.common.IntentStart;
import com.filenko.conspect.db.NoteDataBase;
import com.filenko.conspect.essence.Note;

import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;


public class EditNote extends AppCompatActivity {
    private final Note note = new Note();
    private final NoteDataBase nDatabase = new NoteDataBase(this);
    private RichEditor mEditor;
    private final String TAG = "___EditNote___";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        createEditor();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getInt("key") > 0) {
            int idNote = bundle.getInt("key");
            if (idNote > 0) {
                this.note.setId(idNote);
                nDatabase.getDataFromDatabase(this.note.getId(), this.note);
                setFields();
            }
        } else if (bundle != null && bundle.getInt("parent") > 0) {
            this.note.setParent(bundle.getInt("parent"));
            //Log.d(TAG, "id parent = "+this.note.getParent());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                viewToEssence();
                saveOrUpdateNote();
                break;
            case R.id.top_menu_question:
                IntentStart.intentStartWithIntParam(
                        this, EditQuestion.class,
                        "idnote", this.note.getId());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveOrUpdateNote() {
        String toastMsg;
        if (note.getId() == 0) {
            this.note.setType(2);
            checkParent();
            toastMsg = "Карточка успешно добавлена!";
        } else {
            checkParent();
            toastMsg = "Карточка успешно обновлена!";
        }

        if (!this.nDatabase.saveNote(this.note)) {
            toastMsg = "Возникли проблемы!!!";
        }
        Toast toast = Toast.makeText(this, toastMsg, Toast.LENGTH_LONG);
        toast.show();
    }

    private void viewToEssence() {
        this.note.setName(((EditText) findViewById(R.id.nodesName)).getText().toString());
        this.note.setHtml(mEditor.getHtml());
        this.note.setDescription(((EditText) findViewById(R.id.etDescription)).getText().toString());
    }


    private void setFields() {
        ((EditText) findViewById(R.id.nodesName)).setText(this.note.getName());
        mEditor.setHtml(this.note.getHtml());
        ((EditText) findViewById(R.id.etDescription)).setText(this.note.getDescription());
    }

    /**
    * Проверяем, если текущая запись не родитель
     *  то присваиваем ей тип родителя, т.к. ей добавлен потомок
    * */
    private void checkParent() {
        if (this.note.getParent() > 0) {
            Note parent = new Note();
            nDatabase.getDataFromDatabase(this.note.getParent(), parent);

            if (parent.getType() == 2) {
                parent.setType(1);
                this.nDatabase.saveNote(parent);
            }
        }
    }

    private void createEditor() {
        mEditor = findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.DKGRAY);

        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());
        findViewById(R.id.action_redo).setOnClickListener(v -> mEditor.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        findViewById(R.id.action_subscript).setOnClickListener(v -> mEditor.setSubscript());

        findViewById(R.id.action_superscript).setOnClickListener(v -> mEditor.setSuperscript());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLUE : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(v -> mEditor.setIndent());
        findViewById(R.id.action_outdent).setOnClickListener(v -> mEditor.setOutdent());
        findViewById(R.id.action_align_left).setOnClickListener(v -> mEditor.setAlignLeft());
        findViewById(R.id.action_align_center).setOnClickListener(v -> mEditor.setAlignCenter());
        findViewById(R.id.action_align_right).setOnClickListener(v -> mEditor.setAlignRight());
        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> mEditor.setBullets());
        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> mEditor.setNumbers());
    }


}
