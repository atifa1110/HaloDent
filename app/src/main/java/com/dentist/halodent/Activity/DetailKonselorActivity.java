package com.dentist.halodent.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Chat.GroupActivity;
import com.dentist.halodent.Chat.GroupModel;
import com.dentist.halodent.Model.DokterModel;
import com.dentist.halodent.Model.Extras;
import com.dentist.halodent.Model.KonselorModel;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.Model.UserModel;
import com.dentist.halodent.Profile.EditProfileActivity;
import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DetailKonselorActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btn_back;
    private ImageView  iv_photo_konselor,iv_photo_dokter;
    private TextView tv_nama , tv_email , tv_nim , tv_angkatan, tv_kelamin ,tv_nama_dokter;
    private Button btn_chat;
    private CardView card_dokter;
    private Chip chip_jadwal;

    private String dokterId,dokterNama,dokterEmail,dokterPhoto,dokterNip,dokterStr,dokterSip,mulai,selesai;
    private DatabaseReference databaseReferenceDokter,databaseReferenceGroup,databaseReferenceJadwal;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private KonselorModel konselor;

    DateFormat dateFormat = new SimpleDateFormat("H:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konselor);

        konselor = (KonselorModel) getIntent().getSerializableExtra(Extras.KONSELOR);

        //initialization view
        iv_photo_dokter = findViewById(R.id.iv_photo_detail_dokter);
        iv_photo_konselor = findViewById(R.id.iv_photo_detail_konselor);
        tv_nama = findViewById(R.id.tv_nama_detail_konselor);
        tv_email = findViewById(R.id.tv_email_detail_konselor);
        tv_nim = findViewById(R.id.tv_nim_konselor);
        tv_angkatan = findViewById(R.id.tv_angkatan_konselor);
        tv_kelamin = findViewById(R.id.tv_jenisKel_konselor);
        tv_nama_dokter= findViewById(R.id.tv_nama_dokter);
        card_dokter = findViewById(R.id.card_view_dokter);
        chip_jadwal= findViewById(R.id.chip_jadwal);
        btn_chat = findViewById(R.id.btn_chat);
        //btn_back = findViewById(R.id.btn_back_detail_konselor);

        if(konselor.getPhoto()!=null){
            Glide.with(getApplicationContext())
                    .load(konselor.getPhoto())
                    .placeholder(R.drawable.ic_user)
                    .fitCenter()
                    .into(iv_photo_konselor);
        }else {
            iv_photo_konselor.setImageResource(R.drawable.ic_user);
        }

        tv_nama.setText(konselor.getNama());
        tv_email.setText(konselor.getEmail());
        tv_nim.setText(konselor.getNim());
        tv_angkatan.setText(konselor.getAngkatan());
        tv_kelamin.setText(konselor.getKelamin());

        //initialization firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReferenceDokter = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        databaseReferenceGroup = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);
        databaseReferenceJadwal = FirebaseDatabase.getInstance().getReference().child(NodeNames.JADWAL);

        //initialization click button
        btn_chat.setOnClickListener(this);
        //btn_back.setOnClickListener(this);
        card_dokter.setOnClickListener(this);

        //read database
        readKonselorJadwal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chat:
                createGroupChat();
                Intent group = new Intent(DetailKonselorActivity.this, GroupActivity.class);
                startActivity(group);
                break;
            case R.id.card_view_dokter:
                showPopUp();
                break;
        }
    }

    private void setButtonClick(long timeMulai, long timeSelesai){
        String timenow = dateFormat.format(Calendar.getInstance().getTime());

        Date d = null;
        try {
            d = dateFormat.parse(timenow);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long now = d.getTime();
        Log.d("jam_now",String.valueOf(now));

        if(now>=timeMulai && timeSelesai>=now){
            btn_chat.setEnabled(now>=timeMulai && timeSelesai>=now);
        }else{
            btn_chat.setEnabled(false);
        }
    }

    private void showPopUp(){
        DetailDokterFragment.display(getSupportFragmentManager());
    }

    private void createGroupChat(){
        String timestamp = ""+System.currentTimeMillis();

        GroupModel group = new GroupModel(timestamp,"Group","",timestamp,"");

        Preference.setKeyGroupId(getApplicationContext(),group.getGroupId());
        Preference.setKeyGroupName(getApplicationContext(),group.getGroupTitle());

        databaseReferenceGroup.child(timestamp).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                UserModel pasien = new UserModel(currentUser.getUid(),timestamp,"Pasien");
                UserModel kons = new UserModel(konselor.getId(),timestamp,"Konselor");
                UserModel dokter = new UserModel(dokterId,timestamp,"Dokter Pengawas");

                HashMap partisipant = new HashMap();
                partisipant.put(currentUser.getUid(),pasien);
                partisipant.put(konselor.getId(),kons);
                partisipant.put(dokterId,dokter);

                databaseReferenceGroup.child(timestamp).child("Participants").setValue(partisipant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(DetailKonselorActivity.this,"Partisipan Berhasil ditambahkan",Toast.LENGTH_SHORT).show();
                        }else{
                            //Toast.makeText(DetailKonselorActivity.this,"Partisipan Gagal ditambahkan",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(DetailKonselorActivity.this,"Gagal membuat group",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readKonselorJadwal(){
        databaseReferenceJadwal.child(konselor.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dokterId = snapshot.child(NodeNames.DOKTER_ID).getValue().toString();
                    Log.d("dokter_id",dokterId);
                    mulai = snapshot.child(NodeNames.WAKTU_MULAI).getValue().toString();
                    selesai = snapshot.child(NodeNames.WAKTU_SELESAI).getValue().toString();
                    chip_jadwal.setText(mulai+" - "+selesai);

                    Date m = null;
                    Date s = null;
                    try {
                        m = dateFormat.parse(mulai);
                        s = dateFormat.parse(selesai);
                        Log.d("jam_mulai",String.valueOf(m.getTime()));
                        Log.d("jam_selesai",String.valueOf(s.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    setButtonClick(m.getTime(),s.getTime());
                    readDokterDatabase(dokterId);
                }else{
                    Toast.makeText(DetailKonselorActivity.this,"Data not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailKonselorActivity.this,getString(R.string.failed_to_read_data , error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readDokterDatabase(String dokterId){
        databaseReferenceDokter.child(dokterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dokterNama = snapshot.child(NodeNames.NAME).getValue().toString();
                    dokterEmail = snapshot.child(NodeNames.EMAIL).getValue().toString();
                    dokterPhoto = snapshot.child(NodeNames.PHOTO).getValue().toString();
                    dokterNip = snapshot.child(NodeNames.NIP).getValue().toString();
                    dokterStr = snapshot.child(NodeNames.STR).getValue().toString();
                    dokterSip = snapshot.child(NodeNames.SIP).getValue().toString();

                    Preference.setKeyDokter(getApplicationContext(),dokterId,dokterNama,dokterEmail,dokterPhoto,dokterNip,dokterStr,dokterSip);

                    Glide.with(getApplicationContext())
                            .load(dokterPhoto)
                            .placeholder(R.drawable.ic_user)
                            .centerCrop()
                            .into(iv_photo_dokter);

                    tv_nama_dokter.setText("Drg."+ dokterNama);

                }else{
                    Toast.makeText(DetailKonselorActivity.this,"Data not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailKonselorActivity.this,
                        getString(R.string.failed_to_update, error.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}