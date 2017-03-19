package com.weisenberger.sascha.mccellid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

/**
 * Created by Sascha on 02.02.2017.
 */

public class DataStorage extends SQLiteOpenHelper {
    public DataStorage(Context context) {
        super(context, "positionsdb", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("" +
                "CREATE TABLE " +
                    PointEntry.TABLE_KEY + " (" +
                        PointEntry.CELL_KEY + " INTEGER, " +
                        PointEntry.LAT_KEY + " REAL, " +
                        PointEntry.LON_KEY + " REAL, " +
                        PointEntry.PIC_KEY + " BLOB, " +
                        PointEntry.LOC_KEY + " TEXT)");
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
        values.put(PointEntry.PIC_KEY, pe.imageBytes);
        values.put(PointEntry.LOC_KEY, pe.Location);
        db.insert(PointEntry.TABLE_KEY, null, values);
        db.close();
        DebugOut.print(this, "Stored new Position: " + pe.toString());
    }

    public PointEntry getPointFromID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                PointEntry.TABLE_KEY,
                null,
                PointEntry.CELL_KEY + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        if(0 == cursor.getCount()) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        PointEntry pe = pointFromCursor(cursor);
        cursor.close();
        db.close();
        DebugOut.print(this, "Read Position: " + pe.toString());

        return pe;
    }

    public void updatepoint(PointEntry pe)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PointEntry.LAT_KEY, pe.Latitude);
        values.put(PointEntry.LON_KEY, pe.Longitude);
        values.put(PointEntry.PIC_KEY, pe.imageBytes);
        values.put(PointEntry.LOC_KEY, pe.Location);
        db.update(PointEntry.TABLE_KEY,
                values,
                PointEntry.CELL_KEY + "=?",
                new String[]{String.valueOf(pe.Cell)});
        db.close();

        DebugOut.print(this, "Updated Position: " + pe.toString());
    }

    public Vector<PointEntry> getAllPoints()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PointEntry.TABLE_KEY, null, null, null, null, null, PointEntry.CELL_KEY);
        Vector<PointEntry> allPoints = new Vector<>();

        if(0 == cursor.getCount())
        {
            cursor.close();
            return allPoints;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            PointEntry pe = pointFromCursor(cursor);
            allPoints.add(pe);
            cursor.moveToNext();
        }

        cursor.close();
        return allPoints;
    }

    private PointEntry pointFromCursor(Cursor cursor)
    {
        PointEntry pe = new PointEntry();
        pe.Cell = cursor.getInt(0);
        pe.Latitude = cursor.getFloat(1);
        pe.Longitude = cursor.getFloat(2);
        pe.imageBytes = cursor.getBlob(3);
        pe.Location = cursor.getString(4);
        return pe;
    }

    public void setPicture(Bitmap takenPicture, int cell)
    {
        PointEntry pe = getPointFromID(cell);
        pe.setImage(takenPicture);
        updatepoint(pe);
    }
}
