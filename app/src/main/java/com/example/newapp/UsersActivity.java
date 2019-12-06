package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView userListRecycler;
    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        getSupportActionBar().setTitle("All User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        userListRecycler = (RecyclerView)findViewById(R.id.userList);
        userListRecycler.setHasFixedSize(true);
        userListRecycler.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User,UserViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.users_single_layout,
                UserViewHolder.class,
                mUserDatabase


        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                  viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage(),UsersActivity.this);
                final String user_id = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        userListRecycler.setAdapter(firebaseRecyclerAdapter);

    }

    private void sendToStart(){

        Intent sIntent = new Intent(UsersActivity.this,StartActivity.class);
        startActivity(sIntent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if (item.getItemId() == R.id.account_settings){

            Intent settingIntent = new Intent(UsersActivity.this,SettingsActivity.class);

            startActivity(settingIntent);
        }


        return true;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView mUserName = (TextView) mView.findViewById(R.id.userName);
            mUserName.setText(name);
        }

        public void setStatus(String status){
            TextView mUserStatus = (TextView)mView.findViewById(R.id.userStatus);
            mUserStatus.setText(status);
        }

        public void setImage(String image, Context ctx){
            CircleImageView userImage = (CircleImageView)mView.findViewById(R.id.profile_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.logo).into(userImage);
        }
    }
}
