package com.dentist.halodent.Info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dentist.halodent.Model.Topiks;
import com.dentist.halodent.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private RecyclerView rvTopik;
    private TopikAdapter topikAdapter;
    private List<Topiks> topikList;
    private TextView info_tidak;

    private DatabaseReference databaseReferenceTopik;

    private Chip chip_foto, chip_video;
    private TextInputEditText search;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //inizialization view
        info_tidak = view.findViewById(R.id.tv_info_tidak);
        rvTopik = view.findViewById(R.id.rv_info);
        chip_foto = view.findViewById(R.id.chip_foto);;
        chip_video = view.findViewById(R.id.chip_video);;
        search = view.findViewById(R.id.searchView);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_info);
        shimmerFrameLayout.startShimmer();

        //inizialization database
        databaseReferenceTopik = FirebaseDatabase.getInstance().getReference().child(Topiks.class.getSimpleName());

        //inizialization adapter
        topikList = new ArrayList<>();

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);

        rvTopik.setLayoutManager(linearLayout);
        topikAdapter = new TopikAdapter(getContext(), topikList);
        rvTopik.setAdapter(topikAdapter);

        chip_video.setOnCheckedChangeListener(this);
        chip_foto.setOnCheckedChangeListener(this);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()){
                    rvTopik.setAdapter(topikAdapter);
                    topikAdapter.notifyDataSetChanged();
                }else{
                    searchTopik(s.toString());
                }
            }
        });

        //read database
        getDataTopik();
    }

    private void getDataTopik(){
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topikList.clear();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for(DataSnapshot data : snapshot.getChildren()) {
                    if (data.exists()) {
                        rvTopik.setVisibility(View.VISIBLE);
                        Topiks topiks = data.getValue(Topiks.class);
                        topikList.add(topiks);
                        topikAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), getContext().getString( R.string.failed_to_read_data, error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchTopik(final String query) {
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topikList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.exists()) {
                        Topiks topiks = data.getValue(Topiks.class);
                        if(topiks.getJudul().toLowerCase().contains(query.toLowerCase()) ||
                                topiks.getNarasi().toLowerCase().contains(query.toLowerCase())){
                            topikList.add(topiks);
                            topikAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void searchFoto(String foto){
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topikList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.exists()) {
                        Topiks topiks = data.getValue(Topiks.class);
                        if(topiks.getTipe().contains(foto.toLowerCase())){
                            topikList.add(topiks);
                            topikAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchVideo(String video){
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topikList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.exists()) {
                        Topiks topiks = data.getValue(Topiks.class);
                        if(topiks.getTipe().contains(video.toLowerCase())){
                            topikList.add(topiks);
                            topikAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.chip_foto:
                if(isChecked){
                    String foto = "photo";
                    searchFoto(foto);
                }else{
                    getDataTopik();
                }
                break;
            case R.id.chip_video:
                if(isChecked){
                    String video = "video";
                    searchVideo(video);
                }else{
                    getDataTopik();
                }
                break;
        }
    }
}