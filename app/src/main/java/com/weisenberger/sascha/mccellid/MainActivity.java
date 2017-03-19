package com.weisenberger.sascha.mccellid;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
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
    private static int currentCell = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private void dispatchTakePictureIntent(PointEntry pe)
    {
        DebugOut.print(this, "Request image", Level.WARNING);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File picStorageFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picStorageFile));
            currentCell = pe.Cell;
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        DebugOut.print(this, "AvtivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            DebugOut.print(this, "Image captured", Level.WARNING);
            int cell = currentCell;
            currentCell = 0;
            if(0 == cell)
                return;
            File picStorageFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap a = BitmapFactory.decodeFile(picStorageFile.getAbsolutePath(), bitmapOptions);

            int newSize = Math.min(a.getHeight(), a.getWidth());
            Bitmap b = Bitmap.createBitmap(a, 0,0, newSize, newSize);
            Bitmap takenPicture = Bitmap.createScaledBitmap(b, 128, 128, false);
            DataStorage db = new DataStorage(instance);
            db.setPicture(takenPicture, cell);
        }
        if(REQUEST_IMAGE_CAPTURE == requestCode)
            listeningActive = true;
    }

    private void SetCurrentLocation(PointEntry pe)
    {
        int cell, lac;
        cell = ((pe.Cell >> 16)&0xFFFF);
        lac = (pe.Cell & 0xFFFF);
        TextView tvCellID = (TextView) findViewById(R.id.CellIDView);
        tvCellID.setText(cell + " (0x" + Integer.toHexString(cell) + ")");
        TextView tvLac = (TextView) findViewById(R.id.LACView);
        tvLac.setText(lac + " (0x" + Integer.toHexString(lac) + ")");
        TextView tvLong = (TextView) findViewById(R.id.LongView);
        tvLong.setText( pe.Longitude + "'E");
        TextView tvLat = (TextView) findViewById(R.id.LatView);
        tvLat.setText( pe.Latitude + "'N");
        TextView tvName = (TextView) findViewById(R.id.NameView);
        tvName.setText( pe.Location);
        ImageView ivPicture = (ImageView)findViewById(R.id.MiniPicView);
        ivPicture.setImageBitmap(pe.getImage());
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
                    if(null == currentPoint.getImage())
                    {
                        listeningActive = false;
                        dispatchTakePictureIntent(currentPoint);
                    }

                    SetCurrentLocation(currentPoint);
                    String text = "";//currentPoint.toString();
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
