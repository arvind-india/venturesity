package com.ateam.digitaludharseller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;


public class MyDetails extends AppCompatActivity {
    private String Fname;
    private String Lname;
    private RequestQueue queue;
    public String point;
    private String number;
    public static final String MYCODE = "mycode";
    public static final String POINTS = "points";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
//    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Login_details";
    public static final String Name = "firstnameKey";
    public static final String PhoneNumber = "phoneKey";
    public static final String LName = "lastnamekey";
    Activity context = this;

    private String details;

    TextView name,phone,refer,rateus;

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details);

        settings = MyDetails.this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Fname = settings.getString(Name,null);
        String Refer = settings.getString(MYCODE,null);
        point = settings.getString(POINTS,null);
        queue = Volley.newRequestQueue(this);
        refer = (TextView) findViewById(R.id.mycode);
        refer.setText(Refer);
        number = settings.getString(PhoneNumber,null);
        rateus = (TextView) findViewById(R.id.playstore);
        Lname = settings.getString(LName,null);
        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.phone);
        name.setText(Fname + " " + Lname);
        phone.setText(number);
        details = Fname + "[" + Lname + "]" + number + "*";

        imageView = (ImageView) findViewById(R.id.imagedetails);
        try {
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(details, BarcodeFormat.QR_CODE, 500, 500);


            imageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MyDetails.this, " unable to find market app", Toast.LENGTH_LONG).show();
                }
            }
        });
        showPoints();
    }
    private void showPoints() {
        ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
        if (connectivityChangeReceiver.isConnected(MyDetails.this)) {

            String number = settings.getString(PhoneNumber, null);
            String url = Constants.SERVER_ADDRESS + "points/?username=" + number;
            Log.d("info", url);
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            // display response
                            try {

                                String points = response.getString("points");
                                editor = settings.edit();
                                editor.putString("points", points);
                                editor.commit();
//                                    String transaction_amount = jsonObject.getString("transaction_amount").toString();
//                                    String transaction_time = jsonObject.getString("transaction_time").toString();
//                                    String description = jsonObject.getString("description").toString();
//                                    String transaction_id = jsonObject.getString("transaction_id").toString();
                                Log.d("MyDetailsPoints", String.valueOf(points));
                                TextView pointView = (TextView) findViewById(R.id.myrefer);
                                pointView.setText(points);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("Error", error.toString());
                        }
                    });


            // add it to the RequestQueue
            queue.add(getRequest);

        }
        else {
            TextView pointView = (TextView) findViewById(R.id.myrefer);
            point = settings.getString(POINTS,null);
            pointView.setText(point);
        }
    }

}
