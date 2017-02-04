package com.weisenberger.sascha.mccellid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;

/**
 * Created by Sascha on 02.02.2017.
 */

public class DataStorage extends SQLiteOpenHelper {
    public DataStorage(Context context) {
        super(context, "positionsdb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("" +
                "CREATE TABLE " +
                    PointEntry.TABLE_KEY + " (" +
                        PointEntry.CELL_KEY + " INTEGER, " +
                        PointEntry.LAT_KEY + "latitude REAL, " +
                        PointEntry.LON_KEY + "longitude REAL, " +
                        PointEntry.PIC_KEY + "picname TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PointEntry.TABLE_KEY);
        this.onCreate(db);
    }

    public void saveNewPosition(PointEntry pe)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PointEntry.CELL_KEY, pe.Cell);
        values.put(PointEntry.LAT_KEY, pe.Latitude);
        values.put(PointEntry.LON_KEY, pe.Longitude);
        values.put(PointEntry.PIC_KEY, pe.PictureName);
        db.insert(PointEntry.TABLE_KEY, null, values);
        db.close();
    }

    public PointEntry getPointFromID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(PointEntry.TABLE_KEY, null, PointEntry.CELL_KEY + "=" + id, null, null, null, null, null);
        if(0 == cursor.getCount())
            return null;
        cursor.moveToFirst();
        PointEntry pe = new PointEntry();
        pe.Cell = cursor.getInt(0);
        pe.Latitude = cursor.getFloat(1);
        pe.Longitude = cursor.getFloat(2);
        pe.PictureName = cursor.getString(3);
        db.close();

        return pe;
    }
}
