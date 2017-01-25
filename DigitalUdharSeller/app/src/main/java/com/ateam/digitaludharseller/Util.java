package com.ateam.digitaludharseller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Sumit on 05-Jan-17.
 */

public class Util {
    public static SharedPreferences.Editor editor;
    private String phone_number;
    public static final String PREFS_NAME = "Activation_details";
    public static SharedPreferences settings;
    public static final String Date = "date";
    public static final String Dayy = "day";
    public static final String Month = "month";
    public static final String Year = "year";


    public static void updateActivationDate(Context context, String date){
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        if (date != null) {
            String day = date.substring(0, 2);
            String month = date.substring(2, 4);
            String year = date.substring(4, 8);
            Log.d("Date", date);
            editor.putString(Dayy,day);
            editor.putString(Month,month);
            editor.putString(Year,year);
            editor.commit();
        }

    }
}
