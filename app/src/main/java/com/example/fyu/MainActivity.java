package com.example.fyu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Data.Provider;

public class MainActivity extends AppCompatActivity {
    ImageView fonSet;
    ImageView fonDay;
    ImageView set;
    ImageView play;
    public static int mp;
    boolean doNotStopTheService=false;
    Animation rise;
    Animation darkness;
    ImageView dark;
    public static float soundlevel;
    public static float musiclevel;
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //чтение данных о звуке из бд
        Provider p=new Provider(getApplicationContext());
        soundlevel=p.getSound();
        musiclevel=p.getMusic();

        setContentView(R.layout.activity_main);
        //иннициализация
        set=findViewById(R.id.set);
        play=findViewById(R.id.play);
        fonSet= findViewById(R.id.imageViewSet);
        fonDay=findViewById(R.id.imageViewDay);
        dark =findViewById(R.id.darkness);
        rise =AnimationUtils.loadAnimation(this,R.anim.rise);
        darkness =AnimationUtils.loadAnimation(this,R.anim.darkness);
        //полноэкранный landscape режим
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        play.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        //чтоб экран не "засыпал"
        play.setKeepScreenOn(true);
        //начало мелодии
        startService(new Intent(MainActivity.this, PlayerService.class));
        //смена времени суток на обоях
        Date date = new Date();
        DateFormat timeFormatHours = new SimpleDateFormat("HH", Locale.getDefault());
        DateFormat timeFormatMinutes = new SimpleDateFormat("MM", Locale.getDefault());

        if(Integer.parseInt(timeFormatHours.format(date))==18&& Integer.parseInt(timeFormatMinutes.format(date))==0) {
            // создаем анимацию
            Animation day = AnimationUtils.loadAnimation(this, R.anim.dayanim);
            // запуск анимации
            fonSet.startAnimation(day);
            Animation night = AnimationUtils.loadAnimation(this, R.anim.nightanim);
            fonDay.startAnimation(night);
        }
        if(Integer.parseInt(timeFormatHours.format(date))>=18&& Integer.parseInt(timeFormatMinutes.format(date))!=0){
            fonDay.setAlpha(0.0f);
            fonSet.setAlpha(1.0f);
        }
        //слушатели кнопок

        View.OnClickListener playlis=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MainActivity.this,PlaceActivity.class);
                doNotStopTheService=true;
                dark.startAnimation(darkness);
            }
        };
        View.OnClickListener setlis=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(MainActivity.this,SettingsActivity.class);
                doNotStopTheService=true;
                dark.startAnimation(darkness);
            }
        };
        set.setOnClickListener(setlis);
        play.setOnClickListener(playlis);
        //если впервые в приложении  показать историю
        if(p.isFirst){
            intent=new Intent(MainActivity.this, StoryActivity.class);
            dark.startAnimation(darkness);
        }
    }

    @Override
    protected void onPause() {
        if(!doNotStopTheService)PlayerService.getPlayer().pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.getPlayer()!=null) {
            if (!PlayerService.getPlayer().isPlaying()) PlayerService.getPlayer().start();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        play.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        //измерение размеров экрана
        int dx = point.x;
        int dy = point.y;
        //ввод кастомной единицы длины
        mp=dy/20;
        //эффект появления
        dark.startAnimation(rise);
        rise.setAnimationListener(riseList);
        darkness.setAnimationListener(darkList);

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
            startActivity(intent);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    public void st(View v){
        intent=new Intent (MainActivity.this,StoryActivity.class);
        dark.startAnimation(darkness);
    }

}
