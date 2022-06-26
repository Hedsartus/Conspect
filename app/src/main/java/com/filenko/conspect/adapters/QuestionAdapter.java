package com.filenko.conspect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Question> objects = new ArrayList<>();
    private int idNote;

    public QuestionAdapter(DataBaseConnection db, Context ctx, int idNote) {
        this.db = db;
        this.ctx = ctx;
        lInflater = LayoutInflater.from(ctx);
        this.idNote = idNote;

        if(this.idNote>0) loadQuestionData(this.idNote);

    }


/*
    public void addNewQuestion () {
        Question q = new Question();
        q.setIdNote(this.idNote);
        this.objects.add(q);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_question, parent, false);
        }

        Question question = (Question) getItem(position);
        EditText et = view.findViewById(R.id.item_question_title);
        et.setText(question.getTitle());

        ImageButton btn = view.findViewById(R.id.btn);
        btn.setOnClickListener(v -> {
            listView.smoothOpenMenu(position);
        });

        ImageButton btnsave = view.findViewById(R.id.btnSaveQuestion);
        btnsave.setOnClickListener(v-> {
            question.setTitle(et.getText().toString());
            saveOrUpdateQuestion (question);
            btnEnabled(btnsave, false);
        });

        if(et.getText().toString().equals("")) {
            btnEnabled(btnsave, false);
        }

        if(question.getId() > 0) {
            btnEnabled(btnsave, false);
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    btnEnabled(btnsave, true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void saveOrUpdateQuestion (Question question) {
        SQLiteDatabase database = this.db.getWritableDatabase();

        ContentValues dataValues = new ContentValues();
        dataValues.put("idnote", question.getIdNote());
        dataValues.put("title", question.getTitle());

        if (question.getId() > 0) {
            database.update(
                    "QUESTIONS", dataValues, "_id = ?",
                    new String[]{String.valueOf(question.getId())});
        } else {
            question.setId((int) database.insert("QUESTIONS", null, dataValues));
        }
    }

    private boolean checkQuestion(String questionTitle) {
        return !questionTitle.equals("");
    }


    private void btnEnabled (ImageButton btn , boolean enb) {
        if(enb) {
            btn.setEnabled(true);
            btn.setImageAlpha(255);
        } else {
            btn.setEnabled(false);
            btn.setImageAlpha(75);
        }
    }
*/
    public void loadQuestionData(int idNote) {
        this.objects.clear();

        SQLiteDatabase database = this.db.getReadableDatabase();
        try (Cursor query = database.rawQuery(
                "SELECT * FROM QUESTIONS WHERE idnote = " + idNote + ";", null)) {
            while (query.moveToNext()) {
                this.objects.add(
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
        for(Question q : this.objects) {
            loadAnswers(q, database);
            Log.d("TAG--------", q.getTitle());
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


    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.lInflater.inflate(R.layout.item_question, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.recyclerViewSection.setAdapter(new AnswerAdapter(db, ctx));
        return vh;
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, int position) {
        Question question = objects.get(position);
        holder.questionTitle.setText(question.getTitle());
        AnswerAdapter answerAdapter = (AnswerAdapter) holder.recyclerViewSection.getAdapter();
        answerAdapter.setQuestion(question);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final EditText questionTitle;
        final RecyclerView recyclerViewSection;
        ViewHolder(View view){
            super(view);
            questionTitle = view.findViewById(R.id.item_question_title);
            recyclerViewSection = view.findViewById(R.id.rvAnswers);
        }
    }
}
