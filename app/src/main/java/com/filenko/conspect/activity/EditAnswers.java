package com.filenko.conspect.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.filenko.conspect.R;
import com.filenko.conspect.adapters.AnswerAdapter;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class EditAnswers extends AppCompatActivity {
    private DataBaseConnection db;
    private AnswerAdapter adapter;
    private Question question;
    private TextView questionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answers);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.db = new DataBaseConnection(this);
        this.question = new Question();
        this.questionText = findViewById(R.id.questionEditText);

        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("idquestion") > 0) {
            loadData (bundle.getInt("idquestion"));
        }

        SwipeMenuListView listViewAnswer = findViewById(R.id.answerListView);
        listViewAnswer.setDivider(null);
        //this.adapter = new AnswerAdapter(this.db,this, this.question.getListAnswers(), listViewAnswer);
        //t//his.adapter.setQuestion(this.question);
        //listViewAnswer.setAdapter(this.adapter);


        FloatingActionButton btnAddAnswer = findViewById(R.id.btnAddAnswer);
        btnAddAnswer.setOnClickListener(v -> {
            this.question.addAnswer(new Answer());
            this.adapter.notifyDataSetChanged();
        });



        SwipeMenuCreator creator = menu -> {
            // create "open" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                    0xCE)));
            // set item width
            openItem.setWidth(200);
            // set item title
            openItem.setTitle("Open");
            // set item title fontsize
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);

            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(200);
            // set a icon
            deleteItem.setIcon(R.drawable.delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        // set creator
        listViewAnswer.setMenuCreator(creator);
        listViewAnswer.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    // open
                    break;
                case 1:
                    // delete
                    break;
            }
            // false : close the menu; true : not close the menu
            return false;
        });
        listViewAnswer.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    private void loadData (int idQuestion) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery(
                "SELECT * FROM QUESTIONS WHERE _id = " + idQuestion + ";", null)) {
            while (query.moveToNext()) {
                this.question.setId(idQuestion);
                this.question.setIdNote(query.getInt(1));
                this.question.setTitle(query.getString(2));
            }
        } finally {
            this.questionText.setText(this.question.getTitle());
            loadAnswers(this.question.getId());
        }
    }

    private void loadAnswers(int idQuestion) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery(
                "SELECT * FROM ANSWER WHERE idquestion = " + idQuestion + ";", null)) {

            while (query.moveToNext()) {
                this.question.addAnswer(
                        new Answer(
                                query.getInt(0),
                                query.getInt(1),
                                query.getString(2),
                                query.getInt(3), false)
                );
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
