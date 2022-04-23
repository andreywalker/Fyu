package com.example.fyu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Data.Provider;

import static java.lang.Integer.valueOf;

public class PlaceActivity extends AppCompatActivity {
    static final String TAG = "myLogs";
    static int PAGE_COUNT ;
    int pos;
    ViewPager pager;
    FragmentAdapter pagerAdapter;
    ImageView but;
    ImageView str;
    ImageView fonSet;
    ImageView fonDay;
    boolean doNotStopTheService=false;

    Intent i;

    Animation rise;
    Animation darkness;
    ImageView dark;

    Provider p;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        but=findViewById(R.id.placebutton);
        fonDay=findViewById(R.id.imageViewDay);
        fonSet=findViewById(R.id.imageViewSet);
        fonDay.setKeepScreenOn(true);
        pager =findViewById(R.id.pager);
        dark=findViewById(R.id.darkness);
        str=findViewById(R.id.strelka);
        rise = AnimationUtils.loadAnimation(this,R.anim.rise);
        darkness =AnimationUtils.loadAnimation(this,R.anim.darkness);


        rise.setAnimationListener(riseList);
        darkness.setAnimationListener(darkList);
        PAGE_COUNT=new Provider(getApplicationContext()).getLvlsCount();
        //полноэкоранный режим
        pager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //обои с временем суток
        Date date = new Date();
        DateFormat timeFormatHours = new SimpleDateFormat("HH", Locale.getDefault());
        DateFormat timeFormatMinutes = new SimpleDateFormat("MM", Locale.getDefault());
        if(valueOf(timeFormatHours.format(date))==18&&valueOf(timeFormatMinutes.format(date))==0) {
            // создаем анимацию
            Animation day = AnimationUtils.loadAnimation(this, R.anim.dayanim);
            // запуск анимации
            fonSet.startAnimation(day);
            Animation night = AnimationUtils.loadAnimation(this, R.anim.nightanim);
            fonDay.startAnimation(night);
        }
        if(valueOf(timeFormatHours.format(date))>=18&&valueOf(timeFormatMinutes.format(date))!=0){
            fonDay.setAlpha(0.0f);
            fonSet.setAlpha(1.0f);
        }

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position+"  "+p.getLvlsCount());
                pos=position;
                if(position>=p.getLvlsCount()-1){
                    str.setVisibility(View.INVISIBLE);
                }else{
                    str.setVisibility(View.VISIBLE);
                }
                //but.setImageResource(p.getLvlFonId(pos-1));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        p=new Provider(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.getPlayer()!=null) {
            if (!PlayerService.getPlayer().isPlaying()){ PlayerService.getPlayer().start();}
        }
        //эффект появления
        dark.startAnimation(rise);
    }

    public void placesel(View v){
        if(pos< p.currentLvl) {
            //stopService(new Intent(PlaceActivity.this, PlayerService.class));
            if(!doNotStopTheService)PlayerService.getPlayer().pause();
            i = new Intent(PlaceActivity.this, GameActivity.class);
            i.putExtra("lvlId", pos);
            dark.startAnimation(darkness);
        }
        Log.d("xvbmdoblnfbods","llllllllllllllllllllllllllllllllllllllllll   "+pos+"     currr    "+p.currentLvl);
    }




    public void back(View v){
        i=new Intent(PlaceActivity.this,MainActivity.class);

        dark.startAnimation(darkness);
    }





    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }





    @Override
    protected void onPause() {
        if(!doNotStopTheService)PlayerService.getPlayer().pause();
        super.onPause();
    }
    Animation.AnimationListener riseList =new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dark.setAlpha(0.0f);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    Animation.AnimationListener darkList =new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            dark.setAlpha(1.0f);

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            startActivity(i);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onBackPressed() {
        i=new Intent(PlaceActivity.this,MainActivity.class);
        dark.startAnimation(darkness);

    }

}

