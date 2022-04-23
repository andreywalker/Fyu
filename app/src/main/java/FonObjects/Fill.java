package FonObjects;

import android.graphics.Matrix;
import android.graphics.Path;

import com.example.fyu.MainActivity;


public class Fill extends Path {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float angel;

    public Fill(float x1, float y1, float x2, float y2, float angel) {
        x1 *= MainActivity.mp;
        y1 *= MainActivity.mp;
        x2 *= MainActivity.mp;
        y2 *= MainActivity.mp;
        this.angel = angel;
        addRect(x1,y1,x2,y2,Direction.CW);
        Matrix m = new Matrix();
        m.setRotate(angel,x1,y1);
        transform(m);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
   /* public void move(float dist){
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

    public float getAngel() {
        return angel;
    }
}
