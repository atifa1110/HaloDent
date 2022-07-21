package com.dentist.halodent.OnBoarding;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dentist.halodent.R;

import java.util.List;

public class OnBoardingViewPagerAdapter extends RecyclerView.Adapter<OnBoardingViewPagerAdapter.OnBoardingViewHolder> {

    Context mContext;
    List<ScreenItem> mListScreen;

    public OnBoardingViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @Override
    public OnBoardingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_onboarding,parent,false);
        return new OnBoardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OnBoardingViewPagerAdapter.OnBoardingViewHolder holder, int position) {

        holder.introTitle.setText(mListScreen.get(position).getTitle());
        holder.introDesc.setText(mListScreen.get(position).getDescription());
        holder.introImage.setImageResource(mListScreen.get(position).getScreenImg());

    }

    @Override
    public int getItemCount() {
        return mListScreen.size();
    }


    public class OnBoardingViewHolder extends RecyclerView.ViewHolder{
        private ImageView introImage;
        private TextView introTitle , introDesc;

        public OnBoardingViewHolder(View itemView) {
            super(itemView);

            introImage = itemView.findViewById(R.id.intro_img);
            introTitle = itemView.findViewById(R.id.intro_title);
            introDesc = itemView.findViewById(R.id.intro_description);
        }
    }
}