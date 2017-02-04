package com.weisenberger.sascha.mccellid;

/**
 * Created by Sascha on 04.02.2017.
 */

public class PointEntry {
    public int Cell;
    public float Latitude;
    public float Longitude;
    public String PictureName;

    public static final String CELL_KEY = "cellcode";
    public static final String LAT_KEY = "latitude";
    public static final String LON_KEY = "longitude";
    public static final String PIC_KEY = "picname";
    public static final String TABLE_KEY = "positionentry";

    @Override
    public String toString()
    {
        return "Cell ID: " + (Cell >> 16) + "\n" +
                "Lac    : " + (Cell & 0xFFFF) + "\n" +
                "Long   : " + Longitude + "'E\n" +
                "Lat    : " + Latitude + "'N\n";
    }
}