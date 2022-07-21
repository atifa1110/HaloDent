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
        Jadwals jadwalsModel = jadwals.get(position);

        holder.tanggal.setText(jadwalsModel.getTanggal());
        holder.jam.setText(jadwalsModel.getMulai()+" - "+ jadwalsModel.getSelesai());
        setDokterName(jadwalsModel,holder);

        holder.btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.btn_option);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_jadwal);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_detail:
                                Intent intent = new Intent(context, DetailDokterActivity.class);
                                intent.putExtra("dokter_id", jadwalsModel.getDokter_id());
                                context.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
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

