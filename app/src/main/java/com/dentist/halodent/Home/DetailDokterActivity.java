package com.dentist.halodent.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailDokterActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView tv_nama,tv_email,tv_nip,tv_str,tv_sip,tv_kelamin;
    private String id;
    private DatabaseReference databaseReferenceDokter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dokter);
        setActionBar();

        id = getIntent().getStringExtra("dokter_id");

        imageView = findViewById(R.id.iv_photo_detail_dokter);;
        tv_nama =  findViewById(R.id.tv_nama_detail_dokter);
        tv_email = findViewById(R.id.tv_email_detail_dokter);
        tv_kelamin = findViewById(R.id.tv_kelamin);
        tv_nip = findViewById(R.id.tv_nip);
        tv_str = findViewById(R.id.tv_str);
        tv_sip = findViewById(R.id.tv_sip);

        databaseReferenceDokter = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        getDataDokter(id);
    }

    private void getDataDokter(String id){
        databaseReferenceDokter.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters =  snapshot.getValue(Dokters.class);
                    tv_nama.setText(dokters.getNama());
                    tv_email.setText(dokters.getEmail());
                    tv_kelamin.setText(dokters.getKelamin());
                    tv_nip.setText(dokters.getNip());
                    tv_str.setText(dokters.getStr());
                    tv_sip.setText(dokters.getSip());

                    Glide.with(getApplicationContext())
                                .load(dokters.getPhoto())
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .fitCenter()
                                .into(imageView);
                }else{
                    tv_nama.setText(" ");
                    tv_email.setText(" ");
                    tv_nip.setText(" ");
                    tv_str.setText(" ");
                    tv_sip.setText(" ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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