package com.dentist.halodent.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
import com.dentist.halodent.Chat.ChatFragment;
import com.dentist.halodent.Home.HomeFragment;
import com.dentist.halodent.Info.InfoFragment;
import com.dentist.halodent.Profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottom_nav_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(monNavigationItemSelectedListener);

        loadFragment(new HomeFragment());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS).child(currentUser.getUid());
        //when open the app set status online
        databaseReferenceUsers.child(NodeNames.ONLINE).setValue("Online");
        //when disconnect set status offline
        databaseReferenceUsers.child(NodeNames.ONLINE).onDisconnect().setValue("Offline");

    }

    private BottomNavigationView.OnNavigationItemSelectedListener monNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.menu_home:
                    actionBar.setTitle(R.string.app_name);
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.menu_chat:
                    actionBar.setTitle("Chat");
                    fragment = new ChatFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.menu_info:
                    actionBar.setTitle("Information");
                    fragment = new InfoFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.menu_profile:
                    actionBar.setTitle("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment){
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_main_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}