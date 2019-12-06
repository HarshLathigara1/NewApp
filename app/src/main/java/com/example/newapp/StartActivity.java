package com.example.newapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import de.hdodenhof.circleimageview.CircleImageView;

public class StartActivity extends AppCompatActivity  {
        private Button signUp,logIn,btnForget;
        //VideoView videoView;
        private CircleImageView btnPhoneLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().setTitle("DoIt");
        //videoView = (VideoView)findViewById(R.id.videoView2);

        signUp = (Button)findViewById(R.id.btnRegister);
        logIn = (Button)findViewById(R.id.buttonLogin);
        btnPhoneLogin = (CircleImageView) findViewById(R.id.circleImageView);
        btnForget = findViewById(R.id.btnForget);

       // Uri uri = Uri.parse()
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rIntent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(rIntent);

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lIntent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(lIntent);

            }
        });

        btnPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(StartActivity.this,PhoneLoginActivity.class);
                startActivity(phoneIntent);
                finish();
            }
        });

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgerIntent = new Intent(StartActivity.this,ForgetPasswordActivity.class);
                startActivity(forgerIntent);
            }
        });




    }







}
