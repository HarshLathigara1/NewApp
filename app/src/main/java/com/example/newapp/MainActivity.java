package com.example.newapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
        Intent intent;

        private DatabaseReference mUserRef;
        private ViewPager mViewPager;
       // private SectionsPageAdapter sectionsPageAdapter;
       // private TabLayout tabLayout;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Intent intent = new Intent(getApplicationContext(),UsersActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,0);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("App Is Launched")
                    .setContentText("its Cold")
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent).addAction(R.drawable.logo,"Chat",pendingIntent).build();
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1,notification);

           // getSupportActionBar().setTitle("");

            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {

                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }

        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigation);
        mAuth = FirebaseAuth.getInstance();
       // mFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getUid()).child("list");

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new RequestFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigation = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.chats:
                    selectedFragment =new ChatsFragment();
                    break;
                case R.id.friends:
                selectedFragment =new FriendsFragment();
                break;
                case R.id.request:
                selectedFragment =new RequestFragment();
                break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,selectedFragment).commit();
            return true;

        }
    };




    private void sendToStart(){

        Intent sIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(sIntent);
        finish();

    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendToStart();
        }else{
            mUserRef.child("online").setValue("true");
           // mUserRef.child("lastSeen").setPriority(ServerValue.TIMESTAMP);


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null ) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    // Menu //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if (item.getItemId() == R.id.account_settings){

            Intent settingIntent = new Intent(MainActivity.this,SettingsActivity.class);

            startActivity(settingIntent);
        }

        if (item.getItemId() == R.id.allUsers){
            Intent uIntent = new Intent(MainActivity.this,UsersActivity.class);

            startActivity(uIntent);

        }

        if (item.getItemId() == R.id.getWeatheData){
            Intent weatherIntent = new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(weatherIntent);
        }
        return true;
    }
}
