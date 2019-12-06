package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {
    private RecyclerView reqList;
    private DatabaseReference friend_Req;
    private DatabaseReference userDatabesRef;
    private DatabaseReference mConvDatabase;
    private FirebaseAuth mAuth;
    private String currenUser_id;
    private View mMainView;
    private String currentState;

    public RequestFragment(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*if (getArguments() != null){
            String new_user_id = getArguments().getString("user_id");
            Log.d("TAG",new_user_id);
        }*/
        mMainView = inflater.inflate(R.layout.request_fragment,container,false);
        reqList = (RecyclerView)mMainView.findViewById(R.id.request_flist);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {

            reqList.setEnabled(false);
        }else {
            currenUser_id = mAuth.getCurrentUser().getUid();
            friend_Req = FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(currenUser_id);
        }

       // mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req").child(currenUser_id);
        userDatabesRef = FirebaseDatabase.getInstance().getReference().child("Users");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        reqList.setHasFixedSize(true);
        reqList.setLayoutManager(linearLayoutManager);
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            reqList.setEnabled(false);
        }else {
            Query reqQuery = friend_Req.orderByChild("request_type").equalTo("received");
            FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                    Request.class,
                    R.layout.request_layout,
                    RequestViewHolder.class,
                    reqQuery

            ) {
                @Override
                protected void populateViewHolder(final RequestViewHolder viewHolder, Request model, int position) {
                    final String list_user_id = getRef(position).getKey();
                    //Query lastReqst= friend_Req.child(list_user_id).limitToLast(1);
                    userDatabesRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            viewHolder.setName(name);
                            viewHolder.setImage(image, getContext());
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent newIntent = new Intent(getContext(), ProfileActivity.class);
                                    newIntent.putExtra("user_id", list_user_id);
                                    startActivity(newIntent);

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            };
            reqList.setAdapter(firebaseRecyclerAdapter);
        }

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;



        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        private void setName(String name){
            TextView userName = (TextView)mView.findViewById(R.id.txtReqName);
            userName.setText(name);

        }

        private void setImage(String image, Context ctx){
            CircleImageView reqImage = (CircleImageView)mView.findViewById(R.id.requestProfile);
            Picasso.with(ctx).load(image).placeholder(R.drawable.logo).into(reqImage);

        }



    }
}
