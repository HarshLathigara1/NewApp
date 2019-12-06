package com.example.newapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String friendid = getIntent().getStringExtra("list_user_id");
        textView = (TextView)findViewById(R.id.txId);
        textView.setText(friendid);
    }
}
