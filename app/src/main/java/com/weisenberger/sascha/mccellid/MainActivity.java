package com.weisenberger.sascha.mccellid;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private boolean listeningActive = true;
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

    private String lastEnteredLocation = "";
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void PromptForLocationInput(final PointEntry currentPoint, final DataStorage db)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Cell. Please enter Location:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                instance.lastEnteredLocation = input.getText().toString();
                if(!"".equals(lastEnteredLocation))
                {
                    currentPoint.Location = lastEnteredLocation;
                    lastEnteredLocation = "";
                    db.updatepoint(currentPoint);
                }
                listeningActive = true;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                instance.lastEnteredLocation = "";
                listeningActive = true;
            }
        });
        builder.show();
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
                    if(!listeningActive)
                        return;
                    PointEntry currentPoint = (PointEntry) intent.getExtras().get("point");
                    if(null == currentPoint)
                        return;
                    if(null == currentPoint.Location)
                    {
                        listeningActive = false;
                        PromptForLocationInput(currentPoint, db);
                    }
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
