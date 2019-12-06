package com.example.newapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.googlecode.mp4parser.srt.SrtParser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    private RecyclerView friendlistRecycler;
    private DatabaseReference databaseReference;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private String user_id;
    private View mMainView;
    private DatabaseReference mFriendDatabase;
    public static final String LIST_USER_ID = "list_user_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView =  inflater.inflate(R.layout.friends_fragment,container,false);
        friendlistRecycler = (RecyclerView)mMainView.findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        friendlistRecycler.setHasFixedSize(true);
        friendlistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
        //String name =

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsViewHolderAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

               // viewHolder.setName(model.getName());
               // viewHolder.setStatus(model.getStatus());
               // viewHolder.setImage(model.getImage(),getContext());
                //viewHolder.setOnline(model.getOnline());
                final String list_user_id = getRef(position).getKey();
                mUserDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        viewHolder.setName(name);
                        viewHolder.setStatus(status);
                        viewHolder.setImage(image,getContext());
                        if (dataSnapshot.hasChild("online")){
                            String online =  dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online);

                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final CharSequence options[] = new CharSequence[]{"open Profile","Send Message"};

                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");

                                builder.setIcon(R.drawable.ic_send);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                           // builder.setIcon(R.drawable.ic_friends);

                                            Intent newIntent = new Intent(getContext(),ProfileActivity.class);
                                            newIntent.putExtra("user_id",list_user_id);
                                            startActivity(newIntent);

                                        }
                                        if (which == 1){
                                           // builder.setIcon(R.drawable.ic_send);
                                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",name);

                                            startActivity(chatIntent);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




               /*// viewHolder.setName(model.getName());
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());*/


            }
        };
        friendlistRecycler.setAdapter(friendsViewHolderAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;



        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.userName);
            userNameView.setText(name);
        }

        public void setStatus(String status){
            TextView userStatus = (TextView)mView.findViewById(R.id.userStatus);
            userStatus.setText(status);
        }

        public void setImage(String image, Context ctx){
            CircleImageView userImage = (CircleImageView)mView.findViewById(R.id.profile_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.logo).into(userImage);
        }

        public void setUserOnline(String online_status){
            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.imgOnlineDot);
            if (online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }





    }
}
