package com.example.fyu;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import Data.Level;
import Data.Provider;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {
    int dx;
    int dy;
    int mp;
    Level lvl;
    GameView gv;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        dx = point.x;
        dy = point.y;
        mp=dy/20;
        Provider provider=new Provider(getApplicationContext());
        lvl=provider.getLevel(getIntent().getExtras().getInt("lvlId")+1);
        gv=new GameView(getApplicationContext(),lvl,dx,dy,MainActivity.soundlevel);
        gv.setKeepScreenOn(true);
        setContentView(gv);
    }









    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }










    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onResume(){
        super.onResume();
        setContentView(gv);
        gv.setOnTouchListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        gv.setPause(false);



    }








    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: // нажатие
                gv.xDown = event.getX();
                gv.yDown = event.getY();
                gv.setTouched(true);
                //проверка на кнопку начала
                if (gv.xDown > dx - 4 * mp && gv.xDown < dx && gv.yDown > 0 && gv.yDown < 4 * mp&&!gv.isDead&&!gv.isPause()) {
                    gv.setPause(true);
                    gv.play();
                }else if(gv.isPause()&&gv.xDown>dx/2-3*mp&&gv.xDown<dx/2+3*mp&&gv.yDown>8*mp&&gv.yDown<14*mp){
                    gv.setPause(false);
                    gv.invalidate();
                }else if((gv.isDead||gv.isPause())&&gv.xDown>=gv.pausaX+4*mp&&gv.xDown<=gv.pausaX+10*mp&&gv.yDown<dy-2*mp&&gv.yDown>=dy-8*mp){
                    gv.setHeroToBegin();
                    gv.isPause=false;
                    gv.invalidate();
                }else if((gv.isDead||gv.isPause())&&gv.xDown>=dx-gv.pausaX-8*mp&&gv.xDown<=dx-gv.pausaX-2*mp&&gv.yDown<dy-2*mp&&gv.yDown>=dy-8*mp){
                    startActivity(new Intent(GameActivity.this, PlaceActivity.class));
                }else if(gv.endOfGame&&gv.xDown>dx/2-3*mp&&gv.xDown<dx/2+3*mp&&gv.yDown>8*mp&&gv.yDown<14*mp){
                    startActivity(new Intent(GameActivity.this, PlaceActivity.class));
                }
                Log.d("MainActivity", "Dddddddddddddddddddddddddddddown = " + gv.xDown);
                //установка большого кружка джостика на место касания пальцем;
                break;
            case MotionEvent.ACTION_MOVE: // движение
                gv.setTouched(true);
                // if(gv.xDown < dx - 4 * mp && gv.xDown > dx && gv.yDown < 0 && gv.yDown > 4 * mp&&!gv.isDead&&!gv.isPause()) {
                gv.yMove = event.getY();
                gv.xMove = event.getX();
                //}
                break;
            case MotionEvent.ACTION_UP:
                // отпускание
                gv.setTouched(false);
                gv.xDown = 0.0f;
                gv.yDown = 0.0f;
                gv.xMove=0.0f;
                gv.yMove=0.0f;
                break;

            case MotionEvent.ACTION_CANCEL:
                gv.setTouched(false);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //второй палец
                //нажатие
                Log.d("MainActivity", "second");
                gv.isSecondTouched = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //второй палец
                //отпускание
                gv.isSecondTouched = false;
                Log.d("MainActivity", "second");
                break;
        }
        return true;
    }











    @Override
    protected void onStop() {
        super.onStop();
        lvl.setCurrentCP(gv.getCurrentCP(),getApplicationContext());
        gv.setPause(true);
    }

    @Override
    public void onBackPressed() {
        lvl.setCurrentCP(gv.getCurrentCP(),getApplicationContext());
        super.onBackPressed();
        //gv.surfaceDestroyed(gv.getHolder());
    }

    @Override
    protected void onPause() {
        super.onPause();
        lvl.setCurrentCP(gv.getCurrentCP(),getApplicationContext());
        //gv.surfaceDestroyed(gv.getHolder());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lvl.setCurrentCP(gv.getCurrentCP(),getApplicationContext());
        //gv.surfaceDestroyed(gv.getHolder());
    }
}
