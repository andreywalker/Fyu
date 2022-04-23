package com.example.fyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StoryActivity extends AppCompatActivity {
    ImageView fonSet;
    ImageView fonDay;
    ImageView dark;
    ImageView back;
    TextView story;
    Animation appearAnimation;
    Animation fadeAnimation ;
    Animation rise;
    int i=0;
    String [] stories;
    Context c;
    boolean doNotStopTheService=false;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        fonSet= findViewById(R.id.imageViewSet);
        fonDay=findViewById(R.id.imageViewDay);
        dark=findViewById(R.id.imaged);
        story=findViewById(R.id.storyView);
        back=findViewById(R.id.imageVie);
        story.setKeepScreenOn(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fonDay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        Context c= getApplication().getApplicationContext();
        startService(new Intent(StoryActivity.this, PlayerService.class));
        Date date = new Date();
        DateFormat timeFormatHours = new SimpleDateFormat("HH", Locale.getDefault());
        DateFormat timeFormatMinutes = new SimpleDateFormat("MM", Locale.getDefault());
        back.setVisibility(ImageView.INVISIBLE);

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
        stories=getResources().getString(R.string.story1).split("==");
        final Animation darkness=AnimationUtils.loadAnimation(c,R.anim.darkness);
        rise=AnimationUtils.loadAnimation(c,R.anim.rise);
        dark.startAnimation(rise);
        appearAnimation=AnimationUtils.loadAnimation(c, R.anim.appear);
        fadeAnimation =AnimationUtils.loadAnimation(c,R.anim.fade);
        Animation.AnimationListener listener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(i==stories.length){
                }else{
                    story.setText(stories[i]);

                    story.startAnimation(appearAnimation);}
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        darkness.setAnimationListener(listener);

        Animation.AnimationListener fadeListener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (i == stories.length) {
                    story.setAlpha(0.0f);
                    startActivity(new Intent(StoryActivity.this,MainActivity.class));
                } else {
                    story.setText(stories[i]);
                    //story.setAlpha(0.0f);
                    story.startAnimation(appearAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation.AnimationListener appearListener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                story.startAnimation(fadeAnimation);
                i++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        Animation.AnimationListener riseListener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dark.startAnimation(darkness);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        rise.setAnimationListener(riseListener);
        fadeAnimation.setAnimationListener(fadeListener);
        appearAnimation.setAnimationListener(appearListener);

    }
    public void vis(View v){
        back.setVisibility(View.VISIBLE);
    }


    public void back(View v){
        doNotStopTheService=true;
        startActivity(new Intent(StoryActivity.this,MainActivity.class));
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
}
