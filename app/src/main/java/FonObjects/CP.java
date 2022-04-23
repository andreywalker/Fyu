package FonObjects;


import com.example.fyu.MainActivity;

public class CP {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private int firstFog;
    private boolean completed= false;

    public CP(float x1, float y1, float x2, float y2, int firstFog) {
        this.x1 = x1* MainActivity.mp;
        this.y1 = y1* MainActivity.mp;
        this.x2 = x2* MainActivity.mp;
        this.y2 = y2* MainActivity.mp;
        this.firstFog=firstFog;
    }
    /*public void move(float dist){
        x1-=dist;
        x2-=dist;
    }*/

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public int getFirstFog() {
        return firstFog;
    }
}
