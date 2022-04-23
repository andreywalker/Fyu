package FonObjects;


import com.example.fyu.MainActivity;

public class Fog {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private int direction;      //1-вправо, 2-вниз, 3-влево, 4-вверх
    private boolean completed=false;
    private boolean first;      //находится ли туман в начале контр точки

    public Fog(float x1, float y1, float x2, float y2, int direction, boolean first) {
        this.x1 = x1* MainActivity.mp;
        this.y1 = y1* MainActivity.mp;
        this.x2 = x2* MainActivity.mp;
        this.y2 = y2* MainActivity.mp;
        this.direction = direction;
        this.first = first;
    }
    /*public void move(float dist){
        x1-=dist;
        x2-=dist;
    }*/


    public void setCompleted(boolean completed) {
        if (isFirst()) {this.completed = completed;}
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

    public int getDirection() {
        return direction;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isFirst() {
        return first;
    }
}
