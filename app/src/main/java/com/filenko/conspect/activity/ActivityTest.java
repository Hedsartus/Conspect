package com.filenko.conspect.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Note;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityTest extends AppCompatActivity {
    private DataBaseConnection db;
    private int idNode;
    private List<Question> question = new ArrayList<>();
    private List<Integer> tempIdQuestions = new ArrayList<>();
    private List<Integer> tempIdNote = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.db = new DataBaseConnection(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("idnote") > 0) {
            idNode = bundle.getInt("idnote");
            loadQuestionData(idNode);
            setTitle("Вопрос 1 из "+this.question.size());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadQuestionData(int id) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        this.tempIdQuestions.clear();

        List<Integer> list = new ArrayList<>();
        list.add(id);
        recLoadIdQuestions (list, database);

        for(Integer idQuestion : this.tempIdQuestions) {
            loadQuestion(idQuestion);
        }

    }

    private void recLoadIdQuestions (List<Integer> id, SQLiteDatabase database) {
        List<Integer> list = new ArrayList<>();
        String sql = null;
        if(list.size()>1) {
            sql = "SELECT * FROM NOTES WHERE parent IN = (" + getStringArrayId(list) + ");";
        } else if(list.size()>0) {
            sql = "SELECT * FROM NOTES WHERE parent = " + id + ";";
        }

        try (Cursor q = database.rawQuery(sql, null)) {
            while (q.moveToNext()) {
                if(q.getInt(1) == 1) {
                    list.add(q.getInt(0));
                } else {
                    this.tempIdQuestions.add(q.getInt(0));
                }
            }
        } finally {
            recLoadIdQuestions(list, database);
        }
    }

    private String getStringArrayId (List<Integer> list) {
        StringBuilder str = new StringBuilder();
        for(Integer i : list) {
            if(str.equals("")) {
                str.append(i);
            } else {
                str.append(", "+i);
            }
        }
        Log.d("))))))))))))))))", str.toString());
        return str.toString();
    }

    public void loadQuestion(int idNote) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery(
                "SELECT * FROM QUESTIONS WHERE idnote = " + idNote + ";", null)) {
            while (query.moveToNext()) {
                this.question.add(
                        new Question(
                                query.getInt(0),
                                query.getInt(1),
                                query.getString(2))
                );
            }
        } finally {
            addAnswersToQuestion ();
        }
    }

    private void addAnswersToQuestion () {
        SQLiteDatabase database = this.db.getReadableDatabase();
        for(Question q : this.question) {
            loadAnswers(q, database);
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
                                query.getInt(3), false)
                );
            }

        }
    }
}

