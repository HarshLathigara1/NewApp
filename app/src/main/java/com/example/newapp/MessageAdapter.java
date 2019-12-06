package com.example.newapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages>mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;


    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;

    }

    @Override
    @NonNull
   public MessageViewHolder onCreateViewHolder( @NonNull ViewGroup parent,int viewType){
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messasge_single_layout,parent,false);
       return new MessageViewHolder(v);


   }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        mAuth = FirebaseAuth.getInstance();
        String currentUerId = mAuth.getCurrentUser().getUid();


        Messages c = mMessageList.get(i);
        String fromUser = c.getFrom();
       // String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(fromUser);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // String name = dataSnapshot.child("name").getValue().toString();
               // String image = dataSnapshot.child("image").getValue().toString();
               // messageViewHolder.txtName.setText(name);
                //Picasso.with(messageViewHolder.circleImageView.getContext()).load(image).placeholder(R.drawable.logo).into(messageViewHolder.circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // if (message_type.equals("text")){
       //  messageViewHolder.messageText.setText(c.getMessage());
       //  messageViewHolder.imageView.setVisibility(View.INVISIBLE);

        //}else
        //{
       //     messageViewHolder.messageText.setVisibility(View.INVISIBLE);
       //     Picasso.with(messageViewHolder.imageView.getContext()).load(c.getMessage()).placeholder(R.drawable.logo).into(messageViewHolder.imageView);
        //}

        if (fromUser.equals(currentUerId)){
            messageViewHolder.messageText.setBackgroundColor(Color.WHITE);
            messageViewHolder.messageText.setTextColor(Color.BLACK);



        }else {
            messageViewHolder.messageText.setBackgroundResource(R.drawable.messasge_text);
            messageViewHolder.messageText.setTextColor(Color.WHITE);

        }
        messageViewHolder.messageText.setText(c.getMessage());
        //messageViewHolder.
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public  class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView circleImageView;
        public TextView txtName;
        public ImageView imageView;
        public MessageViewHolder(View view){
            super(view);

            messageText = (TextView)view.findViewById(R.id.txtMessageChat);
            circleImageView =(CircleImageView)view.findViewById(R.id.messageProfileLayout);
           // txtName = (TextView)view.findViewById(R.id.txtName);
           // imageView = (ImageView)view.findViewById(R.id.messageImage);


        }

    }




}
