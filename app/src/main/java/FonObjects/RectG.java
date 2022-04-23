package FonObjects;


import com.example.fyu.MainActivity;

public class RectG  {
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    public RectG(float left, float top, float right, float bottom) {
        this.x1=left* MainActivity.mp;
        this.x2=right* MainActivity.mp;
        this.y1=top* MainActivity.mp;
        this.y2=bottom* MainActivity.mp;
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

}
