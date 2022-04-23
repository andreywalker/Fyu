package FonObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.fyu.MainActivity;
import com.example.fyu.R;


public class Danger {
    private float x;
    private float y;
    private float r;
    private int type;
    private Bitmap d;


    public Danger(float x, float y, float r, Context c ) {
        this.x = x* MainActivity.mp;
        this.y = y* MainActivity.mp;
        this.r = r* MainActivity.mp;
        type =(int)(Math.random()*3);
        switch (type){
            case 0 :
                Bitmap b= BitmapFactory.decodeResource(c.getResources(), R.drawable.dang1);
                d=Bitmap.createScaledBitmap(b,(int)this.r*2,(int)this.r*2,true);
            break;
            case 1:
                Bitmap b1= BitmapFactory.decodeResource(c.getResources(), R.drawable.dang2);
                d=Bitmap.createScaledBitmap(b1,(int)this.r*2,(int)this.r*2,true);
            break;
            case 2:
                Bitmap b2= BitmapFactory.decodeResource(c.getResources(), R.drawable.dang3);
                d=Bitmap.createScaledBitmap(b2,(int)this.r*2,(int)this.r*2,true);
            break;
        }
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public int getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return d;
    }


}
