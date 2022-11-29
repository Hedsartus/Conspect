package com.filenko.conspect.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.filenko.conspect.R;
import com.filenko.conspect.db.DataBaseConnection;

public class Settings extends AppCompatActivity {
    private DataBaseConnection db;
    private static final int ACTIVITY_CHOOSE_FILE = 787;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
