package com.dentist.halodent.OnBoarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.dentist.halodent.R;
import com.dentist.halodent.Activity.SignInActivity;
import com.dentist.halodent.Model.ScreenItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager2 screenPager;
    private TabLayout tabIndicator;
    private OnBoardingViewPagerAdapter onBoardingViewPagerAdapter;
    private Button btnNext, btnMulai , btnSkip;
    private TextView tvSkip;

    private Animation btnAnim ;
    private int position = 0 ;

    final List<ScreenItem> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), SignInActivity.class );
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_on_boarding);

        // hide the action bar
        //getSupportActionBar().hide();

        // set view
        screenPager = findViewById(R.id.screen_viewpager);
        tabIndicator = findViewById(R.id.tab_indicator);
        tvSkip = findViewById(R.id.tv_skip);
        btnNext = findViewById(R.id.btn_next);
        btnMulai = findViewById(R.id.btn_mulai);

        // fill list screen
        mList.add(new ScreenItem(getString(R.string.chat_bersama_dokter),getString(R.string.konsultasi_deskripsi),R.drawable.ic_chat));
        mList.add(new ScreenItem(getString(R.string.caries_risk_assesment),getString(R.string.caries_deskripsi),R.drawable.ic_caries));
        mList.add(new ScreenItem(getString(R.string.informasi_kesehatan_gigi),getString(R.string.informasi_deskripsi),R.drawable.ic_file));

        // setup viewpager
        onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(this,mList);
        screenPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        screenPager.setAdapter(onBoardingViewPagerAdapter);

        //setup tablayout with viewpager
        //tabIndicator.setupWithViewPager(screenPager);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabIndicator, screenPager, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        });
        tabLayoutMediator.attach();

        // next button click Listner
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()-1) { // when we rech to the last screen
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loaddLastScreen();
                }
            }
        });

        // tablayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Get Started button click listener
        btnMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity
                Intent mainActivity = new Intent(OnBoardingActivity.this,SignInActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();
            }
        });

        // skip button click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }


    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnMulai.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnMulai.setAnimation(btnAnim);
    }
}
