package FonObjects;


import com.example.fyu.MainActivity;

public class Line {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private boolean up;
    private boolean impact;

    public Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1* MainActivity.mp;
        this.y1 = y1* MainActivity.mp;
        this.x2 = x2* MainActivity.mp;
        this.y2 = y2* MainActivity.mp;
        this.up= y1 <= y2;
    }
   /* public void move(float dist){
        x1-=dist;
        x2-=dist;
    }*/

    public void setImpact(boolean impact) {
        this.impact = impact;
    }
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

    public boolean isUp() {
        return up;
    }

    public boolean isImpact() {
        return impact;
    }

    //получение единицы длины перемещения на оси Х по выбранной прямой за прорисоку
    //нужно для реализации движения по прямой
    public float getDeltaX(float v){
        float x=x2;
        float y=y2;
        float d = (float) Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        while(d>=v){
            x=(x1+x)/2;
            y=(y1+y)/2;
            d=(float) Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
        }
        if(isUp())return d;
        else return -d;
    }


    //получение единицы длины перемещения на оси Х по выбранной прямой за прорисоку
    public float getDeltaY(float v){
        float x=x2;
        float y=y2;
        float d = (float) Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        while(d>=v){
            x=(x1+x)/2;
            y=(y1+y)/2;
            d=(float) Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
        }
        if(isUp())return d;
        else return d;
    }

}
