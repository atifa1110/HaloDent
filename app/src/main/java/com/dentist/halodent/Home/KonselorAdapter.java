package com.dentist.halodent.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KonselorAdapter extends RecyclerView.Adapter<KonselorAdapter.KonselorViewHolder> {

    private Context context;
    private List<Konselors> konselorsList;

    public KonselorAdapter(Context context, List<Konselors> konselorsList) {
        this.context = context;
        this.konselorsList = konselorsList;
    }

    @NonNull
    @NotNull
    @Override
    public KonselorViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_list,parent,false);
        return new KonselorViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull @NotNull KonselorAdapter.KonselorViewHolder holder, int position) {

        Konselors konselors = konselorsList.get(position);

        holder.tvNamaKonselor.setText(konselors.getNama());
        holder.tvOnline.setText(konselors.getStatus());

        if (konselors.getStatus().equals("Online")){
            holder.ivCircle.setImageDrawable(context.getDrawable(R.drawable.ic_circle_green));
        }else{
            holder.tvOnline.setTextColor(context.getResources().getColor(R.color.grey));
            holder.ivCircle.setImageDrawable(context.getDrawable(R.drawable.ic_circle_gray));
        }

        try{
            Glide.with(context)
                    .load(konselors.getPhoto())
                    .placeholder(R.drawable.ic_user)
                    .fitCenter()
                    .into(holder.ivProfileKonselor);

        }catch (Exception e){
            holder.ivProfileKonselor.setImageResource(R.drawable.ic_user);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailKonselorActivity.class);
                intent.putExtra("konselor",new Konselors(konselors.getId(), konselors.getNama(), konselors.getEmail(), konselors.getPhoto(), konselors.getPonsel(), konselors.getStatus(), konselors.getRole(), konselors.getKelamin(), konselors.getNim(), konselors.getAngkatan()));
                Preference.setKeyKonselorId(context, konselors.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return konselorsList.size();
    }

    public class KonselorViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileKonselor , ivCircle;
        private TextView tvNamaKonselor , tvOnline;

        public KonselorViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileKonselor = itemView.findViewById(R.id.iv_profile_user);
            ivCircle = itemView.findViewById(R.id.iv_circle);
            tvNamaKonselor = itemView.findViewById(R.id.tv_nama_user);
            tvOnline = itemView.findViewById(R.id.tv_online);
        }
    }
}
