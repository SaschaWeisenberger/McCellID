package com.weisenberger.sascha.mccellid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.logging.Level;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CamHandler
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private MainActivity main;
    public CamHandler(MainActivity main)
    {
        this.main = main;
    }

    public void dispatchTakePictureIntent()
    {
        DebugOut.print(this, "Request image", Level.WARNING);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(main.getPackageManager()) != null)
        {
            File picStorageFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picStorageFile));
            main.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        DebugOut.print(this, "AvtivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            DebugOut.print(this, "Image captured", Level.WARNING);
            File picStorageFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap a = BitmapFactory.decodeFile(picStorageFile.getAbsolutePath(), bitmapOptions);

            int newSize = Math.min(a.getHeight(), a.getWidth());
            Bitmap b = Bitmap.createBitmap(a, 0,0, newSize, newSize);
            Bitmap takenPicture = Bitmap.createScaledBitmap(b, 128, 128, false);
            main.SetTakenPicture(takenPicture);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED)
        {
            main.SetTakenPicture(null);
        }
    }
}