package Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import FonObjects.CP;
import FonObjects.Danger;
import FonObjects.Element;
import FonObjects.Fill;
import FonObjects.Fog;
import FonObjects.Line;
import FonObjects.RectG;

public class Provider {
    static String LVL_T_NAME ="tv8";
    static String LV_ID="id";
    static String LV_FON_ID="fonId";
    static String LVL_V_FON_ID="lvlVFonId";
    static String CP_TOPS ="CPtops";
    static String CUR_CP="currentCP";
    static String FILL_TOPS_ANGELS="fillTopsAndAngels";
    static String RECT_TOPS="recttops";
    static String LINE_TOPS="linetops";
    static String CUR_FOG="currentFog";
    static String FOG_TOPS_DIRS="fogTopsAndDirs";
    static String ELEM_TOPS="elemTops";
    static String ELEM_PICTS="elemPictures";
    static String DANGERS_XY_AND_R="dangersXYAndR";
    static String IS_DONE="isDone";


    static String DT_T_NAME="dt8";
    static String CUR_LVL="currentLevel";
    static String SOUND="soundLevel";
    static String MUSIC="musicLevel";
    static String LVLS_COUNT="levelsCount";
    static String IS_FIRST="isFirst";

    public boolean isFirst=false;

    public   int currentLvl;
    private  int lvlsCount;
    private  int sound;
    private  int music;
    private int []lvlVIds;

    private SQLiteDatabase db;

    private DbHelper dbHelper;
    private Context c;
    public Provider(Context c){
        this.c=c;
        this.dbHelper = new DbHelper(c);
        db=this.dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM "+DT_T_NAME,null);
        cursor.moveToFirst();
        lvlsCount=cursor.getInt(cursor.getColumnIndex(LVLS_COUNT));
        currentLvl=cursor.getInt(cursor.getColumnIndex(CUR_LVL));
        sound=cursor.getInt(cursor.getColumnIndex(SOUND));
        music=cursor.getInt(cursor.getColumnIndex(MUSIC));
        if(cursor.getInt(cursor.getColumnIndex(IS_FIRST))==0){
            isFirst=false;
        }else if(cursor.getInt(cursor.getColumnIndex(IS_FIRST))==1){
            isFirst=true;
            db.execSQL("UPDATE "+DT_T_NAME+" SET "+IS_FIRST+"=0");
        }
        lvlVIds=new int[lvlsCount];
        Cursor ci=db.rawQuery("SELECT "+LVL_V_FON_ID+" FROM "+LVL_T_NAME,null);
        ci.moveToFirst();
        int i=0;
        while (i<lvlVIds.length-1) {
            lvlVIds[i]=ci.getInt(ci.getColumnIndex(LVL_V_FON_ID));
                ci.moveToNext();
            i++;
        }
        ci.close();
        cursor.close();
    }
    void setCurCp(int curCp, int curFog, int id){
        db.execSQL("UPDATE "+ LVL_T_NAME +" SET "+CUR_CP+"="+curCp+", "+CUR_FOG+"="+curFog+" WHERE "+LV_ID+"="+id);
        Log.d("kgufufu","cccccccccccuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuur"+curCp);
    }
    void setCompleted(int id){
        Log.d("bkofepbkef","ccccccccccccoooooooooooommmmmmmmmppppppplllllleeeeeeeeeeeeeeeeeeeeettttteeeeeeeeeeeeeeeeeeeeedddddddddddd");
        db.execSQL("UPDATE "+ LVL_T_NAME +" SET "+CUR_CP+"= 0, "+CUR_FOG+"= 0,"+IS_DONE+"= 1 WHERE "+LV_ID+"="+id);
        Cursor cursor=db.rawQuery("SELECT "+CUR_LVL+", "+LVLS_COUNT+" FROM "+DT_T_NAME,null);
        cursor.moveToFirst();
        if (cursor.getInt(cursor.getColumnIndex(LVLS_COUNT))>cursor.getInt(cursor.getColumnIndex(CUR_LVL))){
            db.execSQL("UPDATE "+DT_T_NAME+" SET "+CUR_LVL+"="+cursor.getInt(cursor.getColumnIndex(CUR_LVL))+1);}
        cursor.close();
    }
    public Level getLevel(int id){
        String sql = "SELECT * FROM "+ LVL_T_NAME +" WHERE "+LV_ID+" = "+id;
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            int fonId=cursor.getInt(cursor.getColumnIndex(LV_FON_ID));
            String sCPtops =cursor.getString(cursor.getColumnIndex(CP_TOPS));
            int curCP=cursor.getInt(cursor.getColumnIndex(CUR_CP));
            String sfillTopsAndAngels=cursor.getString(cursor.getColumnIndex(FILL_TOPS_ANGELS));
            String srectTops=cursor.getString(cursor.getColumnIndex(RECT_TOPS));
            String slineTops =cursor.getString(cursor.getColumnIndex(LINE_TOPS));
            String selemTops =cursor.getString(cursor.getColumnIndex(ELEM_TOPS));
            String selemPicts=cursor.getString(cursor.getColumnIndex(ELEM_PICTS));
            String sdangerTops =cursor.getString(cursor.getColumnIndex(DANGERS_XY_AND_R));
            int currentFog=cursor.getInt(cursor.getColumnIndex(CUR_FOG));
            String sfogTopsAndDirs=cursor.getString(cursor.getColumnIndex(FOG_TOPS_DIRS));
            int  intIsDone=cursor.getInt(cursor.getColumnIndex(IS_DONE));
            cursor.close();
            boolean isDone = false;
            if(intIsDone==0)isDone=false;
            else if(intIsDone==1)isDone=true;
            ArrayList<CP>CPs =new ArrayList<>();
            if(sCPtops!=null) {
                String[] splitCPtops = sCPtops.split(",");
                for (int i = 0; i < splitCPtops.length; i += 4) {
                    CPs.add(new CP(Float.parseFloat(splitCPtops[i]), Float.parseFloat(splitCPtops[i + 1]), Float.parseFloat(splitCPtops[i + 2]), Float.parseFloat(splitCPtops[i + 3]), 0));
                }
            }
            ArrayList<Fill> fills = new ArrayList<>();
            if(sfillTopsAndAngels!=null) {
                String[] splitFills = sfillTopsAndAngels.split(",");
                for (int i = 0; i < splitFills.length; i += 5) {
                    fills.add(new Fill(Float.parseFloat(splitFills[i]), Float.parseFloat(splitFills[i + 1]), Float.parseFloat(splitFills[i + 2]), Float.parseFloat(splitFills[i + 3]), Float.parseFloat(splitFills[i + 4])));
                }
            }
            ArrayList<RectG> rects=new ArrayList<>();
            if(srectTops!=null) {
                String[] splitRectTops = srectTops.split(",");
                for (int i = 0; i < splitRectTops.length; i += 4) {
                    rects.add(new RectG(Float.parseFloat(splitRectTops[i]), Float.parseFloat(splitRectTops[i + 1]), Float.parseFloat(splitRectTops[i + 2]), Float.parseFloat(splitRectTops[i + 3])));
                }
            }
            ArrayList<Line> lines =new ArrayList<>();
            if(slineTops!=null) {
                String[] splitLinesTops = slineTops.split(",");
                for (int i = 0; i < splitLinesTops.length; i += 4) {
                    lines.add(new Line(Float.parseFloat(splitLinesTops[i]), Float.parseFloat(splitLinesTops[i + 1]), Float.parseFloat(splitLinesTops[i + 2]), Float.parseFloat(splitLinesTops[i + 3])));
                }
            }
            ArrayList<Fog> fogs=new ArrayList<>();
            if(sfogTopsAndDirs!=null) {
                String[] splitFogTopsAndDirs = sfogTopsAndDirs.split(",");
                for (int i = 0; i < splitFogTopsAndDirs.length; i += 6) {
                    fogs.add(new Fog(Float.parseFloat(splitFogTopsAndDirs[i]), Float.parseFloat(splitFogTopsAndDirs[i + 1]), Float.parseFloat(splitFogTopsAndDirs[i + 2]), Float.parseFloat(splitFogTopsAndDirs[i + 3]), Integer.parseInt(splitFogTopsAndDirs[i + 4]), Boolean.parseBoolean(splitFogTopsAndDirs[i + 5])));
                }
            }
            ArrayList<Danger> dangers=new ArrayList<>();
            if(sdangerTops!=null) {
                String[] splitDangers = sdangerTops.split(",");
                for (int i = 0; i < splitDangers.length; i += 3) {
                    dangers.add(new Danger(Float.parseFloat(splitDangers[i]), Float.parseFloat(splitDangers[i + 1]), Float.parseFloat(splitDangers[i + 2]),c));
                }
            }
            ArrayList<Element> elements=new ArrayList<>();
            if(selemTops!=null&&selemPicts!=null) {
                String[] splitElementT = selemTops.split(",");
                String[] splitElementP= selemPicts.split(",");
                for (int i = 0; i < splitElementP.length/2; i ++) {
                    elements.add(new Element(Float.parseFloat(splitElementT[i*5]), Float.parseFloat(splitElementT[i*5 + 1]), Float.parseFloat(splitElementT[i*5 + 2]), Float.parseFloat(splitElementT[i*5 + 3]), Float.parseFloat(splitElementT[i*5 + 4]), Integer.parseInt(splitElementP[i*2]),Integer.parseInt(splitElementP[i*2+1]),c));
                }
            }
            if(isDone){
                setCompleted(id);
                curCP=0;}
            Level lvl= new Level (id,fonId,CPs,curCP,fills,rects,lines,currentFog,fogs,dangers,elements,isDone);
            Log.d("gvsdgvs","llllllllllllllllllllllllllllllllllllllvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvlllllllllllllllllllllllllllll");
            return lvl;
        }else{
            Log.d("kdvjdkh","cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
            return null;}
    }

    public  int getCurrentLvl() {
        return currentLvl;
    }

    public int getLvlsCount() {
        return lvlsCount;
    }

    public float getMusic() {
        return (float)music;
    }

    public  float getSound() {
        return (float)sound;
    }

    public void setMusic(int music) {
        this.music = music;
        db.execSQL("UPDATE "+DT_T_NAME+" SET "+MUSIC+" = "+music+" WHERE "+MUSIC+" NOT NULL");
        Log.d("dofkmepnkefh","mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm          "+music);
    }

    public void setSound(int sound) {
        this.sound = sound;
        db.execSQL("UPDATE "+DT_T_NAME+" SET "+SOUND+" = "+sound+" WHERE "+SOUND+" NOT NULL");
    }
    public int getLvlFonId(int id){
        return lvlVIds[id];
    }
}
