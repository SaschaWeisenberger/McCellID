package com.weisenberger.sascha.mccellid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.util.logging.Level;


/**
 * Created by Sascha on 22.01.2017.
 */

public class PositionInfo implements LocationListener
{
    private Context context;
    private int cellId = 0;
    private int lac = 0;
    private LocationManager locationManager;
    private TelephonyManager teleman;
    GsmCellLocation cellLocation;
    private double latitude, longitude;
    private boolean gpsAvailable = false;
    private PointEntry currentPoint;

    private static PositionInfo instance;
    public static PositionInfo GetInstance()
    {
        return instance;
    }

    public PositionInfo(Context context)
    {
        this.context = context;
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        teleman = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        instance = this;
    }

    public PointEntry ReadCellInfo()
    {
        cellLocation = (GsmCellLocation) teleman.getCellLocation();
        if(null == cellLocation)
        {
            DebugOut.print(this, "No Cell Location");
            return null;
        }
        cellId = cellLocation.getCid() & 0xFFFF;
        lac = cellLocation.getLac() & 0xFFFF;
        String output = String.format("CellID, LAC, Hex: %X %X Dec: %d %d", cellId, lac, cellId, lac);
        DebugOut.print(this, output);
        PointEntry pe = new PointEntry();
        pe.Cell = (cellId << 16) + lac ;
        return pe;
    }

    public PointF getGpsInfo()
    {
        if(!gpsAvailable)
            return null;
        PointF gps = new PointF();
        gps.x = (float) longitude;
        gps.y = (float) latitude;
        return gps;
    }

    @Override
    public void onLocationChanged(Location location) {
        DebugOut.print(this, "Location changed", Level.INFO);
        DebugOut.print(this, String.format("Current GPS Position: %f,%f", location.getLatitude(), location.getLongitude()), Level.INFO);
        DebugOut.print(this, String.format("Current Accuracy: %f", location.getAccuracy()), Level.INFO);
        gpsAvailable = true;
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String statustext = "";
        switch (status)
        {
            case LocationProvider.AVAILABLE:
                statustext = "AVAILABLE";
                break;
            case LocationProvider.OUT_OF_SERVICE:
                statustext = "OUT_OF_SERVICE";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                statustext = "TEMPORARILY_UNAVAILABLE";
                break;
        }
        DebugOut.print(this, "Status changed to " + provider + " " + statustext, Level.INFO);

    }

    @Override
    public void onProviderEnabled(String provider) {
        DebugOut.print(this, "Provider " + provider + " enabled", Level.INFO);

    }

    @Override
    public void onProviderDisabled(String provider) {
        DebugOut.print(this, "Provider " + provider + " disabled", Level.INFO);

    }

    public PointEntry getCurrentPosition() {
        return currentPoint;
    }

    public void setCurrentPosition(PointEntry pe) {
        currentPoint = pe;
    }
}
