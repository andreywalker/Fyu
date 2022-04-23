package Data;

import android.content.Context;

import java.util.ArrayList;

import FonObjects.CP;
import FonObjects.Danger;
import FonObjects.Element;
import FonObjects.Fill;
import FonObjects.Fog;
import FonObjects.Line;
import FonObjects.RectG;

public class Level {
    private int id;
    private int fonId;
    private ArrayList<CP> CPs;
    private int currentCP;
    private ArrayList<Fill> fills;
    private ArrayList<RectG> rects;
    private ArrayList<Line> lines;
    private int currentFog;
    private ArrayList<Fog> fogs;
    private ArrayList<Element> elements;
    private ArrayList<Danger> dangers;
    private boolean isDone;

    public Level(int id, int fonId, ArrayList<CP> CPs, int currentCP, ArrayList<Fill> fills, ArrayList<RectG> rects, ArrayList<Line> lines,int currentFog, ArrayList<Fog> fogs, ArrayList<Danger> dangers,ArrayList<Element> elements,boolean isDone) {
        this.id = id;
        this.fonId = fonId;
        this.CPs = CPs;
        this.currentCP = currentCP;
        this.fills = fills;
        this.rects = rects;
        this.lines = lines;
        this.currentFog=currentFog;
        this.fogs = fogs;
        this.dangers=dangers;
        this.elements=elements;
        this.isDone=isDone;
    }

    public int getFonId() {
        return fonId;
    }

    public CP[] getCPs() {
        return (CP[])CPs.toArray(new CP[0]);
    }

    public int getCurrentCP() {
        return currentCP;
    }

    public Fill[] getFills() {
        return (Fill[])fills.toArray(new Fill[0]);
    }

    public RectG[] getRects() {
        return (RectG[])rects.toArray(new RectG[0]);
    }

    public Line[] getLines() {
        return (Line[])lines.toArray(new Line[0]);
    }

    public Fog[] getFogs() {
        return (Fog[])fogs.toArray(new Fog[0]);
    }

    public Element[] getElements() {
        return (Element[])elements.toArray(new Element[0]);
    }

    public Danger[] getDangers() {
        return (Danger[])dangers.toArray(new Danger[0]);
    }

    public int getCurrentFog() {
        return currentFog;
    }

    public boolean isDone() {
        return isDone;
    }



    public void setCurrentCP(int currentCP, Context c) {
        this.currentFog=CPs.get(currentCP).getFirstFog();
        this.currentCP = currentCP;
        Provider p=new Provider(c);
        p.setCurCp(currentCP,currentFog,id);
    }
    public void setDone(Context c){
        this.currentFog=0;
        this.isDone=true;
        this.currentCP=0;
        Provider p=new Provider(c);
        p.setCompleted(id);
    }
}

