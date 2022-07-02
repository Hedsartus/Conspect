package com.filenko.conspect.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.filenko.conspect.R;
import com.filenko.conspect.adapters.QuestionAdapter;
import com.filenko.conspect.db.DataBaseConnection;

public class EditQuestion extends AppCompatActivity {
    private RecyclerView listViewQuestion;
    private QuestionAdapter adapter;
    private int idNote;
    private SwipeLayout sample1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Вопросы");
        DataBaseConnection db = new DataBaseConnection(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!= null && bundle.getInt("idnote") > 0) {
            idNote = bundle.getInt("idnote");
        }


        this.listViewQuestion = findViewById(R.id.rvQuestions);
        this.listViewQuestion.setLayoutManager(new LinearLayoutManager(this));
        this.listViewQuestion.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        this.adapter = new QuestionAdapter(db,this, idNote);
        this.listViewQuestion.setAdapter(this.adapter);


        /*
        listViewQuestion.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    clickOnListViewQuestion (position);
                    break;
                case 1:
                    // delete
                    break;
            }
            // false : close the menu; true : not close the menu
            return false;
        });*/

    }

    private void clickOnListViewQuestion(int position) {
        Intent intent = new Intent(this, EditAnswers.class);
        Bundle b = new Bundle();
        b.putInt("idquestion", (int) this.adapter.getItemId(position));
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "+").setShowAsAction(1);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1 :
                this.adapter.addNewQuestion();
            case 2 :

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
