package info.androidhive.firebaseauthapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonalInformation extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydb.db";
    public static final String TABLE_NAME1 = "PersonalInformation";
    public static final String TABLE_NAME2 = "FastingPlan";
    public static final String TABLE_NAME3 = "foodRecord_table";
    public static final String TABLE_NAME4 = "BodyRecord";
    public static final String TABLE_NAME5 = "FastRecord";
    public static final String COL_1 = "Uid";
    public static final String COL_2 = "Gender";
    public static final String COL_3 = "Age";
    public static final String COL_4 = "Height";
    public static final String COL_5 = "Weight";
    public static final String COL_6 = "Waistline";
    public static final String COL_7 = "Body_fat_percentage";
    public static final String COL_8 = "Activity";


    public PersonalInformation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME1 +" (UID TEXT PRIMARY KEY,GENDER TEXT,AGE INTEGER,HEIGHT FLOAT,WEIGHT FLOAT,WAISTLINE FLOAT,BODY_FAT_PERCENTAGE FLOAT,ACTIVITY INTEGER)");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME2 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,START_TIME LONG,END_TIME LONG,OFF_DAY INTEGER,UID TEXT,NOW_TIME LONG,DAY INTEGER)");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME3 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,DATE TEXT,AMOUNT TEXT,UID TEXT,MEAL INTEGER)");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME4 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,KG FLOAT,HEIGHT FLOAT,WAISTLINE FLOAT,BODYFAT FLOAT,DATE TEXT,TS INTEGER)");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME5 +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,STARTDATE LONG,ENDDATE LONG,EMOJI INTEGER,TS LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME5);
        onCreate(db);
    }

    public boolean insertData(String uid,String gender,Integer age,Float height,Float weight,Float waistline,Float body_fat_percentage,Integer activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,uid);
        contentValues.put(COL_2,gender);
        contentValues.put(COL_3,age);
        contentValues.put(COL_4,height);
        contentValues.put(COL_5,weight);
        contentValues.put(COL_6,waistline);
        contentValues.put(COL_7,body_fat_percentage);
        contentValues.put(COL_8,activity);
        long result = db.insert(TABLE_NAME1,null ,contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME1,null);
    }


    public boolean updateData(String uid,Float height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,uid);
        contentValues.put(COL_4,height);
        db.update(TABLE_NAME1, contentValues, "UID = ?",new String[] { uid });
        return true;
    }

    public void updateData2(String uid, String Gender, Integer Age, Float Height , Float Body_fat_percentage, Integer Activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,uid);
        contentValues.put(COL_2,Gender);
        contentValues.put(COL_3,Age);
        contentValues.put(COL_4,Height);
        contentValues.put(COL_7,Body_fat_percentage);
        contentValues.put(COL_8,Activity);
        db.update(TABLE_NAME1, contentValues, "UID = ?",new String[] { uid });
    }
    public boolean updateBodyData(String uid,Float Height,Float Weight,Float Waistline,Float Body_fat_percentage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,uid);
        contentValues.put(COL_4,Height);
        contentValues.put(COL_5,Weight);
        contentValues.put(COL_6,Waistline);
        contentValues.put(COL_7,Body_fat_percentage);

        db.update(TABLE_NAME1, contentValues, "UID = ?",new String[] { uid });
        return true;
    }

//    public Integer deleteData (String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME1, "ID = ?",new String[] {id});
//    }
}