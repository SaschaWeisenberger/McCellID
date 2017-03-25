package com.weisenberger.sascha.mccellid;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by sasch on 25.03.2017.
 */

public class CellBrowseLayout extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener
{
    PointEntry myPoint;
    MainActivity main;
    public CellBrowseLayout(Context context, PointEntry pe)
    {
        super(context);
        main = (MainActivity)context;
        myPoint = pe;

        inflate(this.getContext(), R.layout.cell_brwose_layout, this);
        Init();
        SetValues();

        btnRemove.setOnClickListener(this);
        btnRename.setOnClickListener(this);
        btnRemPic.setOnClickListener(this);
        setOnClickListener(this);

        setFocusableInTouchMode(true);
        setOnFocusChangeListener(this);
    }

    TextView tvCell;
    TextView tvPosition;
    TextView tvName;
    Button btnRemove;
    Button btnRename;
    Button btnRemPic;
    ImageView preview;
    LinearLayout llButtonRow;
    private void Init()
    {
        tvCell = (TextView) findViewById(R.id.browseCell);
        tvName = (TextView) findViewById(R.id.browseName);
        tvPosition = (TextView) findViewById(R.id.browsePosition);
        btnRemove = (Button) findViewById(R.id.browseRemove);
        btnRename = (Button) findViewById(R.id.browseRename);
        btnRemPic = (Button) findViewById(R.id.browseRemPic);
        preview = (ImageView) findViewById(R.id.browsePicture);
        llButtonRow = (LinearLayout)findViewById(R.id.browseButtonRow);
    }

    private void SetValues()
    {
        int cell, lac;
        cell = ((myPoint.Cell >> 16)&0xFFFF);
        lac = (myPoint.Cell & 0xFFFF);
        tvCell.setText(Integer.toHexString(cell) + ":" + Integer.toHexString(lac));
        tvPosition.setText(myPoint.Longitude + "'E / " + myPoint.Latitude + "'N");
        tvName.setText(myPoint.Location);
        preview.setImageBitmap(myPoint.getImage());
    }

    @Override
    public void onClick(View view)
    {
        Button trigger = (Button)view;
        switch (trigger.getId())
        {
            case R.id.browseRemove:
                main.remove(myPoint);
                break;

            case R.id.browseRename:
                main.rename(myPoint);
                break;

            case R.id.browseRemPic:
                myPoint.setImage(null);
                main.deletePic(myPoint);
                break;
        }
    }

    public void upateValues(PointEntry pe) {
        if(null != pe.Location && !pe.Location.equals(myPoint.Location))
            myPoint.Location = pe.Location;
        if(null == myPoint.getImage() && null != pe.getImage())
            myPoint.setImage(pe.getImage());
        if(0 == myPoint.Latitude || 0 == myPoint.Longitude)
        {
            myPoint.Latitude = pe.Latitude;
            myPoint.Longitude = pe.Longitude;
        }
        SetValues();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b) {
            llButtonRow.setVisibility(VISIBLE);
            setBackgroundColor(Color.WHITE);
        }
        else {
            llButtonRow.setVisibility(GONE);
            setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
