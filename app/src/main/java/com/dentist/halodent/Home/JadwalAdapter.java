package com.dentist.halodent.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dentist.halodent.Model.NodeNames;
import com.dentist.halodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {

    private Context context;
    private List<Jadwals> jadwals;

    public JadwalAdapter(Context context, List<Jadwals> jadwals) {
        this.context = context;
        this.jadwals = jadwals;
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_list,parent,false);
        return new JadwalAdapter.JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwals jadwal = jadwals.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;

        try {
            date = sdf.parse(jadwal.getTanggal());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
        String newFormat = formatter.format(date);

        holder.tanggal.setText(newFormat);
        holder.jam.setText(jadwal.getMulai()+" - "+ jadwal.getSelesai());
        setDokterName(jadwal,holder);

    }

    private void setDokterName(Jadwals jadwalsModel, JadwalViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        ref.child(jadwalsModel.getDokter_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String nama = snapshot.child(NodeNames.NAME).getValue().toString();
                holder.dokter.setText("Diawasi oleh: Drg."+ nama);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return jadwals.size();
    }

    public class JadwalViewHolder extends RecyclerView.ViewHolder {

        private TextView tanggal,dokter,jam,btn_option;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);

            dokter = itemView.findViewById(R.id.tv_dokter_jadwal);
            jam = itemView.findViewById(R.id.tv_jam_jadwal);
            tanggal = itemView.findViewById(R.id.tv_tanggal_jadwal);
            btn_option = itemView.findViewById(R.id.btn_options);
        }
    }
}

