package com.example.fyu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import Data.Level;
import FonObjects.CP;
import FonObjects.Danger;
import FonObjects.Element;
import FonObjects.Fill;
import FonObjects.Fog;
import FonObjects.Line;
import FonObjects.RectG;

public class GameView extends View implements View.OnTouchListener
{
    int to=0;
    int cpt=0;
    boolean isCP=false;
    Level lvl;

    int dx;     //длина дисплея
    int dy;     //высота дисплея
    int mp;     //относительная мера длины
    float v;    //скорость

    float xDown;    //координаты косания пальца
    float yDown;
    float xMove;    //координаты перемещения пальца
    float yMove;

    RectF rectf;    //большой круг джостика
    RectF mrec;     //малый круг джостика
    RectF gg;       //прямоугольник главного героя
    Rect src;       //прямоугольник для создания масштабируемого изображения гг

    float ggx;      //координаты гг
    float ggy;

    float ggsize=3;

    boolean lastTurnWasRight=true;      //сторона , в которую повернут гг

    boolean wasImpactRect =false;

    float t=0;      //время
    int animT=0;    //время анимации затухания
    int animT2=0;        //время анимации затемнения
    int jt=0;

    float backX =0;

    boolean isTouched=false;        //касание
    boolean isSecondTouched=false;      //2-е касание
    boolean isJump=false;       //прыжок
    boolean isPause=false;      //пауза
    boolean isDead=false;       //смерть
    boolean isEndOfDead=false;      //конец прорисовки анимации смерти
    boolean endOfGame=false;           // конец игры
    boolean isFinished=false;       //уровень пройден

    int fonID;      //id картинки фона
    Bitmap picture;     //картинка фона
    Bitmap hero;        //изображения гг (право, лево , смерть , смерть лево)
    Bitmap heroL;
    Bitmap heroD;
    Bitmap heroDL;

    Bitmap pausa;           //окно паузы
    float pausaX;
    Bitmap dead;
    Bitmap pk;
    Bitmap fin;

    //Bitmap fogsB;

    Paint p;        //кисть

    Matrix m;

    Path fon;       //задний фон ( объекты находящиеся за гг )


    int currentCP;       //пройденная точка , на которую нужно вставать
    CP[] CPs;      //check points (точки сохранения и спавна)



    Fill[] fills;      //заполнения (объекты фона, с которыми гг не может столкнуться)

    RectG[] rects;      //прямоугольники , с которыми гг может столкнуться

    Danger[] dangers;     //опасности, которые могут убить гг

    Line[] lines;      //линии (гг может с ними столкнуться)

    Element[] elems;        //елементы окружения (картинки) с которыми гг не может столкнуться

    int currentFog;
    Fog[] fogs;        //туман , который может убить гг
    float curFogX;      //координата перемещения текущего тумана
    float curFogY;       //координата перемещения текущего тумана

    int[] lineim;       //линии , с которыми столкнулся гг
    int[] rectim;       //прямоугольники , с которыми столкнулся гг
    float[] deltaX;     //массивы занчаний длины перемещения по х за прорисовку
    float[] deltaY;       //массивы занчаний длины перемещения по х за прорисовку

    SoundPool sp;
    int woodId, metalId, rockId, portalId, deadId, finId, stepsId;
    float musiclevel;
    CompulationThread ct;

    Context context;

    public GameView(Context context, Level lvl,int dx,int dy,float soundlevel) {
        super(context);
        this.context=context;
        //наследование парваметров уровня
        this.lvl=lvl;
        this.fonID=lvl.getFonId();
        this.CPs=lvl.getCPs();
        this.rects=lvl.getRects();
        this.lines=lvl.getLines();
        this.fills=lvl.getFills();
        this.fogs=lvl.getFogs();
        this.dangers=lvl.getDangers();
        this.elems=lvl.getElements();
        this.currentCP=lvl.getCurrentCP();
        this.currentFog=lvl.getCurrentFog();
        this.dx=dx;
        this.dy=dy;
        //иннициализация
        mp=dy/20;
        v=mp/6;
        deltaX=new float[lines.length];
        deltaY=new float[lines.length];

        //ggsize*=mp;

        //настройка тумана
        if(fogs[currentFog].getDirection()==1) {
            curFogX = fogs[currentFog].getX1();
        }else if(fogs[currentFog].getDirection()==2) {
            curFogY = fogs[currentFog].getY1();
        }else if(fogs[currentFog].getDirection()==3) {
            curFogX = fogs[currentFog].getX2();
        }else if(fogs[currentFog].getDirection()==4) {
            curFogY = fogs[currentFog].getY2();
        }
        // установка гг на контр. точку
        ggx=CPs[currentCP].getX2()+3*mp;
        ggy=(CPs[currentCP].getY1()+CPs[currentCP].getY2())/2;
        backX=-currentCP*25*mp;



        lineim=new int[2];
        rectim=new int[2];
        Resources res = this.getResources();

        //fogsB=BitmapFactory.decodeResource(res,R.drawable.t1);
        Bitmap cBitmap = BitmapFactory.decodeResource(res,R.drawable.hero);
        hero = Bitmap.createScaledBitmap(cBitmap,(int)ggsize*mp,(int)ggsize*mp,false);
        cBitmap.recycle();
        Bitmap cBitmap2 = BitmapFactory.decodeResource(res,R.drawable.hero_l);
        heroL = Bitmap.createScaledBitmap(cBitmap2,(int)ggsize*mp,(int)ggsize*mp,false);
        cBitmap2.recycle();
        Bitmap cBitmap3 = BitmapFactory.decodeResource(res,R.drawable.hero);
        heroD = Bitmap.createScaledBitmap(cBitmap3,(int)ggsize*mp,(int)ggsize*mp,false);
        cBitmap3.recycle();
        Bitmap cBitmap4 = BitmapFactory.decodeResource(res,R.drawable.hero_l);
        heroDL = Bitmap.createScaledBitmap(cBitmap4,(int)ggsize*mp,(int)ggsize*mp,false);
        cBitmap4.recycle();
        // загружаем картинку, которую будем отрисовывать

        picture = BitmapFactory.decodeResource(res,fonID);
        for(int i=0;i<lines.length;i++){
            deltaX[i]=lines[i].getDeltaX(v);
            deltaY[i]=lines[i].getDeltaY(v);
        }
        p=new Paint();
        rectf = new RectF();
        mrec=new RectF();
        gg= new RectF();
        src=new Rect();
        fon=new Path();
        m=new Matrix();
        Bitmap pausa1=BitmapFactory.decodeResource(res,R.drawable.pausa);
        pausaX=(dx-25*mp)/2;
        pausa=Bitmap.createScaledBitmap(pausa1,25*mp,17*mp,true);
        Bitmap b1=BitmapFactory.decodeResource(res,R.drawable.dead);
        dead=Bitmap.createScaledBitmap(b1,25*mp,17*mp,true);
        Bitmap pk1=BitmapFactory.decodeResource(res,R.drawable.pausabt);
        pk=Bitmap.createScaledBitmap(pk1,8*mp,8*mp,true);
        Bitmap fin1=BitmapFactory.decodeResource(res,R.drawable.finish);
        fin=Bitmap.createScaledBitmap(fin1,25*mp,17*mp,true);

        sp=new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        Context c =getContext();
        sp.setOnLoadCompleteListener(new Load());
        woodId=sp.load(c,R.raw.wood,1);
        musiclevel=1.0f;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // получаем объект Canvas и выполняем отрисовку
        p.setColor(Color.BLACK);
        p.setStrokeWidth(10);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawBitmap(picture, 0, 0, p);


            if (!isDead && !isPause && !isFinished) {


                m.setTranslate(backX, 0);
                p.setColor(0x77000000);
                for (int i = 0; i <= currentFog; i++) {
                    if (i == currentFog) {
                        switch (fogs[currentFog].getDirection()) {
                            case 1:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), curFogX + backX, fogs[i].getY2(), p);
                                break;
                            case 2:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, curFogY, p);
                                break;
                            case 3:
                                canvas.drawRect(curFogX + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                            case 4:
                                canvas.drawRect(fogs[i].getX1() + backX, curFogY, fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                        }
                    } else
                        canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                }
                p.setColor(0xFF000000);
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getBack(), elem.getX1() + backX, elem.getY1(), p);
                }
                fon.transform(m);
                canvas.drawPath(fon, p);

                Log.d("dlvjosjdbvojvw", "ggggggggggggggggggggggggggggggggggggggggggggggggggggg      " + ggx / mp + "     " + ggy / mp);

                p.setColor(0xFF000000);
                if (getx(xDown, yDown, xMove, yMove) >= 0 || getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove)) {
                    canvas.drawBitmap(hero, ggx - ggsize/2*mp + backX, ggy -ggsize/2* mp, p);
                } else {
                    canvas.drawBitmap(heroL, ggx - ggsize/2*mp + backX, ggy - ggsize/2*mp, p);
                }
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getFront(), elem.getX1()  + backX, elem.getY1(), p);
                }
                for (Danger danger : dangers) {
                    canvas.drawBitmap(danger.getBitmap(), danger.getX() - danger.getR()+backX, danger.getY() - danger.getR(), p);
                }
                canvas.drawBitmap(pk,dx-8*mp,0,p);
                p.setColor(0x76555555);
                canvas.drawOval(rectf, p);
                p.setColor(0xFF007466);
                canvas.drawOval(mrec, p);
                to++;
                m.reset();
                ct=new CompulationThread();
                ct.execute();
            } else if (isDead && !isPause && !isFinished) {




                //CMEPTR






                animT++;
                if (animT >=16 ) {
                    animT--;
                    animT2++;
                    if(animT2>=32){
                        animT2--;
                        isEndOfDead=true;
                    }
                }

                lvl.setCurrentCP(currentCP, getContext()); m.setTranslate(backX, 0);
                //Log.d("omkergokerge","bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb   "+backX);
                p.setColor(0x77000000);
                for (int i = 0; i <= currentFog; i++) {
                    if (i == currentFog) {
                        switch (fogs[currentFog].getDirection()) {
                            case 1:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), curFogX + backX, fogs[i].getY2(), p);
                                break;
                            case 2:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, curFogY, p);
                                break;
                            case 3:
                                canvas.drawRect(curFogX + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                            case 4:
                                canvas.drawRect(fogs[i].getX1() + backX, curFogY, fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                        }
                    } else
                        canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                }
                p.setColor(0xFF000000);
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getBack(), elem.getX1()  + backX, elem.getY1(), p);
                }
                fon.transform(m);
                canvas.drawPath(fon, p);

                Log.d("dlvjosjdbvojvw", "ggggggggggggggggggggggggggggggggggggggggggggggggggggg      " + ggx / mp + "     " + ggy / mp);

                p.setAlpha(255-animT*16);
                if (getx(xDown, yDown, xMove, yMove) >= 0 || getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove)) {
                    canvas.drawBitmap(hero, ggx - ggsize/2*mp + backX, ggy -ggsize/2* mp, p);
                } else {
                    canvas.drawBitmap(heroL, ggx - ggsize/2*mp + backX, ggy - ggsize/2*mp, p);
                }
                p.setColor(0xFF000000);
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getFront(), elem.getX1() + backX, elem.getY1(), p);
                }
                for (Danger danger : dangers) {
                    canvas.drawBitmap(danger.getBitmap(), danger.getX() - danger.getR()+backX, danger.getY() - danger.getR(), p);
                }
                p.setAlpha(animT2*8);
                canvas.drawRect(-mp, -mp, dx *2, 23 * mp, p);
                canvas.drawBitmap(dead,pausaX,3*mp,p);
                m.reset();
                ct=new CompulationThread();
                ct.execute();








                //PAUSA





            } else if (!isDead && isPause && !isFinished) {
                lvl.setCurrentCP(currentCP, getContext());


                m.setTranslate(backX, 0);
                //Log.d("omkergokerge","bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb   "+backX);
                p.setColor(0x77000000);
                for (int i = 0; i <= currentFog; i++) {
                    if (i == currentFog) {
                        switch (fogs[currentFog].getDirection()) {
                            case 1:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), curFogX + backX, fogs[i].getY2(), p);
                                break;
                            case 2:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, curFogY, p);
                                break;
                            case 3:
                                canvas.drawRect(curFogX + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                            case 4:
                                canvas.drawRect(fogs[i].getX1() + backX, curFogY, fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                        }
                    } else
                        canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                }
                p.setColor(0xFF000000);
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getBack(), elem.getX1()  + backX, elem.getY1(), p);
                }
                fon.transform(m);
                canvas.drawPath(fon, p);

                Log.d("dlvjosjdbvojvw", "ggggggggggggggggggggggggggggggggggggggggggggggggggggg      " + ggx / mp + "     " + ggy / mp);

                p.setColor(0xFF000000);
                if (getx(xDown, yDown, xMove, yMove) >= 0 || getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove)) {
                    canvas.drawBitmap(hero, ggx - ggsize/2*mp + backX, ggy -ggsize/2* mp, p);
                } else {
                    canvas.drawBitmap(heroL, ggx - ggsize/2*mp + backX, ggy - ggsize/2*mp, p);
                }
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getFront(), elem.getX1() + backX, elem.getY1(), p);
                }
                for (Danger danger : dangers) {
                    canvas.drawBitmap(danger.getBitmap(), danger.getX() - danger.getR()+backX, danger.getY() - danger.getR(), p);
                }
                canvas.drawBitmap(pausa,pausaX,3*mp,p);

                m.reset();












            } else if (isFinished) {
                lvl.setDone(getContext());
                lvl.setCurrentCP(0, getContext());
                Log.d("kbpdjn","ffffffffffffffffffffffffiiiiiiiiiiiiiiiiiiiiiiiiiinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                m.setTranslate(backX, 0);
                animT++;
                if (animT >=32 ) {
                    animT--;
                    endOfGame=true;
                }
                for (int i = 0; i <= currentFog; i++) {
                    if (i == currentFog) {
                        switch (fogs[currentFog].getDirection()) {
                            case 1:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), curFogX + backX, fogs[i].getY2(), p);
                                break;
                            case 2:
                                canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, curFogY, p);
                                break;
                            case 3:
                                canvas.drawRect(curFogX + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                            case 4:
                                canvas.drawRect(fogs[i].getX1() + backX, curFogY, fogs[i].getX2() + backX, fogs[i].getY2(), p);
                                break;
                        }
                    } else
                        canvas.drawRect(fogs[i].getX1() + backX, fogs[i].getY1(), fogs[i].getX2() + backX, fogs[i].getY2(), p);
                }
                p.setColor(0xFF000000);
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getBack(), elem.getX1() + backX, elem.getY1(), p);
                }
                fon.transform(m);
                canvas.drawPath(fon, p);

                Log.d("dlvjosjdbvojvw", "ggggggggggggggggggggggggggggggggggggggggggggggggggggg      " + ggx / mp + "     " + ggy / mp);

                p.setColor(0xFF000000);
                if (getx(xDown, yDown, xMove, yMove) >= 0 || getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove)) {
                    canvas.drawBitmap(hero, ggx - ggsize/2*mp + backX, ggy -ggsize/2* mp, p);
                } else {
                    canvas.drawBitmap(heroL, ggx - ggsize/2*mp + backX, ggy - ggsize/2*mp, p);
                }
                for (Element elem : elems) {
                    canvas.drawBitmap(elem.getFront(), elem.getX1() + backX, elem.getY1(), p);
                }
                for (Danger danger : dangers) {
                    canvas.drawBitmap(danger.getBitmap(), danger.getX() - danger.getR()+backX, danger.getY() - danger.getR(), p);
                }
                p.setAlpha(animT*8);
                canvas.drawBitmap(fin,pausaX,3*mp,p);
                ct=new CompulationThread();
                ct.execute();
                m.reset();
            }
            if(!isPause) {
                invalidate();
            }
    }





    class CompulationThread extends AsyncTask<Void,Void,Void>{

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            if(!isDead&&!isFinished) {

                t += 0.02;

//проверка на второе нажатие
                if (!isSecondTouched) {
                    if (!isJump) {
                        //если нан


                        //если НЕ управляется курсором


                        if (getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove) || gety(xDown, yDown, xMove, yMove) != gety(xDown, yDown, xMove, yMove)) {
                            if (!isImpactRect(ggx, ggy) && !isImpactLine(ggx, ggy)) {
                                ggy = ggy + 10 * v * t + 10;
                                //wasImpactRect = false;
                            } else if (isImpactLine(ggx, ggy) && !isMultiImpactLine(ggx, ggy) && !isImpactRect(ggx, ggy)) {
                                ggx = ggx + deltaX[lineim[0]] * t;
                                ggy = ggy + deltaY[lineim[0]] * t;
                                //wasImpactRect = false;

                            } else if (isImpactLine(ggx, ggy) && isMultiImpactLine(ggx, ggy)) {
                                t = 0;
                                Log.d(",jhv,njgcghfch", "mmmmmmumumumumumumumumumuu");
                                wasImpactRect = false;
                            } else if (isImpactRect(ggx, ggy) && isImpactLine(ggx, ggy)&&!isMultiImpactRect(ggx,ggy)) {
                                t = 0;
                                if (!wasImpactRect) {
                                    //wasImpactRect = true;
                                    play();
                                }
                                if (!isImpactRect(ggx + deltaX[lineim[0]], ggy + deltaY[lineim[0]])) {
                                    ggx = ggx + deltaX[lineim[0]] * t;
                                    ggy = ggy + deltaY[lineim[0]] * t;
                                    //wasImpactRect = false;
                                }
                            }
                            else {
                                Log.d("bkgjfkhjfjv", "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                                if (isImpactRect(ggx, ggy)&&!isMultiImpactRect(ggx,ggy) && (ggx + ggsize/2*mp >= rects[rectim[0]].getX1() && ggx + ggsize/2*mp <= rects[rectim[0]].getX2() || ggx - ggsize/2*mp >= rects[rectim[0]].getX1() && ggx - ggsize/2*mp <= rects[rectim[0]].getX2()) && (isImpactRect(ggx + 1.5f * ggsize/2*mp, ggy) || isImpactRect(ggx - 1.5f*ggsize/2* mp, ggy))&&isImpactRect(ggx,ggy+mp)) {
                                    ggy = rects[rectim[0]].getY1() - ggsize/2*mp;
                                    t = 0;

                                } else if (isImpactRect(ggx, ggy)&&!isMultiImpactRect(ggx,ggy) && (ggy + ggsize/2*mp >= rects[rectim[0]].getY1() && ggy + ggsize/2*mp <= rects[rectim[0]].getY2() || ggy - ggsize/2*mp >= rects[rectim[0]].getY1() && ggy - ggsize/2*mp <= rects[rectim[0]].getY2()) && !isImpactRect(ggx + 1.5f * ggsize/2*mp, ggy)) {
                                    ggx = rects[rectim[0]].getX2() +ggsize/2*mp;
                                    t = 0;
                                    play();
                                } else if (isImpactRect(ggx, ggy) &&!isMultiImpactRect(ggx,ggy)&& (ggy + ggsize/2*mp >= rects[rectim[0]].getY1() && ggy + ggsize/2*mp <= rects[rectim[0]].getY2() || ggy - ggsize/2*mp >= rects[rectim[0]].getY1() && ggy - ggsize/2*mp <= rects[rectim[0]].getY2()) && !isImpactRect(ggx - 1.5f * ggsize/2*mp, ggy)) {
                                    ggx = rects[rectim[0]].getX1() - ggsize/2*mp;
                                    t = 0;
                                    play();
                                } else if (isImpactRect(ggx, ggy)&&!isMultiImpactRect(ggx,ggy) && (ggx + ggsize/2*mp >= rects[rectim[0]].getX1() && ggx + ggsize/2*mp <= rects[rectim[0]].getX2() || ggx - ggsize/2*mp >= rects[rectim[0]].getX1() && ggx - ggsize/2*mp <= rects[rectim[0]].getX2()) && (isImpactRect(ggx + 1.5f * ggsize/2*mp, ggy) || isImpactRect(ggx - 1.5f*ggsize/2* mp, ggy))&&!isImpactRect(ggx,ggy+mp)) {
                                    ggy+=mp;
                                    t = 0;

                                }
                                /*else if (isImpactRect(ggx, ggy) && (ggy + mp >= rects[rectim[0]].getY1() && ggy + mp <= rects[rectim[0]].getY2() || ggy - mp >= rects[rectim[0]].getY1() && ggy - mp <= rects[rectim[0]].getY2()) && (!isImpactRect(ggx - 1.5f * mp, ggy + 1.5f * mp) || !isImpactRect(ggx + 1.5f * mp, ggy + 1.5f * mp))) {
                                    ggy += v;
                                } else if (isImpactRect(ggx, ggy) && (ggy + mp >= rects[rectim[0]].getY1() && ggy + mp <= rects[rectim[0]].getY2() || ggy - mp >= rects[rectim[0]].getY1() && ggy - mp <= rects[rectim[0]].getY2()) && (!isImpactRect(ggx - 1.5f * mp, ggy - 1.5f * mp) || !isImpactRect(ggx + 1.5f * mp, ggy - 1.5f * mp))) {
                                    ggy = rects[rectim[0]].getY1();
                                }*/

                            }
                        }
                        //если не нан
                        //если курсор
                        else if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) && gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                            if (isImpactRect(ggx, ggy) && !isImpactLine(ggx, ggy) && !isMultiImpactRect(ggx, ggy)) {

                                Log.d("MainActivity", "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                                if (ggy + ggsize/2*mp <= rects[rectim[0]].getY1()) {
                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                    t = 0;
                                } else if (ggy >= rects[rectim[0]].getY1() && ggy <= rects[rectim[0]].getY2()) {
                                    ggy = ggy + 4 * v * t + 10;
                                }
                                else if((ggx==rects[rectim[0]].getX1()-ggsize/2*mp||ggx==rects[rectim[0]].getX2()+ggsize/2*mp )&& ggy==rects[rectim[0]].getY1()-ggsize/2*mp){
                                    ggy-=mp;
                                }
                            } else if (isMultiImpactRect(ggx, ggy)) {
                                Log.d("MainActivity", "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
                                if (!isMultiImpactRect(ggx + getx(xDown, yDown, xMove, yMove) * v, ggy)) {
                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                }

                            } else if (!isImpactRect(ggx, ggy) && !isImpactLine(ggx, ggy)) {
                                //wasImpactRect = false;
                                ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                ggy = ggy + 4 * v * t + 10;
                            } else if (isImpactLine(ggx, ggy) && !isMultiImpactLine(ggx, ggy)) {

                                t = 0;
                                //если столкновение с прямоугольником и прямой
                                if (isImpactRect(ggx, ggy)) {
                                    //если находится на прямой и сталкивается с прямоугольником , как со стеной и при следующем ходе не будет столкновения с прямоугольником
                                    if (!isImpactRect(ggx + getx(xDown, yDown, xMove, yMove) * v, ggy - v) && ggy + ggsize/2*mp > rects[rectim[0]].getY1() && ggy - ggsize/2*mp < rects[rectim[0]].getY2()) {
                                        //относительно расположения прямой , прямоугольника и гг
                                        if (((getx(xDown, yDown, xMove, yMove) > 0 && isUp(lineim[0])) || (getx(xDown, yDown, xMove, yMove) < 0 && !isUp(lineim[0])))) {
                                            //движение по прямой
                                            ggx = ggx - deltaX[lineim[0]] * v;
                                            ggy = ggy - deltaY[lineim[0]] * v;
                                        } else if (((getx(xDown, yDown, xMove, yMove) > 0 && !isUp(lineim[0])) || (getx(xDown, yDown, xMove, yMove) < 0 && isUp(lineim[0])))) {
                                            //движение по прямой
                                            ggx = ggx + deltaX[lineim[0]] * v;
                                            ggy = ggy + deltaY[lineim[0]] * v;
                                        }
                                        //если находится на прямоугольнике(сверху) и при следующем ходе не будет столкновения с прямоугольником
                                    } else if (!isImpactRect(ggx + getx(xDown, yDown, xMove, yMove) * ggsize/2*mp / 6, ggy - v) && ggx > rects[rectim[0]].getX1() && ggx < rects[rectim[0]].getX2()) {
                                        //если находится слева от  прямой
                                        if (isLeft(ggx + ggsize/2*mp, ggy)) {
                                            //если прямая напрвленна вверх _/
                                            if (isUp(lineim[0])) {
                                                //если курсор направлен вправо
                                                if (getx(xDown, yDown, xMove, yMove) > 0) {
                                                    //движение по прямой
                                                    Log.i("dobmdobm", "000000000000000000000000000000000000000000000001111111111111111111111111   " + deltaX[lineim[0]] / mp);
                                                    ggx = ggx - deltaX[lineim[0]] * v;
                                                    ggy = ggy - deltaY[lineim[0]] * v;
                                                    //если курсор направлен влево
                                                } else {
                                                    //движение по поверхности прямоугольника
                                                    ggx += getx(xDown, yDown, xMove, yMove) * v;
                                                }
                                                //если прямая направлена вниз /_
                                            } else {
                                                //если при следующем ходе не будет столкновения
                                                if (!isImpactLine(ggx + getx(xDown, yDown, xMove, yMove) * v, ggy)) {
                                                    //движение по поверхности прямоугольника
                                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                                }
                                            }
                                            //если находится справа от прямой (тот же код , с противоположными значениями ">" и "isUp"
                                        } else {
                                            if (!isUp(lineim[0])) {
                                                if (getx(xDown, yDown, xMove, yMove) < 0) {
                                                    ggx = ggx - deltaX[lineim[0]] * v;
                                                    ggy = ggy - deltaY[lineim[0]] * v;
                                                } else {
                                                    ggx += getx(xDown, yDown, xMove, yMove) * v;
                                                }
                                            } else {
                                                if (!isImpactLine(ggx + getx(xDown, yDown, xMove, yMove) * v, ggy)) {
                                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                                }
                                            }
                                        }
                                        //если находится на прямоугольнике(сверху) и при следующем ходе НЕ БУДЕТ столкновение с прямоугольником
                                    } else if (!isImpactRect(ggx + getx(xDown, yDown, xMove, yMove) * v, ggy ) && ggx > rects[rectim[0]].getX1() && ggx < rects[rectim[0]].getX2()) {
                                        //перенос гг на поверхность прямоугольника
                                        ggy = rects[rectim[0]].getY1() - ggsize/2*mp;
                                        ggx = ggx - getx(xDown, yDown, xMove, yMove) * v;
                                    }
                                } else {
                                    //wasImpactRect = false;
                                    if (((getx(xDown, yDown, xMove, yMove) > 0 && isUp(lineim[0])) || (getx(xDown, yDown, xMove, yMove) < 0 && !isUp(lineim[0])))) {
                                        ggx = ggx - deltaX[lineim[0]];
                                        ggy = ggy - deltaY[lineim[0]];
                                    } else if (((getx(xDown, yDown, xMove, yMove) > 0 && !isUp(lineim[0])) || (getx(xDown, yDown, xMove, yMove) < 0 && isUp(lineim[0])))) {
                                        ggx = ggx + deltaX[lineim[0]];
                                        ggy = ggy + deltaY[lineim[0]];
                                    }
                                }
                            } else if (isImpactLine(ggx, ggy) && isMultiImpactLine(ggx, ggy) && !isImpactRect(ggx, ggy)) {
                                //wasImpactRect = false;
                                t = 0;
                                //если сверху от пересечения прямых
                                if (((rects[rectim[0]].getY1() - rects[rectim[0]].getY2()) * ggx + (rects[rectim[0]].getX2() - rects[rectim[0]].getX1()) * ggy + rects[rectim[0]].getX1() * rects[rectim[0]].getY2() - rects[rectim[0]].getX2() + rects[rectim[0]].getY1() > 0) && ((rects[rectim[1]].getY1() - rects[rectim[1]].getY2()) * ggx + (rects[rectim[1]].getX2() - rects[rectim[1]].getX1()) * ggy + rects[rectim[1]].getX1() * rects[rectim[1]].getY2() - rects[rectim[1]].getX2() + rects[rectim[1]].getY1() > 0)) {
                                    //проверка на направление , и реализация движения по прямой
                                    if ((getx(xDown, yDown, xMove, yMove) > 0 && isUp(lineim[0])) || (getx(xDown, yDown, xMove, yMove) < 0 && !isUp(lineim[0]))) {
                                        ggx = ggx - deltaX[lineim[0]];
                                        ggy = ggy - deltaY[lineim[0]];
                                    } else if ((getx(xDown, yDown, xMove, yMove) > 0 && isUp(lineim[1])) || (getx(xDown, yDown, xMove, yMove) < 0 && !isUp(lineim[1]))) {
                                        ggx = ggx - deltaX[lineim[1]];
                                        ggy = ggy - deltaY[lineim[1]];
                                    }

                                }
                            }

                        }
                    } else {
                        if (isImpactRect(ggx, ggy)) {
                            //wasImpactRect=true;
                            isJump = false;
                        } else if (isImpactLine(ggx, ggy)) {
                            //wasImpactRect=false;
                            isJump = false;
                        } else if (isImpactDang(ggx, ggy)) {
                            Log.d("dmpboisdb","ddddddddddaaaaaaaaaaaaaannnnnnnnnnnggggggggggggggggggggggggg1");
                            isDead = true;
                            isJump = false;
                        } else {
                            if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) || gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                                ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                            }
                            ggy = ggy - 0.028f * jt * 4.5f * v + 0.01f * jt * jt * ggsize/2*mp / 32;
                            jt++;
                            ///wasImpactRect=false;
                        }
                    }
                } else {      //если есть 2 нажатие
                    if (getx(xDown, yDown, xMove, yMove) != getx(xDown, yDown, xMove, yMove) && gety(xDown, yDown, xMove, yMove) != gety(xDown, yDown, xMove, yMove)) {
                        //если нан
                        t = 0;
                        if (!isImpactRect(ggx, ggy)) {

                        } else {
                            t = 0;
                        }
                        //wasImpactRect=false;
                    }

                    //если не нан
                    if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) && gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                        if (isJump && (isImpactRect(ggx, ggy) || isImpactLine(ggx, ggy)) && !wasImpactRect) {
                            isJump = false;
                            // wasImpactRect=true;
                        } else if (isImpactRect(ggx, ggy) && !isJump) {
                            if (wasImpactRect) {
                                isJump = true;
                                if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) && gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                }
                                t = 0;
                                jt = 0;
                                jt++;
                                ggy = ggy - 0.028f * jt * 4.5f * v + 0.01f * jt * jt * ggsize/2*mp / 32;
                            }
                        } else if (!isImpactRect(ggx, ggy) && !isJump) {
                            if (isImpactLine(ggx, ggy)) {
                                isJump = true;
                                if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) || gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                                    ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                                }
                                t = 0;
                                jt = 0;
                                jt++;
                                ggy = ggy - 0.028f * jt * 4.5f * v + 0.01f * jt * jt * ggsize/2*mp / 32;
                            }
                            //wasImpactRect=false;

                        } else if (isJump && !isImpactRect(ggx, ggy) && !isImpactLine(ggx, ggy)) {
                            if (getx(xDown, yDown, xMove, yMove) == getx(xDown, yDown, xMove, yMove) && gety(xDown, yDown, xMove, yMove) == gety(xDown, yDown, xMove, yMove)) {
                                ggx = ggx + getx(xDown, yDown, xMove, yMove) * v;
                            }
                            t = 0;
                            jt++;
                            ggy = ggy - 0.028f * jt * 4.5f * v + 0.01f * jt * jt * ggsize/2*mp / 32;
                            //wasImpactRect=false;
                        }
                    }
                }
                if (ggy > dy || ggy < 0) {
                    ggy = rects[rectim[0]].getY1() - ggsize/2*mp;
                }
                Log.d("ffdjfn", "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj       " + isJump);
                if (isImpactDang(ggx, ggy)) {
                    Log.d("kmfbksbndspb","dddddddddddddddddddddddaaaaaaaaaaaaaaaaannnnnnnnnnnnnnnnnngggggggggggggggggg2");
                    isDead = true;

                }
                //проверка на пересечение контр. точки
                if (ggx + ggsize/2*mp >= CPs[currentCP + 1].getX1() && ggx - ggsize/2*mp <= CPs[currentCP + 1].getX2() && ggy + ggsize/2*mp >= CPs[currentCP + 1].getY1() & ggx - ggsize/2*mp >= CPs[currentCP + 1].getY2()) {
                    isCP = true;
                }
                if (isCP) {
                    //перемещение фона и гг влево (эффект движения камеры вправо)
                    if (cpt <= 50) {
                        backX -= mp / 2;
                        cpt++;
                        //ggx-=mp/2;
                    } else {
                        cpt = 0;
                        isCP = false;
                        currentCP++;

                    }
                }
                if (currentCP + 1 >= CPs.length) {
                    isFinished = true;
                    currentCP = 0;
                    Log.d("mosmbsopbmjsbo", "ffffffffffffffffffffffffIIIIIIIIIIIIIIIIIIIIFFFFFFFFFFFFF");
                }
                if (isImpactRect(ggx, ggy) && !wasImpactRect) play();
                //проверка на пересечение с туманом
                if (fogs[currentFog].getDirection() == 1 && ggx >= fogs[currentFog].getX1() && ggx <= curFogX && ggy >= fogs[currentFog].getY1() && ggy <= fogs[currentFog].getY2()) {
                    Log.d("bfgg", "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    isDead = true;
                } else if (fogs[currentFog].getDirection() == 2 && ggx >= fogs[currentFog].getX1() && ggx <= fogs[currentFog].getX2() && ggy >= fogs[currentFog].getY1() && ggy <= curFogY) {
                    Log.d("bfgg", "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    isDead = true;
                } else if (fogs[currentFog].getDirection() == 3 && ggx >= curFogX && ggx <= fogs[currentFog].getX2() && ggy >= fogs[currentFog].getY1() && ggy <= fogs[currentFog].getY2()) {
                    Log.d("bfgg", "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    isDead = true;
                } else if (fogs[currentFog].getDirection() == 4 && ggx >= fogs[currentFog].getX1() && ggx <= fogs[currentFog].getX2() && ggy >= curFogY && ggy <= fogs[currentFog].getY2()) {
                    Log.d("bfgg", "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
                    isDead = true;
                }

                //увеличение тумана
                switch (fogs[currentFog].getDirection()) {
                    case 1:
                        curFogX += mp / 15;
                        //проверка на конец тумана
                        if (curFogX >= fogs[currentFog].getX2()) currentFog++;
                        break;
                    case 2:
                        curFogY += mp / 15;
                        //проверка на конец тумана
                        if (curFogY >= fogs[currentFog].getY2()) currentFog++;
                        break;
                    case 3:
                        curFogX -= mp / 15;
                        //проверка на конец тумана
                        if (curFogX <= fogs[currentFog].getX1()) currentFog++;
                        break;
                    case 4:
                        curFogY -= mp / 15;
                        //проверка на конец тумана
                        if (curFogY <= fogs[currentFog].getY1()) currentFog++;
                        break;
                }
            }

            //компановка картинки
            gg.set(ggx-ggsize/2*mp,ggy-ggsize/2*mp,ggx+ggsize/2*mp,ggy+ggsize/2*mp);
            fon.reset();
            //добавление элементов фона в фоновый Path
            for (Line line : lines) {
                fon.moveTo(line.getX1(), line.getY1());
                fon.lineTo(line.getX2(), line.getY2());
            }

            for (RectG rect : rects) {
                fon.addRect(rect.getX1(),rect.getY1(),rect.getX2(),rect.getY2(), Path.Direction.CW);

            }
            for (Fill fill : fills) {
                fon.addPath(fill);
            }

            //проверка на поворот гг
            if(getx(xDown, yDown, xMove, yMove)>0){
                lastTurnWasRight=true;
            }else if(getx(xDown, yDown, xMove, yMove)<0){
                lastTurnWasRight=false;
            }
            if(ggx>=CPs[CPs.length-1].getX2())isFinished=true;
            Log.d("moomwhbpo","ssssssssssssssssssssssssssssssssssssssssssssssssss    "+ wasImpactRect);
            if(isTouched){
                //установка джостика
                rectf.set((int)xDown-2*mp,(int)yDown-2*mp,(int)xDown+2*mp,(int)yDown+2*mp);
                //установка маленького кружка джостика через координаты x/yDown с учетом синуса/косинуса
                mrec.set(xDown+2*mp*getx(xDown,yDown,xMove,yMove)-mp/2,yDown+2*mp*gety(xDown,yDown,xMove,yMove)-mp/2,xDown+2*mp*getx(xDown,yDown,xMove,yMove)+mp/2,yDown+2*mp*gety(xDown,yDown,xMove,yMove)+mp/2);
            }else{
                rectf.setEmpty();
                mrec.setEmpty();
            }
            if(isImpactRect(ggx,ggy))wasImpactRect=true;
            else wasImpactRect=false;
            return null;
        }
    }





    //метод используемый GameActivity для передачи факта нажатия
    public void setTouched(boolean isTouched){
        this.isTouched=isTouched;
    }



    //проверка на столкновение с объектами фона
    public boolean isImpactRect(float x,float y){
        boolean []impacts=new boolean[rects.length];
        for (int i=0;i<rects.length;i++){
            //проверка на столкновение с rect через вершины

            if(x+ggsize/2*mp<rects[i].getX1()||x-ggsize/2*mp>rects[i].getX2()||y+ggsize/2*mp<rects[i].getY1()||y-ggsize/2*mp>rects[i].getY2()){
                //записьв массив столкновений если находится внутри rect*
                impacts[i]=false;
            }
            else{
                rectim[0]=i;
                impacts[i]=true;
            }
        }
        boolean impact=false;//наличие столкновения воообще
        //обобщение результатов проверки в impact
        for(int i =0;i<rects.length;i++){
            impact=impacts[i]||impact;
        }
        return impact;
    }
    public boolean isMultiImpactRect(float x,float y){
        boolean []impacts=new boolean[rects.length];
        int rectnumber=0;
        for (int i=0;i<rects.length;i++){
            //проверка на столкновение с rect через вершины

            if(x+ggsize/2*mp<rects[i].getX1()||x-ggsize/2*mp>rects[i].getX2()||y+ggsize/2*mp<rects[i].getY1()||y-ggsize/2*mp>rects[i].getY2()){
                //записьв массив столкновений если находится внутри rect
                impacts[i]=false;
            }
            else{
                rectim[rectnumber]=i;
                impacts[i]=true;
                rectnumber++;
            }
        }
        boolean impact=false;
        boolean multiim=false;
        //наличие столкновения воообще
        //обобщение результатов проверки в impact
        for(int i =0;i<rects.length;i++){
            if(impact&&impacts[i]){
                multiim=true;
            }
            impact=impacts[i]||impact;
        }
        return multiim;
    }








    public boolean isImpactDang(float x,float y ){ ;
        for (Danger danger : dangers) {
            float d = (float) Math.sqrt((x - danger.getX()) * (x - danger.getX()) + (y - danger.getY()) * (y - danger.getY()));
            if (d <=ggsize/2* mp + danger.getR()) return true;
        }
        return false;
    }








    public boolean isImpactLine(float x,float y){
        boolean[] impacts =new boolean[lines.length];
        int a=0;
        for (int i=0;i<lines.length;i++){
            //если y начала отрезка меньше чем y конца (отрезок напрвлен вправо вниз)
            if(lines[i].getY2()>lines[i].getY1()) {
                //если гг находится внутри квадрата с данным отрезком ро диагонали
                if (x>=lines[i].getX1()&&x<=lines[i].getX2()&&y>=lines[i].getY1()&&y<=lines[i].getY2()) {
                    double d= (Math.abs((lines[i].getY2()-lines[i].getY1())*x-(lines[i].getX2()-lines[i].getX1())*y+lines[i].getX2()*lines[i].getY1()-lines[i].getY2()*lines[i].getX1()))/(Math.sqrt((lines[i].getY2()-lines[i].getY1())*(lines[i].getY2()-lines[i].getY1())+(lines[i].getX2()-lines[i].getX1())*(lines[i].getX2()-lines[i].getX1())));
                    if (d <=ggsize/2* mp) {
                        impacts[i] = true;
                        lineim[a] = i ;
                        a++;
                    }
                    else impacts[i] = false;
                }
                else impacts[i]=false; }
            //если у начала отрезка больше чем у конца(отрезок направлен вправо вверх)
            else if(lines[i].getY2()<lines[i].getY1()){
                //если гг находится внутри квадрата с данным отрезком по диагонали
                if (x>=lines[i].getX1()&&x<=lines[i].getX2()&&y<=lines[i].getY1()&&y>=lines[i].getY2()) {
                    double d= (Math.abs((lines[i].getY2()-lines[i].getY1())*x-(lines[i].getX2()-lines[i].getX1())*y+lines[i].getX2()*lines[i].getY1()-lines[i].getY2()*lines[i].getX1()))/(Math.sqrt((lines[i].getY2()-lines[i].getY1())*(lines[i].getY2()-lines[i].getY1())+(lines[i].getX2()-lines[i].getX1())*(lines[i].getX2()-lines[i].getX1())));
                    if (d <=ggsize/2* mp) {
                        impacts[i ] = true;
                        lineim[a] = i ;
                        a++;
                    }else impacts[i] = false;
                }
            }
        }

        boolean impact=false;//наличие столкновения воообще
        //обобщение результатов проверки в impact
        for(int i =0;i<lines.length;i++){
            if(impact&&impacts[i]){
            }
            impact=impacts[i]||impact;
        }
        return impact;
    }








    public boolean isMultiImpactLine(float x,float y){
        boolean[] impacts =new boolean[lines.length];
        int a=0;
        for (int i=0;i<lines.length;i++){
            //если y начала отрезка меньше чем y конца (отрезок напрвлен вправо вниз)
            if(lines[i].getY2()>lines[i].getY1()) {
                //если гг находится внутри квадрата с данным отрезком ро диагонали
                if (x>=lines[i].getX1()&&x<=lines[i].getX2()&&y>=lines[i].getY1()&&y<=lines[i].getY2()) {
                    double d= (Math.abs((lines[i].getY2()-lines[i].getY1())*x-(lines[i].getX2()-lines[i].getX1())*y+lines[i].getX2()*lines[i].getY1()-lines[i].getY2()*lines[i].getX1()))/(Math.sqrt((lines[i].getY2()-lines[i].getY1())*(lines[i].getY2()-lines[i].getY1())+(lines[i].getX2()-lines[i].getX1())*(lines[i].getX2()-lines[i].getX1())));
                    if (d <=ggsize/2* mp) {
                        impacts[i] = true;
                        lineim[a] = i ;
                        a++;
                    }
                    else impacts[i] = false;
                }
                else impacts[i]=false; }
            //если у начала отрезка больше чем у конца(отрезок направлен вправо вверх)
            else if(lines[i].getY2()<lines[i].getY1()){
                //если гг находится внутри квадрата с данным отрезком по диагонали
                if (x>=lines[i].getX1()&&x<=lines[i].getX2()&&y<=lines[i].getY1()&&y>=lines[i].getY2()) {
                    double d= (Math.abs((lines[i].getY2()-lines[i].getY1())*x-(lines[i].getX2()-lines[i].getX1())*y+lines[i].getX2()*lines[i].getY1()-lines[i].getY2()*lines[i].getX1()))/(Math.sqrt((lines[i].getY2()-lines[i].getY1())*(lines[i].getY2()-lines[i].getY1())+(lines[i].getX2()-lines[i].getX1())*(lines[i].getX2()-lines[i].getX1())));
                    if (d <= ggsize/2*mp) {
                        impacts[i ] = true;
                        lineim[a] = i ;
                        a++;
                    }else impacts[i] = false;
                }
            }
        }

        boolean impact=false;//наличие столкновения воообще
        boolean multiimpact=false;      //наличие множественного столкновения
        //обобщение результатов проверки в impact
        for(int i =0;i<lines.length;i++){
            if(impact&&impacts[i]){
                multiimpact=true;
            }
            impact=impacts[i]||impact;
        }
        return multiimpact;
    }



    // проверка восходящая прямая или нисходящая
    //       / или\
    public boolean isUp(int line){
        return lines[line].getY1() > lines[line].getY2();
    }



    //проверка  расположения гг относительно прямой столкновения
    public boolean isLeft(float x,float y){
        if((x-lines[lineim[0]].getX1())/(lines[lineim[0]].getX2()-lines[lineim[0]].getX1())-(y-lines[lineim[0]].getY1())/(lines[lineim[0]].getY2()-lines[lineim[0]].getY1())<0){
            Log.d("MainActivity","llllllllllllllllllllllllllllllllllllllllllllllllllllllllleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeft");
            return true;
        }else{
            Log.d("MainActivity","rRRRRRRRRRRRRRRRRRRRRRRrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrriiiiiiiiiiiiiiiiiiiiiiiiiiiigght");
            return false;
        }
    }




    // коэфициент общего движения гг (по сути косинус или синус угла поворота джостика)
    private float getx(float x1, float y1 ,float  x2,float y2) {
        float gip = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (x2 - x1) / (gip);
    }
    private float gety(float x1, float y1 ,float  x2,float y2){
        float gip = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return (y2 - y1) / (gip);
    }


    public void play(){
        Log.d("moefmboebmobm","pppppppppppppppppppppppppppllllllllllllllllllllaaaaaaaaaaaaaaayyyyyyyyyyyyyyy");
        sp.play(woodId,musiclevel+0,musiclevel+0,1,0,1);
    }


    //метод , вызывающийся при нажатии на кнопку паузы
    public void setHeroToBegin(){
        ggx=CPs[currentCP].getX2()+3*mp;
        ggy=(CPs[currentCP].getY1()+CPs[currentCP].getY2())/2;
        Log.d("MainActivity","begin");
    }

    public int getCurrentCP() {
        return currentCP;
    }
    public int getCurrentFog() {
        return currentFog;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isEndOfDead() {
        return isEndOfDead;
    }


    class Load implements SoundPool.OnLoadCompleteListener{

        @Override
        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

        }
    }

}
