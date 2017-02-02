package com.weisenberger.sascha.mccellid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        while(true)
        {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(counter + " " + pi.ReadInfo());
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
