package com.ateam.digitaludharseller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BaseActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Activation_details";
    SharedPreferences settings;
    String day;
    private Date startDate;
    private Date acitivatDate;
    String month;
    String year;
    public static final String Dayy = "day";
    public static final String Monthh = "month";
    public static final String Yearr = "year";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        checkValidation();


    }

    public Boolean checkValidation() {
        Boolean flag = false;
        Calendar validDate = new GregorianCalendar();


        Calendar c1 = Calendar.getInstance();
        String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + "-" + String.valueOf(c1.get(Calendar.MONTH)) + "-" + String.valueOf(c1.get(Calendar.DAY_OF_MONTH ))+ "-" + String.valueOf(c1.get((Calendar.HOUR)))+ "-" + String.valueOf(c1.get((Calendar.MINUTE)))+ "-" + String.valueOf(c1.get((Calendar.SECOND)));
        String Year = String.valueOf(c1.get(Calendar.YEAR));
        Log.d("date", myfrmt);
        String Month = String.valueOf(c1.get(Calendar.MONTH));
        String Day = String.valueOf(c1.get(Calendar.DATE));
        //  Log.d("Month",Month);
        int monthint = Integer.parseInt(Month) + 1;

        String updatedmonth = String.valueOf(monthint);
        Log.d("fmonth", updatedmonth);
        if (monthint == 13) {
            monthint = 1;
        }
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        day = settings.getString(Dayy, null);
        month = settings.getString(Monthh, null);
//        Log.d("day",day);
        year = settings.getString(Yearr, null);
        if (day != null && month != null && year != null) {

        Log.d("Datefromserver",day + "/" + month + "/" + year);
            String startDateString = Day + "/" + updatedmonth + "/" + Year;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            try {
                startDate = df.parse(startDateString);
                String newDateString = df.format(startDate);
                Log.d("newdate", newDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String activateDated = day + "/" + month + "/" + year;
            try {
                acitivatDate = dateFormat.parse(activateDated);
                Log.d("activate",acitivatDate.toString());
                String acti = dateFormat.format(acitivatDate);
                Log.d("newdate", acti);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            validDate.setTime(startDate);

        if (acitivatDate.before(startDate)) {
                flag = false;
                Log.d("first","First");
            }
          else  if (activateDated == null) {
                Toast.makeText(this, "Contact Us!!", Toast.LENGTH_LONG).show();
                Log.d("second","second");
                flag = false;
            }
        else if((acitivatDate.after(startDate)))
        {
            Log.d("fourth","fourth");
            flag = true;
        }
            else {
                Toast.makeText(this, "Not Activated", Toast.LENGTH_LONG).show();
                Log.d("activate",acitivatDate.toString());
                Log.d("start",startDate.toString());
                Log.d("third","third");
                flag = false;
            }
      /*  if(year!=null) {
            if (Integer.parseInt(Year) <= Integer.parseInt(year)) {
                Log.d(Year, year);
                if (monthint <= (Integer.parseInt(month))) {
                    Log.d(Month, month);
                    if (Integer.parseInt(Day) <= Integer.parseInt(day)) {
                          flag = true;
//                        Log.d(Day, day);
//                        Intent intent = new Intent(this, MainActivity.class);
//                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Not Activated", Toast.LENGTH_LONG).show();
                        flag = false;
                        Intent PaymentActivity = new Intent(this, com.io.startuplabs.khata_seller.PaymentActivity.class);
                        startActivity(PaymentActivity);
                    }

                } else {
                    flag = false;
                    Toast.makeText(this, "Not Activated", Toast.LENGTH_LONG).show();
                    Intent PaymentActivity = new Intent(this, com.io.startuplabs.khata_seller.PaymentActivity.class);
                    startActivity(PaymentActivity);
                }
            } else {
                flag = false;
                Toast.makeText(this, "Not Activated", Toast.LENGTH_LONG).show();
                Intent PaymentActivity = new Intent(this, com.io.startuplabs.khata_seller.PaymentActivity.class);
                startActivity(PaymentActivity);
            }
        }
        else {

        }
        */

        }

        return flag;
    }

}
