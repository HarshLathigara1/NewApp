package com.example.newapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
        EditText edtDisplayName,edtUserName,edtPassword;
         Button btnSignUp;
        FirebaseAuth mAuth;
        ProgressDialog progressDialog;
        private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        edtDisplayName = (EditText)findViewById(R.id.edtDisplayName);
        edtUserName = (EditText)findViewById(R.id.edtEmailLog);
        edtPassword = (EditText)findViewById(R.id.edtPasswordLog);
        btnSignUp = (Button)findViewById(R.id.btnRegister);

        // Register User //
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String displayName = edtDisplayName.getText().toString();
                String email = edtUserName.getText().toString();
                String password = edtPassword.getText().toString();


                if (!TextUtils.isEmpty(displayName)||!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please Wait while We Create Your Account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    registerUser(displayName,email,password);
                }


            }
        });

        progressDialog = new ProgressDialog(this);
    }

            // Register User Firebase //
    private void registerUser(final String displayName, String email , String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser current_User = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_User.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,String>userMap = new HashMap<>();
                    userMap.put("name",displayName);
                    userMap.put("status","Hi There i m Using ChatApp");
                    userMap.put("image","Default_Image");
                    userMap.put("thumb_image","default");
                    userMap.put("device_token",device_token);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                     progressDialog.dismiss();

                                    Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                            }

                        }
                    });

                }else{
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_LONG).show();

                }
            }
        });



    }


}
