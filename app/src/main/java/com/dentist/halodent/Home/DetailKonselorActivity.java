package com.dentist.halodent.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Chat.GroupActivity;
import com.dentist.halodent.Chat.Groups;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.Model.Users;
import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DetailKonselorActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView iv_photo_konselor,iv_photo_dokter;
    private TextView tv_nama,tv_email,tv_nim,tv_angkatan,tv_kelamin,tv_nama_dokter,tv_tidak_ada_jadwal,tv_pengawas;
    private Button btn_chat;
    private CardView cardView;

    private DatabaseReference databaseReferenceGroup,databaseReferenceJadwal,databaseReferenceDokter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Konselors konselors;

    private RecyclerView rv_jadwal;
    private List<Jadwals> jadwalsList;
    private JadwalAdapter jadwalAdapter;
    private ProgressDialog progressDialog;

    private String id;
    private DateFormat dateFormat = new SimpleDateFormat("H:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konselor);
        setActionBar();

        //get data from konselor adapter
        konselors = (Konselors) getIntent().getSerializableExtra("konselor");

        //initialization view
        iv_photo_dokter = findViewById(R.id.iv_photo_detail_dokter);
        iv_photo_konselor = findViewById(R.id.iv_photo_detail_konselor);
        tv_nama = findViewById(R.id.tv_nama_detail_konselor);
        tv_email = findViewById(R.id.tv_email_detail_konselor);
        tv_nim = findViewById(R.id.tv_nim_konselor);
        tv_angkatan = findViewById(R.id.tv_angkatan_konselor);
        tv_kelamin = findViewById(R.id.tv_jenisKel_konselor);
        tv_nama_dokter = findViewById(R.id.tv_nama_dokter);
        tv_tidak_ada_jadwal = findViewById(R.id.tv_tidak_jadwal);
        tv_pengawas = findViewById(R.id.tv_pengawas);
        cardView = findViewById(R.id.card_view_dokter);
        btn_chat = findViewById(R.id.btn_chat);
        rv_jadwal = findViewById(R.id.rv_jadwal);

        jadwalsList = new ArrayList<>();
        rv_jadwal.setLayoutManager(new LinearLayoutManager(this));
        jadwalAdapter = new JadwalAdapter(this, jadwalsList);
        rv_jadwal.setAdapter(jadwalAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan Tunggu..");

        //set data
        tv_nama.setText(konselors.getNama());
        tv_email.setText(konselors.getEmail());
        tv_nim.setText(konselors.getNim());
        tv_angkatan.setText(konselors.getAngkatan());
        tv_kelamin.setText(konselors.getKelamin());

        if(konselors.getPhoto()!=null){
            Glide.with(getApplicationContext())
                    .load(konselors.getPhoto())
                    .placeholder(R.drawable.ic_user)
                    .fitCenter()
                    .into(iv_photo_konselor);
        }else {
            iv_photo_konselor.setImageResource(R.drawable.ic_user);
        }

        //initialization firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReferenceDokter = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        databaseReferenceGroup = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);
        databaseReferenceJadwal = FirebaseDatabase.getInstance().getReference().child(NodeNames.JADWAL);

        //initialization click button
        btn_chat.setOnClickListener(this);
        cardView.setOnClickListener(this);

        //read database
        getKonselorJadwal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chat:
                createGroupChat();
                startActivity(new Intent(DetailKonselorActivity.this, GroupActivity.class));
                break;
            case R.id.card_view_dokter:
                Intent intent = new Intent(DetailKonselorActivity.this, DetailDokterActivity.class);
                intent.putExtra("dokter_id",id);
                startActivity(intent);
                break;
        }
    }

    private void getKonselorJadwal(){
        tv_tidak_ada_jadwal.setVisibility(View.VISIBLE);
        rv_jadwal.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.GONE);
        tv_pengawas.setVisibility(View.GONE);

        Query query = databaseReferenceJadwal.child(konselors.getId()).orderByChild("tanggal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                jadwalsList.clear();
                progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (snapshot.exists()) {
                            tv_tidak_ada_jadwal.setVisibility(View.GONE);
                            rv_jadwal.setVisibility(View.VISIBLE);
                            cardView.setVisibility(View.VISIBLE);
                            tv_pengawas.setVisibility(View.VISIBLE);

                            Jadwals jadwals = ds.getValue(Jadwals.class);
                            setClick(jadwals.getDokter_id(), jadwals.getTanggal(), jadwals.getMulai(), jadwals.getSelesai());
                            jadwalsList.add(jadwals);
                            jadwalAdapter.notifyDataSetChanged();
                        }else {
                            tv_tidak_ada_jadwal.setVisibility(View.VISIBLE);
                            rv_jadwal.setVisibility(View.GONE);
                            cardView.setVisibility(View.GONE);
                            tv_pengawas.setVisibility(View.GONE);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailKonselorActivity.this,getString(R.string.failed_to_read_data , error.getMessage()),Toast.LENGTH_SHORT).show();
                tv_tidak_ada_jadwal.setVisibility(View.VISIBLE);
                rv_jadwal.setVisibility(View.GONE);
            }
        });
    }

    private void setClick(String dokterid, String tanggal, String mulai, String selesai){
        DateFormat df = new SimpleDateFormat("EEE, dd/MM/yyyy, HH:mm");
        String date = df.format(new Date());

        String [] splitString = date.split(", ");
        String dt = splitString[1];
        String time = splitString[2];

        Date m = null;
        Date s = null;
        Date n = null;
        try {
            m = dateFormat.parse(mulai);
            s = dateFormat.parse(selesai);
            n = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeMulai= m.getTime();
        long timeSelesai = s.getTime();
        long timeSekarang = n.getTime();

        Log.d("jadwal tanggal",tanggal);
        Log.d("jadwal tanggal Sekarang",dt);
        Log.d("jadwal sekarang",String.valueOf(timeSekarang));
        Log.d("jadwal mulai",String.valueOf(timeMulai));
        Log.d("jadwal selesai",String.valueOf(timeSelesai));
        Log.d("jadwal dokter",dokterid);

        if(tanggal.equals(dt)){
            cardView.setVisibility(View.VISIBLE);
            tv_pengawas.setVisibility(View.VISIBLE);
            //set name for doctor id
            getDataDokter(dokterid);
            //set id into id
            id = dokterid;
            if(timeSekarang>=timeMulai && timeSelesai>=timeSekarang){
                btn_chat.setEnabled(timeSekarang>=timeMulai && timeSelesai>=timeSekarang);
            }else{
                btn_chat.setEnabled(false);
            }
        }else {
            cardView.setVisibility(View.GONE);
            tv_pengawas.setVisibility(View.GONE);
            btn_chat.setEnabled(false);
        }
    }

    private void getDataDokter(String id){
        databaseReferenceDokter.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters = snapshot.getValue(Dokters.class);
                    Preference.setKeyDokter(getApplicationContext(),id,dokters.getNama(),dokters.getEmail(),dokters.getPhoto(),dokters.getNip(),dokters.getStr(),dokters.getSip());

                    Glide.with(getApplicationContext())
                            .load(dokters.getPhoto())
                            .placeholder(R.drawable.ic_user)
                            .centerCrop()
                            .into(iv_photo_dokter);

                    tv_nama_dokter.setText("Drg."+ dokters.getNama());
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

    private void createGroupChat(){
        String timestamp = ""+System.currentTimeMillis();

        Groups group = new Groups(timestamp,"Group","",timestamp,"Berlangsung");

        Preference.setKeyGroupId(getApplicationContext(),group.getGroupId());
        Preference.setKeyGroupName(getApplicationContext(),group.getGroupTitle());

        databaseReferenceGroup.child(timestamp).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Users pasien = new Users(currentUser.getUid(),timestamp,"Pasien");
                Users kons = new Users(konselors.getId(),timestamp,"Konselor");
                Users dokter = new Users(id,timestamp,"Dokter Pengawas");

                HashMap partisipant = new HashMap();
                partisipant.put(currentUser.getUid(),pasien);
                partisipant.put(konselors.getId(),kons);
                partisipant.put(id,dokter);

                databaseReferenceGroup.child(timestamp).child("Participants").setValue(partisipant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(DetailKonselorActivity.this,"Berhasil ditambahkan",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(DetailKonselorActivity.this,"Gagal ditambahkan",Toast.LENGTH_SHORT).show();
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


    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(" ");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions());
        }
    }

    // this event will enable the back , function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}