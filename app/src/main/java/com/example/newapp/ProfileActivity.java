package com.example.newapp;

import android.content.Intent;
import android.content.SyncRequest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.nio.file.ProviderNotFoundException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView DisplayNameIntent,statusIntent,totslCountIntent;
    ImageView imgProfileIntent;
    Button btnSendFriendRequest,btnDeclineFriendRequest;
    private DatabaseReference databaseReferenceIntent;
    private DatabaseReference mFriendRequstDatabase;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUserAuth;
    private DatabaseReference mRootRef;
    private CircleImageView circleImageView;
    private Animation fromTop;
  //  private FirebaseAuth mOnlineAuth;
   // private DatabaseReference mOnlineRef;


    private String mCurrentState ;
    private DatabaseReference mNotificationDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReferenceIntent = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequstDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUserAuth = FirebaseAuth.getInstance().getCurrentUser();
        DisplayNameIntent = (TextView)findViewById(R.id.displayNameProfile);
        //imgProfileIntent = (ImageView)findViewById(R.id.imgIntentProfile);
        statusIntent = (TextView)findViewById(R.id.statusIntentProfile);
        totslCountIntent = (TextView)findViewById(R.id.txtTotalFriendsIntent);
        btnDeclineFriendRequest = (Button)findViewById(R.id.btnDeclineFriendRequest);
        btnSendFriendRequest = (Button)findViewById(R.id.btnIntentProfile);
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        fromTop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        circleImageView = (CircleImageView)findViewById(R.id.imgIntentProfile);
        circleImageView.startAnimation(fromTop);
        btnDeclineFriendRequest.setEnabled(false);
        btnDeclineFriendRequest.setVisibility(View.INVISIBLE    );
        mCurrentState = "not_friends";

        databaseReferenceIntent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     String display_name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();




                    DisplayNameIntent.setText(display_name);
                    statusIntent.setText(status);
                    Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.logo).into(circleImageView);
                //-------- FriendList / Request Feature

                    mFriendRequstDatabase.child(mCurrentUserAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Bundle newBundle = new Bundle();
                                newBundle.putString("user_id", user_id);
                                RequestFragment requestFragment = new RequestFragment();
                                requestFragment.setArguments(newBundle);


                                if (dataSnapshot.hasChild(user_id)){
                                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                                    if (req_type.equals("received")){
                                        mCurrentState = "req_received";
                                        btnSendFriendRequest.setText("Accept Friend Request");
                                        btnDeclineFriendRequest.setVisibility(View.VISIBLE);
                                        btnDeclineFriendRequest.setEnabled(true);

                                    }else if (req_type.equals("sent"))
                                    {
                                            mCurrentState = "Req_Sent";
                                            btnSendFriendRequest.setText("Cancel Friend Request");
                                            btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                            btnDeclineFriendRequest.setEnabled(false);
                                    }
                                }
                                else{
                                        mFriendDatabase.child(mCurrentUserAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(user_id)){
                                                        mCurrentState = "friends";
                                                        btnSendFriendRequest.setText("Unfriend This Person");
                                                        btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                        btnDeclineFriendRequest.setEnabled(false);
                                                       // String friendName = dataSnapshot.child("name").getValue().toString();
                                                       // Intent sendToFriends = new Intent(ProfileActivity.this,FriendsFragment.class);
                                                        //sendToFriends.putExtra("friendName",friendName);
                                                        //startActivity(sendToFriends);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSendFriendRequest.setEnabled(false);
                // NOT FRIENDS STATE //
                if (mCurrentState.equals("not_friends")){
                    DatabaseReference newNotificationRef =mRootRef.child("notifications").child(user_id).push();
                    String newNotification_id = newNotificationRef.getKey();
                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUserAuth.getUid());
                    notificationData.put("type","request");
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Req/"+ mCurrentUserAuth.getUid() + "/" + user_id + "/request_type","sent");
                    requestMap.put("Friend_Req/"+ user_id + "/" + mCurrentUserAuth.getUid() + "/request_type","received");
                    requestMap.put("notifications/" +user_id +"/" + newNotification_id , notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError!= null){
                               Toast.makeText(ProfileActivity.this,"Erroe In Sending Request",Toast.LENGTH_LONG).show();

                            }
                            mCurrentState = "Req_sent";
                            btnSendFriendRequest.setEnabled(true);
                            btnSendFriendRequest.setText("Cancel Friend Request");



                        }
                    });


                }

                // CANCEL REQUEST STATE //
                if (mCurrentState.equals("Req_Sent")){
                    mFriendRequstDatabase.child(mCurrentUserAuth.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequstDatabase.child(user_id).child(mCurrentUserAuth.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mNotificationDatabase.child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(ProfileActivity.this,"Notification Deleted",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    btnSendFriendRequest.setEnabled(true);
                                    mCurrentState = "not_friends";
                                    btnSendFriendRequest.setText("send Friend Request");
                                    btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                    btnDeclineFriendRequest.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                //----Request Recived State-----//
                if (mCurrentState.equals("req_received")){
                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + mCurrentUserAuth.getUid() + "/" + user_id   + "/state","friends");
                    friendMap.put("Friends/" +   user_id + "/" + mCurrentUserAuth.getUid()  + "/state","friends");

                    friendMap.put("Friend_Req/" + mCurrentUserAuth.getUid() + "/" + user_id ,null );
                    friendMap.put("Friend_Req/" + user_id + "/" + mCurrentUserAuth.getUid() ,null );

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){
                                btnSendFriendRequest.setEnabled(true);
                                mCurrentState = "friends";
                                btnSendFriendRequest.setText("UnFriend This Person");
                                btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btnDeclineFriendRequest.setEnabled(false);
                            }else{
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();
                            }


                        }
                    });

                }

                //UnFriends--//
                if (mCurrentState.equals("friends")){
                    Map unfriend = new HashMap();
                    unfriend.put("Friends/" + mCurrentUserAuth.getUid() + "/" + user_id, null);
                    unfriend.put("Friends/" + user_id + "/" + mCurrentUserAuth.getUid(), null);
                    mRootRef.updateChildren(unfriend, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){

                                mCurrentState = "not_friends";
                                btnSendFriendRequest.setText("Send Friend Request");
                                btnDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                btnDeclineFriendRequest.setEnabled(false);


                            }else{
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_LONG).show();
                            }


                             btnSendFriendRequest.setEnabled(true);

                        }

                    });
                    btnSendFriendRequest.setEnabled(true);

                }





            }
        });




    }




}
