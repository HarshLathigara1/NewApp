package com.example.newapp;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
        EditText edtStatus;
        Button btnSave;
        private DatabaseReference statusRef;
        private FirebaseUser mCurrentUser;

        ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getting old Status Value from Settings //
        String status_value = getIntent().getStringExtra("status_Value");
        edtStatus = (EditText)findViewById(R.id.edtStatus);
        btnSave =  (Button)findViewById(R.id.btnStatus);
        edtStatus.setText(status_value);



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid = mCurrentUser.getUid();
         statusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please Wait");
                mProgress.show();

                String status = edtStatus.getText().toString();
                statusRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext()," Staus Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });





    }
}
