package studio.emendi.mareapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Maksim on 2/10/17.
 */

public class SqlLiteBaza  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Baza.db";
    public static final String tbName = "Podaci";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "temperatura";
    public static final String COL_3 = "maksTemp";
    public static final String COL_4 = "minTemp";
    public static final String COL_5 = "brzinaVetra";
    public static final String COL_6 = "pritisak";
    public static final String COL_7 = "vlaznost";
    public static final String COL_8 = "slika";



    public SqlLiteBaza(Context context) {
        super(context, DATABASE_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+tbName+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, temperatura TEXT, maksTemp TEXT, minTemp TEXT, brzinaVetra TEXT, pritisak TEXT, vlaznost TEXT, slika TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + tbName);
        onCreate (db);

    }

    public boolean insertData(String temperatura, String makstemp, String minTemp, String brzinaVetra, String pritisak, String vlaznost, String slika)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,temperatura);
        contentValues.put(COL_3,makstemp);
        contentValues.put(COL_4,minTemp);
        contentValues.put(COL_5,brzinaVetra);
        contentValues.put(COL_6,pritisak);
        contentValues.put(COL_7,vlaznost);
        contentValues.put(COL_8,slika);
        db.insert(tbName,null ,contentValues);

        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName,null);
        return res;
    }

    public Cursor getTemp() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_2,null);
        return res;
    }

    public Cursor getMaxTemp() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_3,null);
        return res;
    }

    public Cursor getMinTemp() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_4,null);
        return res;
    }

    public Cursor getVetar() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_5,null);
        return res;
    }

    public Cursor getPritisak() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_6,null);
        return res;
    }

    public Cursor getVlaznost() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+tbName+" "+COL_7,null);
        return res;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tbName, "ID = ?",new String[] {id});
    }

}
