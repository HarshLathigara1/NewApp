package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {
    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUSerDatabase;

    private FirebaseAuth mAuth;
    private String mCurrentUser_id;
    private View mMainView;
    private String currentState;

    public ChatsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      mMainView = inflater.inflate(R.layout.chat_frgament,container,false);
      mConvList = (RecyclerView)mMainView.findViewById(R.id.conv_list);
      mAuth = FirebaseAuth.getInstance();
      mCurrentUser_id = mAuth.getCurrentUser().getUid();
      currentState = "not_friends";
      mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUser_id);
      mUSerDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
      mMessageDatabase= FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUser_id);

      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
      linearLayoutManager.setReverseLayout(true);
      linearLayoutManager.setStackFromEnd(true);

      mConvList.setHasFixedSize(true);
      mConvList.setLayoutManager(linearLayoutManager);
      return mMainView;





    }

    @Override
    public void onStart() {
        super.onStart();
        Query conversationQuery  = mConvDatabase.orderByChild("timeStamp");
        FirebaseRecyclerAdapter<Conv ,ConvViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_single_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder viewHolder, final Conv model, int position) {
                final String list_user_id = getRef(position).getKey();
                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data = dataSnapshot.child("message").getValue().toString();
                        viewHolder.setMessage(data,model.isSeen() );

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mUSerDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        viewHolder.setName(userName);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("user_id",list_user_id);
                                chatIntent.putExtra("user_name",userName);
                                startActivity(chatIntent);
                            }
                        });


                    }




                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        mConvList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder{
        View mView;


        public ConvViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setMessage(String message , boolean isSeen){
            TextView userMessage =  (TextView)mView.findViewById(R.id.userStatus);
            userMessage.setText(message);
            if (!isSeen){
               // userMessage.setTypeface( userMessage.getTypeface(),Typeface.BOLD);
                userMessage.setTextColor(Color.BLUE);

            }else{
                userMessage.setTypeface( userMessage.getTypeface(),Typeface.NORMAL);

            }
        }

        public void setName(String name){
            TextView userName = (TextView)mView.findViewById(R.id.userName);
            userName.setText(name);

        }




    }





}
