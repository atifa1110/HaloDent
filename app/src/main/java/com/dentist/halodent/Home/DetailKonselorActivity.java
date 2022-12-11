package com.dentist.halodent.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.dentist.halodent.Group.GroupActivity;
import com.dentist.halodent.Model.Groups;
import com.dentist.halodent.Model.Dokters;
import com.dentist.halodent.Model.Jadwals;
import com.dentist.halodent.Model.Konselors;
import com.dentist.halodent.Utils.Preference;
import com.dentist.halodent.Model.Users;
import com.dentist.halodent.R;
import com.dentist.halodent.Utils.NodeNames;
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

    private static final String TAG = "DetailActivity";
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
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");

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
        tv_tidak_ada_jadwal.setVisibility(View.VISIBLE);
        getKonselorJadwal();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getKonselorJadwal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_chat:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Apakah anda ingin berkonsultasi?")
                        .setCancelable(false)
                        .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createGroupChat();
                                startActivity(new Intent(DetailKonselorActivity.this, GroupActivity.class));
                            }
                        }).setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.card_view_dokter:
                Intent intent = new Intent(DetailKonselorActivity.this, DetailDokterActivity.class);
                intent.putExtra("dokterId",id);
                startActivity(intent);
                break;
        }
    }

    private void getKonselorJadwal(){
        tv_tidak_ada_jadwal.setVisibility(View.GONE);
        rv_jadwal.setVisibility(View.VISIBLE);
        Query query = databaseReferenceJadwal.child(konselors.getId()).orderByChild(NodeNames.JADWAL_TANGGAL);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                jadwalsList.clear();
                if(snapshot.exists()){
                    tv_tidak_ada_jadwal.setVisibility(View.GONE);
                    rv_jadwal.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Jadwals jadwals = ds.getValue(Jadwals.class);
                        jadwalsList.add(jadwals);
                        setJadwalAvailable();
                        jadwalAdapter.notifyDataSetChanged();
                    }
                }else{
                    tv_tidak_ada_jadwal.setVisibility(View.VISIBLE);
                    rv_jadwal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailKonselorActivity.this,getString(R.string.failed_to_read_data , error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setJadwalAvailable(){
        for (int i=0; i<jadwalsList.size(); i++){
            String dokter = jadwalsList.get(i).getDokterId();
            String tanggal = jadwalsList.get(i).getTanggal();
            String mulai = jadwalsList.get(i).getMulai();
            String selesai = jadwalsList.get(i).getSelesai();

            Log.d(TAG,dokter);
            Log.d(TAG,tanggal);
            Log.d(TAG,mulai);
            Log.d(TAG,selesai);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
            String date = df.format(new Date());

            String [] splitString = date.split(", ");
            String dt = splitString[0];
            String time = splitString[1];

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

            long timeMulai = m.getTime();
            long timeSelesai = s.getTime();
            long timeSekarang = n.getTime();

            if(tanggal.equals(dt)){
                //set name for doctor id
                getDataDokter(dokter);
                //set id into id
                id = dokter;
                if(timeSekarang>=timeMulai && timeSelesai>=timeSekarang){
                    tv_pengawas.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.VISIBLE);
                    btn_chat.setEnabled(timeSekarang>=timeMulai && timeSelesai>=timeSekarang);
                }else{
                    tv_pengawas.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                    btn_chat.setEnabled(false);
                }
            }else{
                tv_pengawas.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                btn_chat.setEnabled(false);
            }
        }
    }

    private void getDataDokter(String id){
        databaseReferenceDokter.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters = snapshot.getValue(Dokters.class);
                    try{
                        Preference.setKeyDokter(getApplicationContext(),id,dokters.getNama(),dokters.getEmail(),dokters.getPhoto(),dokters.getNip(),dokters.getStr(),dokters.getSip());
                        Glide.with(getApplicationContext())
                                .load(dokters.getPhoto())
                                .placeholder(R.drawable.ic_user)
                                .centerCrop()
                                .into(iv_photo_dokter);
                        tv_nama_dokter.setText("Drg."+ dokters.getNama());
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(DetailKonselorActivity.this,"Data Dokter Kosong",Toast.LENGTH_SHORT).show();
                    }

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

                databaseReferenceGroup.child(timestamp).child(NodeNames.PARTICIPANTS).setValue(partisipant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(DetailKonselorActivity.this,getString(R.string.failed_to_add_participant,task.getException()),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(DetailKonselorActivity.this,getString(R.string.failed_to_add_group,e.getMessage()),Toast.LENGTH_SHORT).show();
                finish();
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