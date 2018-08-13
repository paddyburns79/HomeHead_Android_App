package com.paddy.homehead;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "notificationsReceived.db";
    public static final String TABLE_NAME = "notificationsReceived_data";
    public static final String MSG_ID = "ID";
    public static final String NOTIFICATION_DATA = "ITEM1";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createDBTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " ITEM1 TEXT)";
        sqLiteDatabase.execSQL(createDBTable);

    }

    /**
     * Method to handle DB on upgrade
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    /**
     * Method to add data to SQLite DB
     * @param msgBody
     * @return
     */
    public boolean addData(String msgBody) {
        SQLiteDatabase notificationsDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_DATA, msgBody);

        long result = notificationsDB.insert(TABLE_NAME, null, contentValues);

        //if data is inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method to get contents of DB
     * @return
     */
    public Cursor getListContents(){
        // instance of DB
        SQLiteDatabase notificationsDB = this.getWritableDatabase();
        // selection query to output stored data by descending order (i.e. most recent first)
        Cursor data = notificationsDB.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY "+ MSG_ID+" DESC", null);
        return data;
    }
}
