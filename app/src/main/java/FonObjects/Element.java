package FonObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.fyu.MainActivity;


public class Element {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private  float angel;
    private int backPictureId;
    private int frontPictureId;
    private Bitmap back;
    private Bitmap front;


    public Element(float x1, float y1, float x2, float y2,float angel, int frontPictureId, int backPictureId,Context c) {
        x1 *= MainActivity.mp;
        y1 *= MainActivity.mp;
        x2 *= MainActivity.mp;
        y2 *= MainActivity.mp;
        this.angel=angel;
        this.backPictureId = backPictureId;
        this.frontPictureId=frontPictureId;
        Matrix m =new Matrix();
        Bitmap back1=BitmapFactory.decodeResource(c.getResources(),this.backPictureId);
        m.setRotate(angel,x1,y2);
        Log.d("dfebeg",this.backPictureId+"                         "+this.frontPictureId);
        back=Bitmap.createScaledBitmap(back1,(int)(x2-x1),(int)Math.abs(y2-y1),true);
        back=Bitmap.createBitmap(back,0,0,back.getWidth(),back.getHeight(),m,true);
        Log.d("dfebeg",this.backPictureId+"                         "+this.frontPictureId);

        Bitmap front1=BitmapFactory.decodeResource(c.getApplicationContext().getResources(),this.frontPictureId);
        m.setRotate(angel,x1,y2);
        front=Bitmap.createScaledBitmap(front1,(int)(x2-x1),(int)Math.abs(y2-y1),true);
        front=Bitmap.createBitmap(front,0,0,front.getWidth(),front.getHeight(),m,true);

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

    }
   /*public void move(float dist){
        x1-=dist;
        x2-=dist;

    }*/


    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public int getBackPictureId() {
        return backPictureId;
    }

    public int getFrontPictureId() {
        return frontPictureId;
    }

    public float getAngel() {
        return angel;
    }


    public Bitmap getBack() {
        return back;
    }

    public Bitmap getFront() {
        return front;
    }



}
