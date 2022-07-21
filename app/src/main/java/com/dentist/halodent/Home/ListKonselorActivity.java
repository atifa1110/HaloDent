package com.dentist.halodent.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
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
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReferenceKonselor;

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
        rv_all_konselor.setLayoutManager(new LinearLayoutManager(this));
        konselorAdapter = new KonselorAdapter(this, konselorsList);
        rv_all_konselor.setAdapter(konselorAdapter);

        //initialization dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Silahkan Tunggu..");
        progressDialog.show();

        //read database
        getDataKonselor();
    }

    private void getDataKonselor(){
        Query query = databaseReferenceKonselor.orderByPriority();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                konselorsList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.exists()) {
                        progressDialog.dismiss();
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