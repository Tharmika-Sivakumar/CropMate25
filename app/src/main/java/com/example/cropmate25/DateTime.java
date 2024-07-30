package com.example.cropmate25;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTime{
    private DateTime() {}

    public static String getCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormatter.format(now);
    }

    public static String getCurrentTime() {
        Date now = new Date();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormatter.format(now);
    }
    public static String getTimeStamp() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("'on' yyyy-MM-dd  'at' HH:mm:ss", Locale.getDefault());
        return formatter.format(now);
    }
}