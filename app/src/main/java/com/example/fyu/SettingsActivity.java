package com.example.fyu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import Data.Provider;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    SeekBar music;

    TextView set;
    TextView langtext;

    TextView tv;
    TextView tv2;

    RadioGroup langButtons;

    Animation rise;
    Animation darkness;
    ImageView dark;

    boolean doNotStopTheService=false;
    ImageView back;
    @SuppressLint({"WrongViewCast", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        set=findViewById(R.id.set);
        set.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        langtext=findViewById(R.id.langtext);
        langButtons= findViewById(R.id.lang);
        music=findViewById(R.id.musicbar);
        back=findViewById(R.id.back);
        tv=findViewById(R.id.textView);
        dark=findViewById(R.id.darkness);
        tv2=findViewById(R.id.textView2);
        rise = AnimationUtils.loadAnimation(this,R.anim.rise);
        darkness =AnimationUtils.loadAnimation(this,R.anim.darkness);
        tv.setKeepScreenOn(true);
        Provider p= new Provider(getApplicationContext());
        music.setProgress((int)p.getMusic());
        music.setOnSeekBarChangeListener(this);
        back.setOnClickListener(backlis);
        PlayerService.getPlayer().start();
        langButtons= findViewById(R.id.lang);
        //эффект появления
        dark.startAnimation(rise);
        rise.setAnimationListener(riseList);
        darkness.setAnimationListener(darkList);
        langButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {

                switch(id) {
                    case R.id.rus:
                        setLang("ru");
                        break;
                    case R.id.kaz:
                        setLang("kk");
                        tv2.setText("Перевод на Казахский язык может быть некорректным");
                        break;
                    case R.id.eng:
                        setLang("en");
                        break;
                    default:
                        break;
                }
            }});
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setVolumeLevel(100,music.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        setVolumeLevel(100,music.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setVolumeLevel(100,music.getProgress());

    }
    public void setLang(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PlayerService.getPlayer()!=null) {
            if (!PlayerService.getPlayer().isPlaying()) PlayerService.getPlayer().start();
        }
    }

    @Override
    protected void onPause() {
        if(!doNotStopTheService)PlayerService.getPlayer().pause();
        super.onPause();
    }
    private void setVolumeLevel(float s, float m){
        MainActivity.soundlevel=s/100;
        MainActivity.musiclevel=m/100;
        Provider p= new Provider(getApplicationContext());
        PlayerService.getPlayer().setVolume(MainActivity.musiclevel,MainActivity.musiclevel);
        p.setMusic((int)m);
        p.setSound((int)s);
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
            startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onBackPressed() {
        dark.startAnimation(darkness);
        doNotStopTheService=true;

    }
    View.OnClickListener backlis=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dark.startAnimation(darkness);
            doNotStopTheService=true;
        }
    };
}