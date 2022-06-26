package com.filenko.conspect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.load.engine.Resource;
import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;
import java.util.List;

public class AnswerAdapter extends BaseAdapter {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private List<Answer> objects;
    private Question question;
    private SwipeMenuListView listViewAnswer;


    public AnswerAdapter(DataBaseConnection db, Context ctx, List<Answer> objects, SwipeMenuListView lv) {
        this.db = db;
        this.ctx = ctx;
        this.objects = objects;
        this.listViewAnswer = lv;
        this.lInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setQuestion (Question question) {
        this.question = question;
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
        Answer a = (Answer) getItem(position);
        return a.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_edit_answer, parent, false);
        }

        ToggleButton btn = view.findViewById(R.id.btn_leftmenu);
        btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                listViewAnswer.smoothOpenMenu(position);
            } else {
                listViewAnswer.smoothCloseMenu();
            }
        });


        Answer answer = (Answer) getItem(position);
        CheckBox cb = view.findViewById(R.id.checkboxAnswer);
        cb.setChecked(answer.isCorrect());

        EditText et = view.findViewById(R.id.item_answer_text);
        et.setText(answer.getAnswer());

        ImageButton btnsave = view.findViewById(R.id.btnSaveAnswer);

        btnsave.setOnClickListener(v-> {
            answer.setAnswer(et.getText().toString());
            answer.setCorrect(cb.isChecked());
            saveOrUpdateAnswer (answer);
            btnEnabled(btnsave, false);
        });

        if(et.getText().toString().equals("")) {
            btnEnabled(btnsave, false);
        }

        if(answer.getId() > 0) {
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

        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!et.getText().toString().equals("")) {
                btnEnabled(btnsave, true);
            } else {
                btnEnabled(btnsave, false);
            }
        });

        return view;
    }

    private void saveOrUpdateAnswer (Answer answer) {
        SQLiteDatabase database = this.db.getWritableDatabase();

        int correct = answer.isCorrect()? 1:0;
        ContentValues dataValues = new ContentValues();
        dataValues.put("idquestion", this.question.getId());
        dataValues.put("title", answer.getAnswer());
        dataValues.put("correct", correct);


        if (answer.getId() > 0) {
            database.update(
                    "ANSWER", dataValues, "_id = ?",
                    new String[]{String.valueOf(answer.getId())});
        } else {
            answer.setId((int) database.insert("ANSWER", null, dataValues));
        }

    }

    private void deleteAnswer (int id) {

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
