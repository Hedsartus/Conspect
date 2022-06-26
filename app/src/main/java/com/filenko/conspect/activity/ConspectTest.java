package com.filenko.conspect.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;
import com.filenko.conspect.essence.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConspectTest extends AppCompatActivity {
    private DataBaseConnection db;
    private List<Question> question = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_answers);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }
}
