package com.dentist.halodent.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Info.TopikAdapter;
import com.dentist.halodent.Info.Topiks;
import com.dentist.halodent.Profile.KuesionerActivity;
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

    private RecyclerView rvKonselor,rvTopik;
    private KonselorAdapter konselorAdapter;
    private List<Topiks> topiksList;
    private List<Konselors> konselorsList;
    private TopikAdapter topikAdapter;

    private TextView tvNama;
    private ImageView ivProfile , image;
    private Button btnlihatKonselor,btnLetsgo;
    private View layoutKuesioner;

    private String topik_id,konselor_id;
    private DatabaseReference databaseReferenceKonselor,databaseReferenceTopik;
    private FirebaseUser currentUser;
    private Uri serverFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutKuesioner = view.findViewById(R.id.ll_blm_isi);
        btnLetsgo = view.findViewById(R.id.btn_lets_go);
        image = view.findViewById(R.id.iv_icon_belum_isi);
        image.bringToFront();

        tvNama = view.findViewById(R.id.tv_nama_home);
        ivProfile = view.findViewById(R.id.iv_profile_home);
        btnlihatKonselor = view.findViewById(R.id.btn_lihat_semua_konselor);
        rvKonselor = view.findViewById(R.id.rv_available_konselor);
        rvTopik = view.findViewById(R.id.rv_topik_hangat);

        konselorsList = new ArrayList<>();
        rvKonselor.setLayoutManager(new LinearLayoutManager(getContext()));
        konselorAdapter = new KonselorAdapter(getContext(), konselorsList);
        rvKonselor.setAdapter(konselorAdapter);

        databaseReferenceTopik = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOPIKS);
        databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
        topiksList = new ArrayList<>();
        rvTopik.setLayoutManager(new LinearLayoutManager(getContext()));
        topikAdapter = new TopikAdapter(getContext(), topiksList);
        rvTopik.setAdapter(topikAdapter);

        Query query = databaseReferenceTopik.orderByChild(NodeNames.TOPIK_TIME).limitToLast(3);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topiksList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.exists()){
                        topik_id = ds.getKey();
                        Topiks topiks = ds.getValue(Topiks.class);
                        topiksList.add(topiks);

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
                konselorsList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.exists()) {
                        konselor_id = data.getKey();
                        Konselors konselors = data.getValue(Konselors.class);
                        if (konselors.getNama() != null) {
                            konselorsList.add(konselors);

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