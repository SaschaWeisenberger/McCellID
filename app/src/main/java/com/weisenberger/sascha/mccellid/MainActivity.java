package com.weisenberger.sascha.mccellid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        getSystemService(Context.TELEPHONY_SERVICE);

        new PositionInfo(this);
        Intent serviceIntent = new Intent(this, DataService.class);
        startService(serviceIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(DataService.RETRIEVE_DATA_INTENT));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            DebugOut.print(this, "Received intent:" + intent.getAction(), Level.INFO);
            final PositionInfo pi = PositionInfo.GetInstance();
            if(null == pi)
                return;

            final TextView tv = (TextView) findViewById(R.id.exampleText);
            final DataStorage db = new DataStorage(instance);
            instance.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    DebugOut.print(this, "run on ui thread", Level.INFO);
                    PointEntry currentPoint = (PointEntry) intent.getExtras().get("point");
                    if(null == currentPoint)
                        return;
                    String text = currentPoint.toString();
                    for (PointEntry p : db.getAllPoints())
                    {
                        text += p.toFlatString();
                    }
                    tv.setText(text);
                }
            });
        }
    };
}
