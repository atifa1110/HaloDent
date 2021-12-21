package com.dentist.halodent.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.halodent.Model.Extras;
import com.dentist.halodent.Model.KonselorModel;
import com.dentist.halodent.Model.Preference;
import com.dentist.halodent.R;
import com.dentist.halodent.Activity.DetailKonselorActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KonselorAdapter extends RecyclerView.Adapter<KonselorAdapter.KonselorViewHolder> {

    private Context context;
    private List<KonselorModel> konselorList;

    public KonselorAdapter(Context context, List<KonselorModel> konselorList) {
        this.context = context;
        this.konselorList = konselorList;
    }

    @NonNull
    @NotNull
    @Override
    public KonselorViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_konselor_list,parent,false);
        return new KonselorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull KonselorAdapter.KonselorViewHolder holder, int position) {

        KonselorModel konselor = konselorList.get(position);

        holder.tvNamaKonselor.setText(konselor.getNama());
        holder.tvOnline.setText(konselor.getStatus());

        if (konselor.getStatus().equals("Online")){
            holder.ivCircle.setImageDrawable(context.getDrawable(R.drawable.ic_circle_green));
        }else{
            holder.tvOnline.setTextColor(context.getResources().getColor(R.color.grey));
            holder.ivCircle.setImageDrawable(context.getDrawable(R.drawable.ic_circle_gray));
        }

        try{
            Glide.with(context)
                    .load(konselor.getPhoto())
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
                intent.putExtra(Extras.KONSELOR,new KonselorModel(konselor.getId(),konselor.getNama(),konselor.getEmail(),konselor.getPhoto(),konselor.getPonsel(),konselor.getStatus(),konselor.getRole(),konselor.getNim(),konselor.getAngkatan(),konselor.getKelamin()));
                Preference.setKeyKonselorId(context,konselor.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return konselorList.size();
    }

    public class KonselorViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfileKonselor , ivCircle;
        private TextView tvNamaKonselor , tvOnline;

        public KonselorViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileKonselor = itemView.findViewById(R.id.iv_profile_konselor);
            ivCircle = itemView.findViewById(R.id.iv_circle);
            tvNamaKonselor = itemView.findViewById(R.id.tv_nama_konselor);
            tvOnline = itemView.findViewById(R.id.tv_online);
        }
    }
}
