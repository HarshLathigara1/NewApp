package com.example.newapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnSend;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getSupportActionBar().setTitle("Set New Passsword");

        edtEmail = findViewById(R.id.edtEmail);
        btnSend = findViewById(R.id.btnSendMePasssword);
        mAuth = FirebaseAuth.getInstance();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(edtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgetPasswordActivity.this,"Password Send To Your Email",Toast.LENGTH_LONG).show();
                        }else
                        {
                            Toast.makeText(ForgetPasswordActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }
}
