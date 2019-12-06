package com.example.newapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText edtPhone,edtverify;
    private Button btnSendVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private FirebaseAuth mAuth;
    private TextView txtError;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private DatabaseReference userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtverify = (EditText)findViewById(R.id.edtVerify);
        txtError = (TextView)findViewById(R.id.txtError);
        btnSendVerificationCode = (Button)findViewById(R.id.btnSend);
        mAuth = FirebaseAuth.getInstance();
        btnSendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPhone.setEnabled(false);
                btnSendVerificationCode.setEnabled(false);

                String phoneNumber = edtPhone.getText().toString();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        PhoneLoginActivity.this,
                        mCallBacks

                );
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                txtError.setText("Try Again");
                txtError.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResendToken = forceResendingToken;
                edtverify.setVisibility(View.VISIBLE);
                btnSendVerificationCode.setText("Verify Code");


            }
        };




    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            final FirebaseUser user = task.getResult().getUser();


                            String userid = user.getUid();


                            String phoneNumber = user.getPhoneNumber();
                            String device_token = FirebaseInstanceId.getInstance().getToken();
                            userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            user.reload();
                            /*HashMap<String,String>userMap = new HashMap<>();
                            userMap.put("name",phoneNumber);
                            userMap.put("status","Hi There i m Using ChatApp");
                            userMap.put("image","Default_Image");
                            userMap.put("thumb_image","default");
                            userMap.put("device_token",device_token);
                            userDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                       Toast.makeText(PhoneLoginActivity.this,"Donee",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });*/
                            Intent phoneIntent = new Intent(PhoneLoginActivity.this,MainActivity.class);
                            startActivity(phoneIntent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            txtError.setText("Try Again");
                            txtError.setVisibility(View.VISIBLE);
                           // edtPhone.setEnabled(true);
                            edtPhone.setVisibility(View.VISIBLE);
                           // btnSendVerificationCode.setEnabled(true);
                            btnSendVerificationCode.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }





}
