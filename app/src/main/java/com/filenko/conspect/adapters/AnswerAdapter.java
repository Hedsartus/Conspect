package com.filenko.conspect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.List;

public class AnswerAdapter extends RecyclerSwipeAdapter<AnswerAdapter.ViewHolder> {
    private DataBaseConnection db;
    private Context ctx;
    private LayoutInflater lInflater;
    private List<Answer> objects;
    private Question question;

    public void addNewAnswer() {
        Answer answer = new Answer();
        if(this.question.getType() == 2) {
            answer.setAnswer("Да / Нет");
        }
        answer.setIdQuestion(question.getId());
        this.objects.add(answer);
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Answer answer;
        private SwipeLayout layoutAnswerItem;
        final EditText answerTitle;
        final CheckBox checkBox;
        final ImageView buttondeleteanswer;
        final ImageButton btnSaveAnswer;

        ViewHolder(View view){
            super(view);
            //this.answer = answer;
            layoutAnswerItem = view.findViewById(R.id.layoutAnswerItem);
            checkBox = view.findViewById(R.id.checkboxAnswer);
            answerTitle = view.findViewById(R.id.item_answer_text);
            btnSaveAnswer = view.findViewById(R.id.btnSaveAnswer);
            buttondeleteanswer = view.findViewById(R.id.buttonDeleteAnswer);

            btnSaveAnswer.setOnClickListener(v-> {
                if(this.answer!= null) {
                    answer.setAnswer(answerTitle.getText().toString());
                    answer.setCorrect(checkBox.isChecked());
                    saveOrUpdateAnswer(answer);
                    btnEnabled(btnSaveAnswer, false);
                }
            });

            answerTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    btnEnabled(btnSaveAnswer, s.length() != 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(!answerTitle.getText().toString().equals("")) {
                    btnEnabled(btnSaveAnswer, true);
                } else {
                    btnEnabled(btnSaveAnswer, false);
                }
            });

            btnEnabled(btnSaveAnswer, false);
        }

        public void setAnswer (Answer answer) {
            this.answer = answer;
            if(this.answer!= null) {
                this.answerTitle.setText(this.answer.getAnswer());
                this.checkBox.setChecked(this.answer.isCorrect());
                btnEnabled (btnSaveAnswer, false);
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

    @Override
    public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.lInflater.inflate(R.layout.item_answer, parent, false);
        return new AnswerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswerAdapter.ViewHolder holder, int position) {
        holder.setAnswer(objects.get(position));

        if(this.question.getType() == 2) holder.answerTitle.setEnabled(false);

        holder.buttondeleteanswer.setOnClickListener(view -> {
            //mItemManger.removeShownLayouts(holder.layoutAnswerItem);
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layoutAnswerItem;
    }
}
