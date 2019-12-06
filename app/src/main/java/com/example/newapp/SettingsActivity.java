package com.example.newapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
        private DatabaseReference databaseReference;
        private FirebaseUser mCurrentUser;
        private CircleImageView imgProfile;
        private TextView displayName;
        private TextView tStatus;
        private Button btnChangeStatus,btnChangeImage;

        private static final int GALLERY_PICK = 1;
        private StorageReference storageReference;
        private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        displayName = (TextView)findViewById(R.id.txtDisplayName);
        tStatus = (TextView)findViewById(R.id.txtSeen);
        btnChangeStatus = (Button)findViewById(R.id.btnChangeStatus) ;
        btnChangeImage = (Button)findViewById(R.id.btnChangeImage);
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(SettingsActivity.this);

        btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = tStatus.getText().toString();

                Intent sIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                sIntent.putExtra("status_Value",status_value);
                startActivity(sIntent);
            }
        });


        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gallery Opner // Image Cropper //
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent,"SELECT IMAGE"),GALLERY_PICK);

               // CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(SettingsActivity.this);
               // Crop.pickImage(SettingsActivity.this);

            }
        });
            // GET DATA From DATABASE TO YOUR Device//
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        displayName.setText(name);
                        tStatus.setText(status);
                        if (!image.equals("default")) {
                            Picasso.with(SettingsActivity.this).load(image).into(imgProfile);
                        }

                //Glide.with(SettingsActivity.this).load(storageReference).into(imgProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            progressDialog.setMessage("Please Wait");
            progressDialog.setTitle("Adding Your Image");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            final Uri imageUri = data.getData();
            final String current_uid = mCurrentUser.getUid();

            final StorageReference filePath = storageReference.child(current_uid);
           // StorageReference thumb_filePath = storageReference.child("thumb_images").child(current_uid);
           // UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);


            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                         storageReference.child(current_uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 String down = uri.toString();
                                 databaseReference.child("image").setValue(down).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(SettingsActivity.this,"worked",Toast.LENGTH_LONG).show();
                                            }
                                     }
                                 });

                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {

                             }
                         });
                    }

                }
            });



        }
    }


}
