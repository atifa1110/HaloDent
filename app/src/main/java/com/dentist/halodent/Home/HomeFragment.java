package com.dentist.halodent.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.KonselorModel;
import com.dentist.halodent.Model.TopikModel;
import com.dentist.halodent.Profile.KuesionerActivity;
import com.dentist.halodent.Activity.ListKonselorActivity;
import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private RecyclerView rvKonselor , rvTopik;
    private KonselorAdapter konselorAdapter;
    private List<KonselorModel> konselorList;

    private TopikAdapter topikAdapter;
    private List<TopikModel> topikModelList;

    private TextView tvNama;
    private ImageView ivProfile , image;
    private Button btnlihatTopik ,btnlihatKonselor,btnLetsgo;
    private View layoutKuesioner;

    private String topik_id,topik_nama,topik_narasi,topik_photo,topik_sumber,topik_time,topik_tipe;
    private String konselor_id,konselor_nama,konselor_email,konselor_photo,konselor_ponsel,konselor_online,konselor_role,konselor_nim,konselor_angkatan,konselor_kelamin;

    private DatabaseReference databaseReferenceKonselor,databaseReferenceTopik;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private Uri serverFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNama = view.findViewById(R.id.tv_nama_home);
        ivProfile = view.findViewById(R.id.iv_profile_home);
        layoutKuesioner = view.findViewById(R.id.ll_belum_isi);
        btnlihatKonselor = view.findViewById(R.id.btn_lihat_semua_konselor);
        image = view.findViewById(R.id.iv_icon_belum_isi);
        image.bringToFront();
        btnLetsgo = view.findViewById(R.id.btn_lets_go);

        rvKonselor = view.findViewById(R.id.rv_available_konselor);
        rvTopik = view.findViewById(R.id.rv_topik_hangat);

        konselorList = new ArrayList<>();
        rvKonselor.setLayoutManager(new LinearLayoutManager(getContext()));
        konselorAdapter = new KonselorAdapter(getContext(),konselorList);
        rvKonselor.setAdapter(konselorAdapter);

        databaseReferenceTopik = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOPIKS);
        databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);
        currentUser  = FirebaseAuth.getInstance().getCurrentUser();

        btnlihatKonselor.setOnClickListener(this);
        btnLetsgo.setOnClickListener(this);

        setData();
        getDataKonselor();
        getDataTopik();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_lihat_semua_konselor:
                Intent semuaKonselor = new Intent(getContext(), ListKonselorActivity.class);
                startActivity(semuaKonselor);
                break;
            case R.id.btn_lets_go:
                Intent kuesioner = new Intent(getContext(), KuesionerActivity.class);
                startActivity(kuesioner);
                break;
        }
    }

    private void setData(){
        if(currentUser!=null){
            tvNama.setText(currentUser.getDisplayName());
            serverFileUri= currentUser.getPhotoUrl();
            if(serverFileUri!=null)
            {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfile);
            }
        }
    }

    private void getDataTopik(){
        topikModelList = new ArrayList<>();
        rvTopik.setLayoutManager(new LinearLayoutManager(getContext()));
        topikAdapter = new TopikAdapter(getContext(), topikModelList);
        rvTopik.setAdapter(topikAdapter);

        Query query = databaseReferenceTopik.orderByChild(NodeNames.TOPIK_TIME).limitToLast(3);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topikModelList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.exists()){
                        topik_id = ds.getKey();
                        topik_nama = ds.child(NodeNames.TOPIK_NAME).getValue().toString();
                        topik_narasi = ds.child(NodeNames.TOPIK_NARASI).getValue().toString();

                        topik_photo= "";
                        if (ds.child(NodeNames.TOPIK_PHOTO).getValue() != null) {
                            topik_photo = ds.child(NodeNames.TOPIK_PHOTO).getValue().toString();
                            Log.d("topik_photo",topik_photo);
                        }else{
                            topik_photo="";
                        }

                        topik_sumber = ds.child(NodeNames.TOPIK_SUMBER).getValue().toString();
                        topik_time = ds.child(NodeNames.TOPIK_TIME).getValue().toString();
                        topik_tipe = ds.child(NodeNames.TOPIK_TYPE).getValue().toString();

                        TopikModel topik = new TopikModel(topik_id,topik_nama,topik_photo,topik_narasi,topik_sumber,topik_time,topik_tipe);
                        topikModelList.add(topik);

                        databaseReferenceTopik.child(topik_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    topikAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                Toast.makeText(getContext(), getContext().getString( R.string.failed_to_read_data, error.getMessage())
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), getContext().getString( R.string.failed_to_read_data, error.getMessage())
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataKonselor(){

        Query query = databaseReferenceKonselor.orderByChild(NodeNames.ONLINE).equalTo("Online");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                konselorList.clear();
                for (final DataSnapshot ds : snapshot.getChildren()){
                    if(ds.exists()) {
                        konselor_id = ds.getKey();
                        if (ds.child(NodeNames.NAME).getValue() != null) {
                            konselor_nama = ds.child(NodeNames.NAME).getValue().toString();
                            konselor_email =ds.child(NodeNames.EMAIL).getValue().toString();
                            konselor_photo = ds.child(NodeNames.PHOTO).getValue().toString();
                            konselor_ponsel = ds.child(NodeNames.PHONE_NUMBER).getValue().toString();
                            konselor_online = ds.child(NodeNames.ONLINE).getValue().toString();
                            konselor_role = ds.child(NodeNames.ROLE).getValue().toString();
                            konselor_nim =ds.child(NodeNames.NIM).getValue().toString();
                            konselor_angkatan = ds.child(NodeNames.ANGKATAN).getValue().toString();
                            konselor_kelamin = ds.child(NodeNames.JENIS_KELAMIN).getValue().toString();

                            KonselorModel konselor = new KonselorModel(konselor_id,konselor_nama,konselor_email,konselor_photo,konselor_ponsel,konselor_online,konselor_role,konselor_nim,konselor_angkatan,konselor_kelamin);
                            konselorList.add(konselor);

                            databaseReferenceKonselor.child(konselor_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        konselorAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    Toast.makeText(getContext(), getContext().getString( R.string.failed_to_read_data, error.getMessage())
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), getContext().getString( R.string.failed_to_read_data, error.getMessage())
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

}