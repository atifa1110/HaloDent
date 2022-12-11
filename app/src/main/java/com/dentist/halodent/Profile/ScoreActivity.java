package com.dentist.halodent.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dentist.halodent.Model.Karies;
import com.dentist.halodent.Utils.NodeNames;
import com.dentist.halodent.Utils.Preference;
import com.dentist.halodent.R;
import com.dentist.halodent.SignIn.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ScoreActivity extends AppCompatActivity {

    private TextView cara_jaga_karies,tv_nilai,tv_kategori;
    private Button btn_lanjutkan;
    private int score=0;
    private int umur= 0;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    private DatabaseReference databaseReferenceSurvey = FirebaseDatabase.getInstance().getReference().child(NodeNames.SURVEY).child(currentUser.getUid()).child("karies");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        btn_lanjutkan = findViewById(R.id.button_lanjutkan);
        tv_kategori = findViewById(R.id.tv_kategori);
        tv_nilai = findViewById(R.id.tv_nilai);
        cara_jaga_karies = findViewById(R.id.cara_jaga);

        btn_lanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //receive score through intent
        score = Preference.getKeyQuizScore(getApplicationContext());
        umur = Preference.getKeyUserAge(getApplicationContext());

        getDataKaries();
    }

    private void getDataKaries(){
        databaseReferenceSurvey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Karies karies = snapshot.getValue(Karies.class);
                    try{
                        tv_kategori.setText(karies.getKategori());
                        tv_nilai.setText(karies.getScore()+"/20");
                        getKariesScore(karies.getScore());
                    }catch (Exception e){
                        tv_kategori.setText(" ");
                        tv_nilai.setText(" ");
                        getKariesScore(0);
                    }
                }else{
                    getKariesScore(score);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadSurveyDatabase(String kategori, int score){
        Karies karies = new Karies(kategori,score);
        databaseReferenceSurvey.setValue(karies).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("berhasil","berhasil di upload");
                }else{
                    Log.d("gagal","gagal di upload");
                }
            }
        });
    }

    private void getKariesScore(int score){
        if(umur<6){
            getKariesAnak(score);
        }else{
            getKariesDewasa(score);
        }
    }

    private void getKariesDewasa (int score){
        tv_nilai.setText(score+"/20");
        if(score>=8 && score<=12){
            tv_kategori.setText(R.string.karies_rendah);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_rendah);
        }else if(score>=13 && score<=16){
            tv_kategori.setText(R.string.karies_sedang);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_sedang);
        }else if(score>=17 && score<=20){
            tv_kategori.setText(R.string.karies_tinggi);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_tinggi);
        }
        uploadSurveyDatabase(tv_kategori.getText().toString(),score);
    }
    private void getKariesAnak(int score){
        tv_nilai.setText(score+"/24");
        if(score>=10 && score<=13){
            tv_kategori.setText(R.string.karies_rendah);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_rendah);
        }else if(score>=14 && score<=19){
            tv_kategori.setText(R.string.karies_sedang);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_sedang);
        }else if(score>=20 && score<=24){
            tv_kategori.setText(R.string.karies_tinggi);
            cara_jaga_karies.setText(R.string.cara_jaga_karies_tinggi);
        }
        uploadSurveyDatabase(tv_kategori.getText().toString(),score);
    }

}