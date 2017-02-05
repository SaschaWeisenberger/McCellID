package com.weisenberger.sascha.mccellid;

import android.app.Service;
import android.content.Intent;
import android.graphics.PointF;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.logging.Level;

/**
 * Created by Sascha on 31.01.2017.
 */

public class DataService extends Service implements Runnable{

    private PositionInfo pi = null;
    public static final String RETRIEVE_DATA_INTENT = "com.weisenberger.sascha.mccellid.retrievedata";
    public DataService() {
        DebugOut.print(this, "DataService created");
    }
    private Intent intent;

    @Override
    public void onCreate(){
        super.onCreate();
        intent = new Intent(RETRIEVE_DATA_INTENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            DebugOut.print(this, "Satrt Service intent:" + intent.getAction(), Level.WARNING);
        }
        catch (Exception ex)
        {
            DebugOut.print(this, "Satrt Service ex:" + ex.getMessage(), Level.WARNING);
        }
        new PositionInfo(this);
        if(null == pi)
            pi = PositionInfo.GetInstance();
        if(null == pi) {
            DebugOut.print(this, "Could not get an instance of PositionInfo", Level.SEVERE);
            return Service.START_STICKY;
        }

        DebugOut.print(this, "Service START_STICKY", Level.INFO);
        Thread t = new Thread(this);
        t.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DebugOut.print(this, "onBind inten:" + intent.getAction(), Level.INFO);
        return null;
    }

    @Override
    public void run() {
        DataStorage db = new DataStorage(this);
        while(true) {
            try
            {
                Thread.sleep(2000);
                if(null == pi)
                {
                    DebugOut.print(this, "No PositionInfo yet...", Level.WARNING);
                    return;
                }

                PointF gps = pi.getGpsInfo();
                PointEntry liveData = pi.ReadCellInfo();
                PointEntry storedData = db.getPointFromID(liveData.Cell);
                if(null == storedData) {
                    db.saveNewPosition(liveData);
                    storedData = liveData;
                }
                if(storedData.Latitude == 0 && null != gps)
                {
                    storedData.Longitude = gps.x;
                    storedData.Latitude = gps.y;
                    db.updatepoint(storedData);
                }
                intent.putExtra("point", storedData);
                DebugOut.print(this, "About to send broadcast", Level.INFO);
                sendBroadcast(intent);
            }
            catch (Exception ex)
            {
                DebugOut.print(this, "Exception " + ex.getMessage(), Level.SEVERE);
            }
        }
    }
}
