package com.weisenberger.sascha.mccellid;

import java.io.Serializable;

/**
 * Created by Sascha on 04.02.2017.
 */

public class PointEntry implements Serializable {
    public int Cell;
    public float Latitude;
    public float Longitude;
    public String PictureName;
    public String Location;

    public static final String CELL_KEY = "cellcode";
    public static final String LAT_KEY = "latitude";
    public static final String LON_KEY = "longitude";
    public static final String PIC_KEY = "picname";
    public static final String LOC_KEY = "location";
    public static final String TABLE_KEY = "positionentry";

    @Override
    public String toString()
    {
        int cell, lac;
        cell = ((Cell >> 16)&0xFFFF);
        lac = (Cell & 0xFFFF);
        return "Cell ID\t: " + cell + "/" + Integer.toHexString(cell) + "\n" +
                "Lac    \t: " + lac + "/" + Integer.toHexString(lac) +"\n" +
                "Long   \t: " + Longitude + "'E\n" +
                "Lat    \t: " + Latitude + "'N\n" +
                "Location\t: " + Location + "\n";
    }

    public String toFlatString()
    {
        int cell, lac;
        cell = ((Cell >> 16)&0xFFFF);
        lac = (Cell & 0xFFFF);
        return Integer.toHexString(cell)+ ":" + Integer.toHexString(lac) + " " + Longitude + "'E:" + Latitude + "'N (" + Location + ")\n";
    }
}
