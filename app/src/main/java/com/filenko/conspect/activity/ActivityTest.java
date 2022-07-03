package com.filenko.conspect.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.container.ButtonTest;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityTest extends AppCompatActivity {
    private DataBaseConnection db;
    private final List<Question> question = new ArrayList<>();
    private int count;
    private LinearLayout layoutAnswersbutton;
    //private ButtonTestClickListener buttonTestClickListener;
    private Button btnNextQuestion;
    private List<ButtonTest> buttonTestList = new ArrayList<>();
    private int errorAnswer = 0;
    private TextView tvTestQuestion;
    private boolean isNext = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        this.db = new DataBaseConnection(this);
        this.count = 0;
        this.layoutAnswersbutton = findViewById(R.id.createButtonAnswerLayout);
        this.tvTestQuestion = findViewById(R.id.tvTestQuestion);
        //this.buttonTestClickListener = new ButtonTestClickListener();
        this.btnNextQuestion = findViewById(R.id.btnNextQuestion);

        this.btnNextQuestion.setOnClickListener(v -> {
            if(!isNext) {
                setEnableButton(false);
                for (ButtonTest bt : this.buttonTestList) {
                    if (bt.isCorrect() && bt.isSelectAnswer()) {
                        bt.setBackgroundColor(Color.GREEN);
                    }
                    if (bt.isCorrect() && !bt.isSelectAnswer()) {
                        this.errorAnswer++;
                    }
                    if (!bt.isCorrect() && bt.isSelectAnswer()) {
                        bt.setBackgroundColor(Color.RED);
                        this.errorAnswer++;
                    }
                }
                this.btnNextQuestion.setText("Дальше");
            } else {
                this.count++;

                if(this.question.size() > this.count) {
                    loadAnswers(this.question.get(count));
                    setTitle("Вопрос "+this.count+1+" из " + this.question.size());
                    setEnableButton(true);
                    this.btnNextQuestion.setText("Проверить");
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибок: "+this.errorAnswer+"!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    this.btnNextQuestion.setVisibility(View.GONE);
                }

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("idnote") > 0) {
            int idNode = bundle.getInt("idnote");
            loadTree(idNode);

        }

    }

    private void setEnableButton (boolean b) {
        for(ButtonTest bt : this.buttonTestList) {
            bt.setEnabled(b);
        }
        this.isNext = !b;

    }

    private void createAnswersButtons(Question question) {
        this.layoutAnswersbutton.removeAllViews();
        this.buttonTestList.clear();
        this.tvTestQuestion.setText(question.getTitle());

        switch (question.getType()) {
            case 1 : createMultipleButtons(question); break;
            case 2 : createYesNoButtons(question); break;
        }

    }

    private void createMultipleButtons(Question question) {
        for(Answer anr : question.getListAnswers()) {
            ButtonTest buttonTest = new ButtonTest(this, anr);
            buttonTest.setOnClickListener(new ButtonTestClickListener(question, this.buttonTestList));
            this.layoutAnswersbutton.addView(buttonTest);
            this.buttonTestList.add(buttonTest);
        }
    }

    private void createYesNoButtons(Question question){
        ButtonTest buttonTest;
        ButtonTest buttonTest1;

        for(Answer anr : question.getListAnswers()) {
            if(anr.isCorrect()) {
                anr.setAnswer("Да");
                buttonTest = new ButtonTest(this, anr);
                buttonTest1 = new ButtonTest(this, new Answer(0, question.getId(), "Нет", 0));
                this.layoutAnswersbutton.addView(buttonTest);
                this.layoutAnswersbutton.addView(buttonTest1);
            } else {
                anr.setAnswer("Нет");
                buttonTest1 = new ButtonTest(this, anr);
                buttonTest = new ButtonTest(this, new Answer(0, question.getId(), "Да", 0));
                this.layoutAnswersbutton.addView(buttonTest1);
                this.layoutAnswersbutton.addView(buttonTest);
            }
            buttonTest.setOnClickListener(new ButtonTestClickListener(question, this.buttonTestList));
            buttonTest1.setOnClickListener(new ButtonTestClickListener(question, this.buttonTestList));
            this.buttonTestList.add(buttonTest);
            this.buttonTestList.add(buttonTest1);
        }


    }

    private void checkQuestionList (List<Question> list) {
        for(Question q : list) {
            if(q.getListAnswers().size() < 1) {
                list.remove(q);
                continue;
            }

            if(q.getType() == 1 && q.getListAnswers().size() < 2) {
                list.remove(q);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadTree(int id) {
        SQLiteDatabase database = this.db.getReadableDatabase();
        List<Integer> list = new ArrayList<>();

        /*
        String sql = "WITH treenotes AS (SELECT _id, type, parent FROM NOTES " +
                "WHERE _id = " + id +" "+
                "UNION ALL " +
                "SELECT n._id, n.type, n.parent FROM NOTES as n " +
                "JOIN treenotes as tn " +
                "ON n._id = tn.parent )" +
                "SELECT * FROM treenotes WHERE type = 2";

         */

        String sql = "SELECT * FROM QUESTIONS WHERE idnote IN ( " +
                "WITH recursive " +
                "  Parrent_Id(n) AS ( " +
                "    VALUES("+id+") " +
                "    UNION " +
                "    SELECT _id FROM NOTES, Parrent_Id WHERE NOTES.parent = Parrent_Id.n) " +
                "SELECT _id FROM NOTES " +
                "WHERE NOTES._id IN Parrent_Id AND NOTES.parent != 0 )";

        try (Cursor q = database.rawQuery(sql, null)) {
            while (q.moveToNext()) {
                this.question.add(
                        new Question(
                                q.getInt(0),
                                q.getInt(1),
                                q.getInt(2),
                                q.getString(3)));
            }
        } finally {
            //checkQuestionList(this.question);
            if(this.question.size()>0) {
                Collections.shuffle(list);
                loadAnswers(this.question.get(count));
                setTitle("Вопрос 1 из " + this.question.size());
            }
        }
    }

    private void loadAnswers(Question question) {
        SQLiteDatabase database = this.db.getReadableDatabase();
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

        } finally {
            createAnswersButtons(question);
        }
    }

    class ButtonTestClickListener implements View.OnClickListener {
        private Question question;
        private List<ButtonTest> list;
        public ButtonTestClickListener(Question question, List<ButtonTest> buttonTestList) {
            this.question = question;
            this.list = buttonTestList;
        }

        @Override
        public void onClick(View v) {
            if(question.getType() == 2) {
                for(ButtonTest bt : this.list) {
                    bt.setBackgroundColor(Color.GRAY);
                    bt.setSelectedAnswer(false);
                }
            }


            ButtonTest bt = (ButtonTest) v;
            bt.setBackgroundColor(Color.YELLOW);
            bt.setSelectedAnswer(true);
        }
    }

}

