package uic.hcilab.citymeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static uic.hcilab.citymeter.SensingDB.COLUMN_ENTRY;
import static uic.hcilab.citymeter.SensingDB.TABLE_NAME;

public class SensingDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;// Database Version
    private static final String DATABASE_NAME = "sensing_db";// Database Name

    private SQLiteDatabase sensingDB;

    public SensingDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.onCreate(sensingDB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.openDb();
        try {
            Cursor cursor = sensingDB.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_NAME+"'", null);
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    Log.i("BT","Table Exists");
                    cursor.close();
                }
                else {
                    db.execSQL(SensingDB.CREATE_TABLE);
                    cursor.close();
                    Log.i("BT","Table Created");
                }
            }
        }
        catch (Exception e){
            Log.i("BT", "Can't create table : " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public String readNext(){
        Cursor cursor = sensingDB.query(TABLE_NAME,
                new String[]{ SensingDB.COLUMN_ENTRY, SensingDB.COLUMN_IS_WRITTEN},
                SensingDB.COLUMN_IS_WRITTEN+ "=?",
                new String[]{"0"}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        // prepare note object
        SensingDB result = new SensingDB(
                cursor.getString(cursor.getColumnIndex(SensingDB.COLUMN_ENTRY)),
                cursor.getInt(cursor.getColumnIndex(SensingDB.COLUMN_IS_WRITTEN)));
        // close the db connection
        cursor.close();
        return result.getEntry();
    }

    public void insertDBRow(String entry){
        ContentValues values = new ContentValues();
        values.put(SensingDB.COLUMN_ENTRY, entry);
        values.put(SensingDB.COLUMN_IS_WRITTEN, 0);
        try {
            sensingDB.insert(TABLE_NAME, null, values);
        }
        catch (Exception e){
            Log.i("BT", "Insert to DB failed : " + e.toString());
        }
    }

    public int updateDBRow(String entry) {
        ContentValues values = new ContentValues();
        values.put(SensingDB.COLUMN_IS_WRITTEN, 1);
        // update row
        return sensingDB.update(TABLE_NAME, values, SensingDB.COLUMN_ENTRY + " = ?",
                new String[]{entry});
    }

    public void deleteDBRow(String entry) {
        sensingDB.delete(TABLE_NAME, SensingDB.COLUMN_ENTRY + " = ?",
                new String[]{entry});
    }

    public void openDb() {
        try {
            sensingDB = this.getWritableDatabase();
            sensingDB.enableWriteAheadLogging();//To allow multithread access
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), e.toString());
            throw(new RuntimeException(e));
        }
    }

    public void closeDb() {
        if (sensingDB != null)
            sensingDB.close();
    }

    public void dropTable() {
        sensingDB.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }

    public void wipeTable() {
        synchronized (TABLE_NAME) {
            sensingDB.delete(TABLE_NAME, null, null);
        }
    }

    public ArrayList<String> readAll(){
        Cursor cursor = sensingDB.rawQuery("select * from " + TABLE_NAME,null);
        if (cursor != null)
            cursor.moveToFirst();
        ArrayList<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_ENTRY));
                list.add(name);
                cursor.moveToNext();
            }
        }
        // close the db connection
        cursor.close();
        return list;
    }

    @Override
    public synchronized void close() {
        super.close();
        closeDb();
    }
}
