package com.dentist.halodent.Info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dentist.halodent.R;
import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.Home.TopikAdapter;
import com.dentist.halodent.Model.TopikModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class InfoFragment extends Fragment {

    private RecyclerView rvTopik;
    private TopikAdapter topikAdapter;
    private List<TopikModel> topikModelList;
    private View progress_bar;

    private String topik_id,topik_nama,topik_narasi,topik_photo,topik_sumber,topik_time,topik_tipe;
    private DatabaseReference databaseReferenceTopik;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        progress_bar = view.findViewById(R.id.progress_bar);

        rvTopik = view.findViewById(R.id.rv_info);
        databaseReferenceTopik = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOPIKS);

        topikModelList = new ArrayList<>();
        rvTopik.setLayoutManager(new LinearLayoutManager(getContext()));
        topikAdapter = new TopikAdapter(getContext(), topikModelList);
        rvTopik.setAdapter(topikAdapter);


        readTopikDatabase();
        //progress_bar.setVisibility(View.VISIBLE);
    }

    private void readTopikDatabase(){
        //progress_bar.setVisibility(View.GONE);
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topikModelList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.exists()) {
                        final String topik_id = ds.getKey();
                        final String topik_nama = ds.child(NodeNames.TOPIK_NAME).getValue().toString();
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
                        topikModelList.add(new TopikModel(topik_id,topik_nama,topik_photo,topik_narasi,topik_sumber,topik_time,topik_tipe));
                        topikAdapter.notifyDataSetChanged();
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

    private void searchTopik(final String query) {
        progress_bar.setVisibility(View.GONE);
        databaseReferenceTopik.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                topikModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    topik_id = ds.getKey();
                    Log.d("id_topik",topik_id);
                    if (ds.exists()) {
                        if(ds.child(NodeNames.TOPIK_NAME).toString().toLowerCase().contains(query.toLowerCase()) ||
                                ds.child(NodeNames.TOPIK_NARASI).toString().toLowerCase().contains(query.toLowerCase())){
                            topik_nama = ds.child(NodeNames.TOPIK_NAME).getValue().toString();
                            Log.d("namaTopik",topik_nama);
                            topik_photo = ds.child(NodeNames.TOPIK_PHOTO).getValue().toString();
                            topik_narasi = ds.child(NodeNames.TOPIK_NARASI).getValue().toString();
                            topik_time = ds.child(NodeNames.TOPIK_TIME).getValue().toString();
                            topik_tipe = ds.child(NodeNames.TOPIK_TYPE).getValue().toString();
                            TopikModel topikModel = new TopikModel(topik_id,topik_nama,topik_narasi,topik_photo,topik_sumber,topik_time,topik_tipe);
                            topikModelList.add(topikModel);
                        }else{
                            Toast.makeText(getContext(),"Data failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                    topikAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        MenuItem info = menu.findItem(R.id.menu_info);
        info.setVisible(false);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when submit text
                if(!TextUtils.isEmpty(query.trim())){
                    searchTopik(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called when typing text
                if(!TextUtils.isEmpty(newText.trim())){
                    searchTopik(newText);
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}