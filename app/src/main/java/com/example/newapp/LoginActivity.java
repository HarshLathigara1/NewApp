package com.example.newapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.AnimatorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Type;

public class LoginActivity extends AppCompatActivity  {

    private EditText edtEmailLog , edtPasswordLog;
    Button btnLogin,btnNewAccount;
    FirebaseAuth mAuth;
    Typeface fontDoIt;
    TextView doIt;
    private Animation fromBottom,fromTop;

    ProgressDialog logInProgress;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Log In");
        btnNewAccount = (Button)findViewById(R.id.btnNewAccount);
        doIt = (TextView)findViewById(R.id.textDoIt);
        fontDoIt = Typeface.createFromAsset(getAssets(),"fonts/AVANT_1.TTF");
        doIt.setTypeface(fontDoIt);
        btnLogin = (Button)findViewById(R.id.mLogin);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        btnLogin.startAnimation(fromBottom);
        edtEmailLog = (EditText)findViewById(R.id.edtEmailLog);
        edtPasswordLog = (EditText)findViewById(R.id.edtPasswordLog);
        btnNewAccount.startAnimation(fromBottom);
       // edtEmailLog.startAnimation(fromTop);
       // edtPasswordLog.startAnimation(fromTop);
        doIt.startAnimation(fromTop);


        logInProgress = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmailLog.getText().toString();
                String password = edtPasswordLog.getText().toString();




                if (!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){
                    logInProgress.setTitle("Loging In");
                    logInProgress.setMessage("Please Wait While We Login");
                    logInProgress.setCanceledOnTouchOutside(false);
                    logInProgress.show();
                    loginUser(email,password);
                }else{
                    Toast.makeText(LoginActivity.this,"DO It Again",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newAccount = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(newAccount);
            }
        });

    }



    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){

                  logInProgress.dismiss();
                  String current_user_id = mAuth.getCurrentUser().getUid();
                  String device_token = FirebaseInstanceId.getInstance().getToken();
                  databaseReference.child(current_user_id).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          Intent logIntent = new Intent(getApplicationContext(),MainActivity.class);
                          startActivity(logIntent);

                      }
                  });


              }else {
                  logInProgress.hide();
                  Toast.makeText(getApplicationContext(),"Please Login Again",Toast.LENGTH_LONG).show();
              }
            }
        });




    }
}
