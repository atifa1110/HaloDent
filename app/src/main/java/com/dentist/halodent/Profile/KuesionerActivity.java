package com.dentist.halodent.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KuesionerActivity extends AppCompatActivity {

    private Button btn_mulai;
    private Toolbar toolbar;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReferenceSurvey = FirebaseDatabase.getInstance().getReference().child(NodeNames.SURVEY).child(currentUser.getUid()).child("karies");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuesioner);

        btn_mulai = findViewById(R.id.btn_start);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KuesionerActivity.this,MainQuizActivity.class);
                Preference.setKeyButtonClick(getApplicationContext(),true);
                startActivity(intent);
            }
        });

        getDataKaries();
    }

    private void getDataKaries(){
        databaseReferenceSurvey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Preference.setKeyQuizOpen(getApplicationContext(),true);
                } else {
                    Preference.removeQuizData(getApplicationContext());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}