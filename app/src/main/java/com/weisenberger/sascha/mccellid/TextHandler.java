package com.weisenberger.sascha.mccellid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.EditText;

public class TextHandler
{
    private AppCompatActivity main;
    public TextHandler(AppCompatActivity main)
    {
        this.main = main;
    }

    public void PromptForLocationInput(final NameInputHandler handler)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("New Cell. Please enter Location:");
        final EditText input = new EditText(main);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.NameEntered(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                handler.NameEntered("");
            }
        });
        builder.show();
    }
}
