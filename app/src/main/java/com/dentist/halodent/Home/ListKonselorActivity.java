package com.dentist.halodent.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.dentist.halodent.Model.Konselors;
import com.dentist.halodent.Utils.NodeNames;
import com.dentist.halodent.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListKonselorActivity extends AppCompatActivity {

    private RecyclerView rv_all_konselor;
    private List<Konselors> konselorsList;
    private KonselorAdapter konselorAdapter;
    private DatabaseReference databaseReferenceKonselor;
    private ShimmerFrameLayout shimmerFrameLayoutKonselor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_konselor);
        setActionBar();

        //initialization view
        rv_all_konselor = findViewById(R.id.rv_all_konselor);

        databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);

        ////initialization adapter
        konselorsList = new ArrayList<>();
        LinearLayoutManager linearLayout = new LinearLayoutManager(ListKonselorActivity.this);
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        rv_all_konselor.setLayoutManager(linearLayout);

        konselorAdapter = new KonselorAdapter(this, konselorsList);
        rv_all_konselor.setAdapter(konselorAdapter);

        shimmerFrameLayoutKonselor = findViewById(R.id.shimmer_user);
        shimmerFrameLayoutKonselor.startShimmer();

        //read database
        getDataKonselor();
    }

    private void getDataKonselor(){
        Query query = databaseReferenceKonselor.orderByChild(NodeNames.STATUS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                konselorsList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.exists()) {
                        shimmerFrameLayoutKonselor.stopShimmer();
                        shimmerFrameLayoutKonselor.setVisibility(View.GONE);
                        rv_all_konselor.setVisibility(View.VISIBLE);
                        Konselors konselors = data.getValue(Konselors.class);
                        konselorsList.add(konselors);
                        konselorAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Available Konselor");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
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