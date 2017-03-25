package com.weisenberger.sascha.mccellid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.logging.Level;

import static com.weisenberger.sascha.mccellid.CamHandler.REQUEST_IMAGE_CAPTURE;

public class MainActivity extends AppCompatActivity implements NameInputHandler, View.OnClickListener{

    private MainActivity instance;
    private CamHandler camHandler;
    private TextHandler textHandler;

    private DataStorage database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        camHandler = new CamHandler(this);
        textHandler = new TextHandler(this);
        setContentView(R.layout.activity_main);
        InitViews();

        getSystemService(Context.TELEPHONY_SERVICE);

        new PositionInfo(this);

        Intent serviceIntent = new Intent(this, DataService.class);
        startService(serviceIntent);

        registerReceiver(broadcastReceiver, new IntentFilter(DataService.RETRIEVE_DATA_INTENT));

        database = new DataStorage(this);
        RelativeLayout llMain = (RelativeLayout)findViewById(R.id.activity_main);
        llMain.setOnClickListener(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent receivedIntent) {
            DebugOut.print(this, "Received intent:" + receivedIntent.getAction(), Level.INFO);
            final PositionInfo pi = PositionInfo.GetInstance();
            if(null == pi)
                return;

            instance.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    DebugOut.print(this, "run on ui thread", Level.INFO);
                    if(uiAccessGiven)
                        return;
                    PointEntry receivedPoint = (PointEntry) receivedIntent.getExtras().get("point");
                    if(null == receivedPoint)
                        return;
                    if(null == receivedPoint.Location)
                    {
                        if(requestUiAccess(receivedPoint))
                            textHandler.PromptForLocationInput(instance);
                    }
                    if(null == receivedPoint.getImage())
                    {
                        if(requestUiAccess(receivedPoint))
                            camHandler.dispatchTakePictureIntent();
                    }

                    instance.SetCurrentposition(receivedPoint);
                    instance.UpdateUI();
                }
            });
        }
    };

    private int currentCellID = 0;
    private void SetCurrentposition(PointEntry receivedPoint)
    {
        currentCellID = receivedPoint.Cell;
    }

    private static boolean uiAccessGiven = false;
    private static PointEntry pointThatRequestedUiAccess;
    public boolean requestUiAccess(PointEntry pe)
    {
        if(!uiAccessGiven)
        {
            uiAccessGiven = true;
            pointThatRequestedUiAccess = pe;
            return true;
        }
        return false;
    }
    public void releaseUiAccess()
    {
        uiAccessGiven = false;
        pointThatRequestedUiAccess = null;
    }


    public void SetTakenPicture(Bitmap bmp)
    {
        if(null != bmp) {
            pointThatRequestedUiAccess.setImage(bmp);
            database.updatepoint(pointThatRequestedUiAccess);
            UpdateUI();
        }
        releaseUiAccess();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        camHandler.onActivityResult(requestCode, resultCode, data);
        //if(REQUEST_IMAGE_CAPTURE == requestCode)
            //releaseUiAccess();
    }

    @Override
    public void NameEntered(String name) {
        if(!"".equals(name)) {
            pointThatRequestedUiAccess.Location = name;
            database.updatepoint(pointThatRequestedUiAccess);
            UpdateUI();
        }
        releaseUiAccess();
    }


    TextView tvCellID;
    TextView tvLac;
    TextView tvLong;
    TextView tvLat;
    TextView tvName;
    ImageView ivPicture;
    LinearLayout llBrowse;
    private void InitViews()
    {
        tvCellID = (TextView) findViewById(R.id.CellIDView);
        tvLac = (TextView) findViewById(R.id.LACView);
        tvLong = (TextView) findViewById(R.id.LongView);
        tvLat = (TextView) findViewById(R.id.LatView);
        tvName = (TextView) findViewById(R.id.NameView);
        ivPicture = (ImageView)findViewById(R.id.MiniPicView);
        llBrowse = (LinearLayout)findViewById(R.id.mainBrowseLayout);
    }
    private void ShowCurrentLocation(PointEntry pe)
    {
        int cell, lac;
        cell = ((pe.Cell >> 16)&0xFFFF);
        lac = (pe.Cell & 0xFFFF);
        tvCellID.setText(cell + " (0x" + Integer.toHexString(cell) + ")");
        tvLac.setText(lac + " (0x" + Integer.toHexString(lac) + ")");
        tvLong.setText( pe.Longitude + "'E");
        tvLat.setText( pe.Latitude + "'N");
        tvName.setText( pe.Location);
        ivPicture.setImageBitmap(pe.getImage());
    }

    private void UpdateEntry(PointEntry pe)
    {
        int cellsCount = llBrowse.getChildCount();
        boolean found = false;
        for (int cellIndex = 0; cellIndex < cellsCount; cellIndex++)
        {
            CellBrowseLayout cbl = (CellBrowseLayout)llBrowse.getChildAt(cellIndex);
            if(cbl.myPoint.Cell>pe.Cell) {
                llBrowse.addView(new CellBrowseLayout(this, pe), cellIndex);
                return;
            }
            if(cbl.myPoint.Cell == pe.Cell) {
                cbl.upateValues(pe);
                found = true;
            }
        }
        if(!found)
            llBrowse.addView(new CellBrowseLayout(this, pe));
    }


    public void UpdateUI()
    {
        for (PointEntry p : database.getAllPoints())
        {
            if(currentCellID == p.Cell)
                ShowCurrentLocation(p);
            UpdateEntry(p);
        }
    }

    public void remove(PointEntry pe)
    {
        database.deletePoint(pe);
        int cellsCount = llBrowse.getChildCount();
        for (int cellIndex = 0; cellIndex < cellsCount; cellIndex++)
        {
            CellBrowseLayout cbl = (CellBrowseLayout) llBrowse.getChildAt(cellIndex);
            if(cbl.myPoint.Cell == pe.Cell) {
                llBrowse.removeViewAt(cellIndex);
                return;
            }
        }
    }

    public void rename(PointEntry pe)
    {
        requestUiAccess(pe);
        textHandler.PromptForLocationInput(this);
    }

    public void deletePic(PointEntry pe) {
        database.updatepoint(pe);
        UpdateUI();
    }

    @Override
    public void onClick(View view) {

    }
}
