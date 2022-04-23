package com.example.fyu;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import Data.Provider;


public class PlayerService extends Service {
    private static MediaPlayer mPlayer;
    public PlayerService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        mPlayer = MediaPlayer.create(this, R.raw.sample);
        mPlayer.setLooping(true);
        Provider p=new Provider(getApplicationContext());
        mPlayer.setVolume(p.getMusic()/100,p.getMusic()/100);
        Log.d("kbomsbofsb","ooooooooooooooooonnnnnnnCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCc");
        mPlayer.start();

    }
    @Override
    public void onStart(Intent intent, int startid) {
        Log.d("pdmfvabm","oooooooooooooooonnnnnnnnnnSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSs");
        mPlayer.start();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.pause();
        mPlayer.stop();

    }
    static public MediaPlayer getPlayer(){
        return mPlayer;
    }
}
