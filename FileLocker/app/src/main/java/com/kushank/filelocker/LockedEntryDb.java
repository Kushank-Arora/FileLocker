package com.kushank.filelocker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by Password on 14-Jun-16.
 */
public class LockedEntryDb {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DEST = "entry_dest";
    public static final String KEY_SRC = "entry_src";

    public static final String DATABASE_NAME = "LockedEntryDb";
    public static final String DATABASE_TABLE = "LockedEntryTable";
    public static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_DEST + " TEXT NOT NULL, " + KEY_SRC + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public LockedEntryDb(Context context) {
        ourContext = context;
    }

    public LockedEntryDb open() {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getReadableDatabase();
        return this;
    }

    public void close()
    {
        ourHelper.close();
    }

    public long createEntry(String sDest, String sSrc) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_DEST, sDest);
        cv.put(KEY_SRC, sSrc);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }
    public String getSrc(String sDest) {
        // TODO Auto-generated method stub
        String[] columns = new String[] { KEY_ROWID, KEY_DEST, KEY_SRC };

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_DEST+"="+sDest, null, null, null, null);
        if(c!=null){
            String result;
            c.moveToFirst();
            result = c.getString(2);
            return result;
        }
        return null;
    }
    public String getDest(String sSrc) {
        // TODO Auto-generated method stub
        String[] columns = new String[] { KEY_ROWID, KEY_DEST, KEY_SRC };

        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_SRC +"="+sSrc, null, null, null, null);
        if(c!=null){
            String result;
            c.moveToFirst();
            result = c.getString(1);
            return result;
        }
        return null;
    }

    public void delete(String sSrc) {
        // TODO Auto-generated method stub
        ourDatabase.delete(DATABASE_TABLE, KEY_SRC+"=\""+sSrc+"\"",null);
    }
    public String[][] getData() {
        // TODO Auto-generated method stub
        String[] columns = new String[] { KEY_ROWID, KEY_DEST, KEY_SRC };
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        int nEntries=0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) nEntries++;

        String result[][] = new String[nEntries][2];

       // int iRow = c.getColumnIndex(KEY_ROWID);
        int iDest = c.getColumnIndex(KEY_DEST);
        int iSrc = c.getColumnIndex(KEY_SRC);

        int counter=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext(),counter++) {
            //result = result + c.getString(iRow) + " " + new File(c.getString(iDest)).getName() + " " + new File(c.getString(iSrc)).getName() + "\n";
            result[counter][0]=c.getString(iDest);
            result[counter][1]=c.getString(iSrc);
        }

        return result;
    }
}
