package com.example.newapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String mChatUser;
    private DatabaseReference mRootRef;
    private TextView txtName,txtLastSeen;
    private CircleImageView circleImageView;
    private FirebaseAuth mAuth;
    private  String currentUserId;
   // private ImageButton btnAttach;
    private ImageButton btnSendButton;
    private EditText edtChat;
    private RecyclerView messagesList;
    private SwipeRefreshLayout swipeRefreshLayouts;
    //data Recieve//
    private final List<Messages>messageNewList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private static  final int TOTAL_ITEMS_LOAD = 10;
    private  int mCurrentPage = 1;
    private int itemPostion = 0;
     private String lastKey = "";
     private String mPrevKey = "";
     private static  final  int GALLERY_PICK = 1;
     private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(actionbarView);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("user_id");
        String name = getIntent().getStringExtra("user_name");


        txtName = (TextView)findViewById(R.id.txtName);
        txtLastSeen = (TextView)findViewById(R.id.txtSeen);
        circleImageView = (CircleImageView)findViewById(R.id.custombar_Image);
        //btnAttach = (ImageButton)findViewById(R.id.btnAttach);
        btnSendButton = (ImageButton)findViewById(R.id.btnSendMessage);
        edtChat = (EditText)findViewById(R.id.edtChat);
        messageAdapter = new MessageAdapter(messageNewList);
        messagesList = (RecyclerView)findViewById(R.id.messages_List);
        swipeRefreshLayouts = (SwipeRefreshLayout)findViewById(R.id.swipeMessage);
        linearLayoutManager = new LinearLayoutManager(this);
        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(linearLayoutManager);
        messagesList.setAdapter(messageAdapter);
        //---image storage-----//
        mImageStorage = FirebaseStorage.getInstance().getReference();


        loadMessages();


        txtName.setText(name);



        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.logo).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                if (online.equals("true")){
                    txtLastSeen.setText("online");
                }else{
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());
                    txtLastSeen.setText(lastSeenTime);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRootRef.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                   chatUserMap.put("Chat/" + currentUserId + "/" + mChatUser,chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + currentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Log.d("CHAT",databaseError.getMessage().toString());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });
        swipeRefreshLayouts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPostion = 0;
                //messageNewList.clear();
                loadMoreMessages();

            }
        });

       /* btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE ", Gallery_PICK));
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
            }
        });*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri  = data.getData();
            final String currrent_user_ref = "messages/"+ currentUserId + "/" + mChatUser;
            final String chat_USer_Ref = "messages/" + mChatUser + "/" + currentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(currentUserId).child(mChatUser).push();
            final String push_id = user_message_push.getKey();
           // StorageReference filePath = mImageStorage.child("messageImages").child(push_id);
           /* filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this,"Donee",Toast.LENGTH_LONG).show();
                        mImageStorage.child("messageImages").child(push_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String down = uri.toString();
                                Map messageMap = new HashMap();
                                messageMap.put("message",down);
                                messageMap.put("seen",false);
                                messageMap.put("type","image");
                                messageMap.put("time",ServerValue.TIMESTAMP);
                                messageMap.put("from",currentUserId);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(currrent_user_ref + "/" + push_id,messageMap);
                                messageUserMap.put(chat_USer_Ref + "/" + push_id,messageMap);
                                edtChat.setText("");
                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null){
                                            Toast.makeText(ChatActivity.this,databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });






                            }
                        });
                    }

                }
            });*/

        }
    }

    private void loadMoreMessages(){
        DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(mChatUser);
        Query messageQuery  = messageRef.orderByKey().endAt(lastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                String mMessageKey = dataSnapshot.getKey();
                messageNewList.add(itemPostion++,message);
                if (!mPrevKey.equals(mMessageKey)){
                    messageNewList.add(itemPostion++,message);
                }else {
                    mPrevKey = lastKey;
                }
                if (itemPostion ==1){

                    lastKey = mMessageKey;
                   // mPrevKey = mMessageKey;
                }

               // Toast.makeText(ChatActivity.this,)

                messageAdapter.notifyDataSetChanged();

                swipeRefreshLayouts.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(10,0);
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

    }

    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(currentUserId).child(mChatUser);
        Query messageQuery  = messageRef.limitToLast(mCurrentPage *TOTAL_ITEMS_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    itemPostion++;
                    if (itemPostion ==1){
                        String mMessageKey = dataSnapshot.getKey();
                        lastKey = mMessageKey;
                        mPrevKey = mMessageKey;
                    }
                    messageNewList.add(message);
                    messageAdapter.notifyDataSetChanged();
                    messagesList.scrollToPosition(messageNewList.size() -1);
                    swipeRefreshLayouts.setRefreshing(false);
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

    }

    private void sendMessage(){
        String message = edtChat.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String currentUserRef  = "messages/" + currentUserId + "/" + mChatUser;
            String chatUserRef = "messages/" + mChatUser + "/" + currentUserId;

            DatabaseReference userMessagePush = mRootRef.child("messages").child(currentUserId).child(mChatUser).push();
            String pushId = userMessagePush.getKey();

            Map messageMap = new HashMap();
            messageMap.put( "message" ,message);
            messageMap.put( "seen", false);
            messageMap.put( "type", "text");
            messageMap.put( "time",ServerValue.TIMESTAMP);
            messageMap.put("from",currentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/"+ pushId,messageMap);
            messageUserMap.put(chatUserRef + "/" + pushId,messageMap);
            edtChat.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.d("CHAT",databaseError.getMessage().toString());
                    }

                }
            });


        }
    }



}
