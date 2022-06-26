package com.filenko.conspect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private List<Answer> objects;
    private Question question;


    public AnswerAdapter(DataBaseConnection db, Context ctx) {
        this.db = db;
        this.ctx = ctx;
        this.lInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setQuestion (Question question) {
        this.question = question;
        this.objects = question.getListAnswers();
        notifyDataSetChanged();
    }
    /*

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
            view = lInflater.inflate(R.layout.item_answer, parent, false);
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

    private void btnEnabled (ImageButton btn , boolean enb) {
        if(enb) {
            btn.setEnabled(true);
            btn.setImageAlpha(255);
        } else {
            btn.setEnabled(false);
            btn.setImageAlpha(75);
        }
    }*/

    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.lInflater.inflate(R.layout.item_answer, parent, false);
        return new AnswerAdapter.ViewHolder(view, null);
    }

    @Override
    public void onBindViewHolder(AnswerAdapter.ViewHolder holder, int position) {
        Answer answer = objects.get(position);
        holder.answerTitle.setText(answer.getAnswer());
        holder.checkBox.setChecked(answer.isCorrect());
        holder.button.setOnClickListener(new ButtonSaveClickListener(answer, holder));
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Answer answer;
        final EditText answerTitle;
        final CheckBox checkBox;
        final ImageButton button;
        ViewHolder(View view, Answer answer){
            super(view);
            this.answer = answer;
            checkBox = view.findViewById(R.id.checkboxAnswer);
            answerTitle = view.findViewById(R.id.item_answer_text);
            button = view.findViewById(R.id.btnSaveAnswer);
        }
    }

    public class ButtonSaveClickListener implements View.OnClickListener {
        private final Answer answer;
        private final AnswerAdapter.ViewHolder holder;
        public ButtonSaveClickListener(Answer answer, AnswerAdapter.ViewHolder holder) {
            this.answer = answer;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            this.answer.setAnswer(holder.answerTitle.getText().toString());
            answer.setCorrect(holder.checkBox.isChecked());
            saveOrUpdateAnswer (answer);
            holder.button.setEnabled(false);
            holder.button.setImageAlpha(75);
        }

        private void saveOrUpdateAnswer (Answer answer) {
            SQLiteDatabase database = db.getWritableDatabase();

            int correct = answer.isCorrect()? 1:0;
            ContentValues dataValues = new ContentValues();
            dataValues.put("idquestion", this.answer.getIdQuestion());
            dataValues.put("title", answer.getAnswer());
            dataValues.put("correct", correct);

            if (answer.getId() > 0) {
                database.update("ANSWER", dataValues, "_id = ?",
                        new String[]{String.valueOf(answer.getId())});
            } else {
                answer.setId((int) database.insert("ANSWER", null, dataValues));
            }

        }
    }

}
