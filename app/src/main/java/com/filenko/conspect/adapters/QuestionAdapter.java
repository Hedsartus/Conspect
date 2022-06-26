package com.filenko.conspect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;

public class QuestionAdapter extends BaseAdapter {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Question> objects = new ArrayList<>();
    private boolean isAlready;
    private int idNote;
    private SwipeMenuListView listView;

    public QuestionAdapter(DataBaseConnection db, Context ctx, int idNote, SwipeMenuListView listView) {
        this.db = db;
        this.ctx = ctx;
        lInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isAlready = true;
        this.idNote = idNote;
        this.listView = listView;

        if(this.idNote>0) loadQuestionData(this.idNote);

    }

    @Override
    public int getCount() {
        return this.objects.size();
    }

    @Override
    public Object getItem(int position) {
        return this.objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        Question q = (Question) getItem(position);
        return q.getId();
    }



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
            notifyDataSetChanged();
        }
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
}
