package com.weisenberger.sascha.mccellid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.logging.Level;

/**
 * Created by Sascha on 31.01.2017.
 */

public class DataService extends Service implements Runnable{

    private PositionInfo pi = null;
    public DataService() {
        DebugOut.print(this, "DataService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(null == pi)
            pi = PositionInfo.GetInstance();
        if(null == pi) {
            DebugOut.print(this, "Could not get an instance of PositionInfo", Level.SEVERE);
            return Service.START_STICKY;
        }

        DebugOut.print(this, "", Level.INFO);
        Thread t = new Thread(this);
        t.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {
        while(true) {
            try
            {
                Thread.sleep(5000);
                if(null == pi)
                {
                    DebugOut.print(this, "No PositionInfo yet...", Level.WARNING);
                    return;
                }
                pi.ReadCellInfo();
                DebugOut.print(this, "Just read all info...", Level.INFO);
            }
            catch (Exception ex)
            {

            }
        }
    }
}
