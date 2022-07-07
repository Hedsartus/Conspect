package com.filenko.conspect.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Answer;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerSwipeAdapter<QuestionAdapter.ViewHolder> {
    private final DataBaseConnection db;
    private final Context ctx;
    private final LayoutInflater lInflater;
    private final ArrayList<Question> objects = new ArrayList<>();
    private final int idNote;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Question question;
        final EditText questionTitle;
        final RecyclerView recyclerViewSection;
        final ToggleButton toggleButton;
        final ImageView buttondelete;
        final ImageButton btnSaveQuestion;
        final LinearLayout linearLayout;
        final TextView teCountAnswers;
        final SwipeLayout layoutQuestionItem;
        final Button addNewAnswer;

        ViewHolder(View view){
            super(view);
            questionTitle = view.findViewById(R.id.item_question_title);
            btnSaveQuestion = view.findViewById(R.id.btnSaveQuestion);
            teCountAnswers = view.findViewById(R.id.teCountAnswers);
            recyclerViewSection = view.findViewById(R.id.rvAnswers);
            toggleButton = view.findViewById(R.id.btnSetViewPanelAnswer);
            buttondelete = view.findViewById(R.id.buttonDeleteQuestion);
            addNewAnswer = view.findViewById(R.id.addNewAnswer);
            layoutQuestionItem = view.findViewById(R.id.layoutQuestionItem);
            linearLayout = view.findViewById(R.id.layoutRecyclerList);
            linearLayout.setVisibility(View.GONE);

            toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                }
            });

            btnSaveQuestion.setOnClickListener(v-> {
                if(this.question!= null) {
                    question.setTitle(questionTitle.getText().toString());
                    saveOrUpdateQuestion (question);
                    btnEnabled(btnSaveQuestion, false);
                }
            });

            addNewAnswer.setOnClickListener(v-> {
                if(this.question.getId() != 0) {
                    AnswerAdapter adapter = (AnswerAdapter) recyclerViewSection.getAdapter();

                    if (this.question.getType() == 2) {
                        if (this.question.getListAnswers().size() == 0) {
                            adapter.addNewAnswer();
                        } else {
                            Toast toast = Toast.makeText(this.recyclerViewSection.getContext(),
                                    "Нельзя добавить больше одного ответа в тип вопроса - верно или нет!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } else {
                        adapter.addNewAnswer();
                    }

                    this.teCountAnswers.setText("Ответов: " + this.question.getListAnswers().size());
                } else {
                    Toast toast = Toast.makeText(this.recyclerViewSection.getContext(),
                            "Сначала сохраните вопрос!!!", Toast.LENGTH_LONG);
                    toast.show();
                }
            });

            questionTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.length() != 0) {
                        btnEnabled(btnSaveQuestion, true);
                    } else {
                        btnEnabled(btnSaveQuestion, false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }

        public void setQuestion (Question question) {
            this.question = question;
            this.questionTitle.setText(question.getTitle());
            this.teCountAnswers.setText("Ответов: "+this.question.getListAnswers().size());

            if(this.question.getType() == 2) {
                btnEnabled (btnSaveQuestion, false);
            } else {
                btnEnabled (btnSaveQuestion, false);
            }

        }

        private void saveOrUpdateQuestion (Question question) {
            if(checkQuestion(question.getTitle())) {
                SQLiteDatabase database = db.getWritableDatabase();

                ContentValues dataValues = new ContentValues();
                dataValues.put("idnote", question.getIdNote());
                dataValues.put("type", question.getType());
                dataValues.put("title", question.getTitle());

                if (question.getId() > 0) {
                    database.update("QUESTIONS", dataValues, "_id = ?",
                            new String[]{String.valueOf(question.getId())});
                } else {
                    question.setId((int) database.insert("QUESTIONS", null, dataValues));
                }
            } else {
                Toast toast = Toast.makeText(this.recyclerViewSection.getContext(),
                        "Нельзя сохранить пустой вопрос!", Toast.LENGTH_LONG);
                toast.show();
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



    public QuestionAdapter(DataBaseConnection db, Context ctx, int idNote) {
        this.db = db;
        this.ctx = ctx;
        lInflater = LayoutInflater.from(ctx);
        this.idNote = idNote;

        if(this.idNote>0) loadQuestionData(this.idNote);

    }

    private boolean checkQuestion(String questionTitle) {
        return questionTitle.length()>0;
    }

    public void addNewQuestion (int type) {
        Question q = new Question();
        q.setType(type);
        q.setIdNote(this.idNote);
        this.objects.add(q);
        notifyDataSetChanged();
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
                                query.getInt(2),
                                query.getString(3))
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
        holder.setQuestion(question);
        AnswerAdapter answerAdapter = (AnswerAdapter) holder.recyclerViewSection.getAdapter();
        answerAdapter.setQuestion(question);

//        if (position % 2 == 0){
//            holder.layoutQuestionItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        }else {
//            holder.layoutQuestionItem.setBackgroundColor(Color.parseColor("#E9EBED"));
//        }

        holder.buttondelete.setOnClickListener(view -> {
            //mItemManger.removeShownLayouts(holder.layoutQuestionItem);
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layoutQuestionItem;
    }

}
