package com.weisenberger.sascha.mccellid;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSystemService(Context.TELEPHONY_SERVICE);

        new PositionInfo(this);
        Intent serviceIntent = new Intent(this, DataService.class);
        //startService(serviceIntent);
        new Thread(this).start();
    }

    private static int counter = 0;
    @Override
    public void run() {
        final PositionInfo pi = PositionInfo.GetInstance();
        if(null == pi)
            return;
        //final int counter = 0;
        final TextView tv = (TextView) findViewById(R.id.exampleText);
        final DataStorage db = new DataStorage(this);

        while(true)
        {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
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

                    tv.setText(storedData.toString());
                }
            });
            try
            {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                return;
            }
            counter++;
        }
    }
}
