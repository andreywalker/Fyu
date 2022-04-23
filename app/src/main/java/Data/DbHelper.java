package Data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.fyu.R;

public class DbHelper extends SQLiteOpenHelper {
    @Override
    public synchronized void close() {
        super.close();
    }

    private static final String DB_NAME="lv215";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+Provider.DT_T_NAME+"("
        +Provider.CUR_LVL+" integer , "
        +Provider.LVLS_COUNT+" integer , "
        +Provider.IS_FIRST+" integer , "
        +Provider.SOUND+" integer, "
        +Provider.MUSIC+" integer )"
        );

        db.execSQL("insert into "+Provider.DT_T_NAME+" ("
        +Provider.CUR_LVL+", "
        +Provider.LVLS_COUNT+", "
        +Provider.IS_FIRST+", "
        +Provider.SOUND+", "
        +Provider.MUSIC+" ) values (3, 3, 1, 100, 100)");

        db.execSQL("create table if not exists "+Provider.LVL_T_NAME +"("
                +Provider.LV_ID+" integer primary key autoincrement, "
                +Provider.LV_FON_ID+" integer, "
                +Provider.LVL_V_FON_ID+" integer, "
                +Provider.CP_TOPS+" text, "
                +Provider.CUR_CP +" integer, "
                +Provider.FILL_TOPS_ANGELS+" text , "
                +Provider.RECT_TOPS+" text, "
                +Provider.LINE_TOPS+" integer, "
                +Provider.CUR_FOG+" integer ,"
                +Provider.FOG_TOPS_DIRS+" text, "
                +Provider.ELEM_TOPS+" text ,"
                +Provider.ELEM_PICTS+" text,"
                +Provider.DANGERS_XY_AND_R+" text , "
                +Provider.IS_DONE+" integer )");
        db.execSQL("insert into "+Provider.LVL_T_NAME +" ( "
                +Provider.LV_FON_ID+", "
                +Provider.LVL_V_FON_ID+", "
                +Provider.CP_TOPS+", "
                +Provider.CUR_CP+", "
                +Provider.FILL_TOPS_ANGELS+", "
                +Provider.RECT_TOPS+", "
                +Provider.LINE_TOPS+", "
                +Provider.CUR_FOG+" ,"
                +Provider.FOG_TOPS_DIRS+", "
                +Provider.ELEM_TOPS+", "
                +Provider.ELEM_PICTS+", "
                +Provider.DANGERS_XY_AND_R+", "
                +Provider.IS_DONE+ " ) values (" +
                +R.drawable.fon+", "
                +R.drawable.level+", "
                + "'-1,0,0,20,   25,0,26,20,    50,0,51,20,      76,0,78,20    ,99,0,100,20,     125,0,126,20      ,150,0,151,20',"
                + "0,"
                + "'10,-1,20,5,0 ',"
                +"'-20,19,160,40,   17,17,30,20,   68,6,90,7,   78,0,79,6,    60,17,95,20,   112,14,120,15,    122,12,130,13,     132,10,140,11   ',"
                + " '10,20,17,14 ',"
                +" 0,"
                +"'-10,0,23,20,1,true,      23,0,48,21,1,true     ,48,0,73,21,1,true,    73,0,98,21,1,true,   98,0,123,21,1,true,   123,0,150,21,1,true',"
                +"'-1,17,10,19.5,0,     17,13,30,17,0,     60,-1,70,3,45,    68,2,90,7,0    ', "
                +"'"+R.drawable.zemlya+"," +R.drawable.zemlya_b+","+R.drawable.zemlya2+"," +R.drawable.zemlya2b+","+R.drawable.derevo_2+"," +R.drawable.derevo_2_b+","+R.drawable.zemlya3+"," +R.drawable.zemlya3b+"', "
                + "'35,17,2,     73,17,2,     88,17,2,   110,18,2,   118,18,2,   122,18,2,   126,18,2,   130,18,2,   134,18,2,   138,18,2,    142,18,2,      146,18,3  ',"
                + " 0)");
        db.execSQL("insert into "+Provider.LVL_T_NAME +" ( "
                +Provider.LV_FON_ID+", "
                +Provider.LVL_V_FON_ID+", "
                +Provider.CP_TOPS+", "
                +Provider.CUR_CP+", "
                +Provider.FILL_TOPS_ANGELS+", "
                +Provider.RECT_TOPS+", "
                +Provider.LINE_TOPS+", "
                +Provider.CUR_FOG+" ,"
                +Provider.FOG_TOPS_DIRS+", "
                +Provider.ELEM_TOPS+", "
                +Provider.ELEM_PICTS+", "
                +Provider.DANGERS_XY_AND_R+", "
                +Provider.IS_DONE+ " ) values (" +
                +R.drawable.fon2+", "
                +R.drawable.level+", "
                + "'-1,0,0,20,   25,0,26,20,    50,0,51,20,      74,0,75,10    ,99,0,100,20',"
                + "0,"
                + "'45,15,60,20,0  ',"
                +"'-20,19,106,40,      -20,-5,106,1,     -5,10,8,20,      8,15,16,20,    16,10,24,20,    24,13,40,20,    40,17,50,20,     43,12,49,13,       50,15,60,20,    53,8,58,9,    60,17,70,20,    62,12,68,13,     70,18,80,20,    72,15,78,16,    80,14,88,20     ',"
                + " ' 10,20,20,50',"
                +" 0,"
                +"'-3,0,23,21,1,true,   23,0,48,21,1,true   ,48,0,73,21,1,true,    73,0,98,21,1,true  ',"
                +"'0,-2,16,8,0,      26,-1,80,6,0,     80,-1,134,6,0', "
                +"'"+R.drawable.liany+"," +R.drawable.liany_b+","+R.drawable.liany2+"," +R.drawable.liany2b+","+R.drawable.liany2+"," +R.drawable.liany2b+"', "
                + "'42,16,2,   68,15,3',"
                + " 0)");

        db.execSQL("insert into "+Provider.LVL_T_NAME +" ( "
                +Provider.LV_FON_ID+", "
                +Provider.LVL_V_FON_ID+", "
                +Provider.CP_TOPS+", "
                +Provider.CUR_CP+", "
                +Provider.FILL_TOPS_ANGELS+", "
                +Provider.RECT_TOPS+", "
                +Provider.LINE_TOPS+", "
                +Provider.CUR_FOG+" ,"
                +Provider.FOG_TOPS_DIRS+", "
                +Provider.ELEM_TOPS+", "
                +Provider.ELEM_PICTS+", "
                +Provider.DANGERS_XY_AND_R+", "
                +Provider.IS_DONE+ " ) values (" +
                +R.drawable.fon3+", "
                +R.drawable.level+", "
                + "'-1,0,0,20,   25,0,26,20,    50,0,51,20,      74,0,75,10    ,99,0,100,20,     124,0,125,20,     149,0,150,20,     174,0,175,20,     199,0,200,20',"
                + "0,"
                + "'-2,-2,2,-1,0 ',"
                +"'-20,19,106,40,      -3,16,8,20,   24,15,29,16,   38,10,50,11,     53,16,59,17,    67,12,73,13,    88,15,98,20,    97,10,106,20,    105,5,114,20,     113,12,120,20,     119,7,128,20,   127,16,133,20,   132,12,140,20,       139,8,148,20,     147,14,153,20,     152,9,160,20,     159,15,166,20,     165,11,173,20,     172,17,177,20,     176,12,184,20,    183,17,188,20,    187,11,198,20,     197,15,230,20 ',"
                + " '8,17,11,20,     17,20,26,12,     27,15,39,8,      57,17,68,9,    73,13,79,20,     -5,0,25,5,     25,5,40,1,     40,1,59,7,       59,7,72,0   ',"
                +" 0,"
                +"'-3,0,23,21,1,true,   23,0,48,21,1,true   ,48,0,73,21,1,true,    73,0,98,21,1,true,      98,0,123,20,1,true,    123,0,148,20,1,true,      148,0,173,20,1,true,     173,0,198,20,1,true   ',"
                +"'0,-1,18,8,0,      26,-1,80,6,0,     80,-1,134,6,0', "
                +"'"+R.drawable.liany+"," +R.drawable.liany_b+","+R.drawable.liany2+"," +R.drawable.liany2b+","+R.drawable.liany2+"," +R.drawable.liany2b+"', "
                + "'118,12,2,    130,16,3,     150,14,3,     162,15,3,    174,17,2,     185,17,2',"
                + " 0)");

    }

    DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);

    }

    @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

