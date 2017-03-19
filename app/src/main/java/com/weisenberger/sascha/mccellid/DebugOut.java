package com.weisenberger.sascha.mccellid;

import android.os.Environment;

import java.util.logging.Level;

/**
 * Created by Sascha on 17.01.2017.
 */
public class DebugOut {
//    public static final Level OFF = new Level("OFF",Integer.MAX_VALUE, defaultBundle);
//    public static final Level SEVERE = new Level("SEVERE",1000, defaultBundle);
//    public static final Level WARNING = new Level("WARNING", 900, defaultBundle);
//    public static final Level INFO = new Level("INFO", 800, defaultBundle);
//    public static final Level CONFIG = new Level("CONFIG", 700, defaultBundle);
//    public static final Level FINE = new Level("FINE", 500, defaultBundle);
//    public static final Level FINER = new Level("FINER", 400, defaultBundle);
//    public static final Level FINEST = new Level("FINEST", 300, defaultBundle);
//    public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, defaultBundle);

    public static Level currentDebugLevel = Level.WARNING;


    public static void print(Object instance, String text){
        print(instance, text, Level.FINEST);
    }
    public static void print(Object instance, String text, Level level){
        if(level.intValue() < currentDebugLevel.intValue() || currentDebugLevel == Level.OFF)
            return;
        System.out.print(level.getName() + "\t");
        System.out.print(instance.getClass().getName());
        System.out.print(" : ");
        System.out.println(text);
    }
}
