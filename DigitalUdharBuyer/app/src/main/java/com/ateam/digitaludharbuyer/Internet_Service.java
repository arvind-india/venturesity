package com.ateam.digitaludharbuyer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;


public class Internet_Service extends IntentService {

    public static final String SYNC = "sync";
    private RequestQueue queue;
    public Internet_Service() {
        super("Internet_Service");
    }
    Internet_Service context = this;
    SharedPreferences settings;
    private int Internet_Flag;
    public String url;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        settings = context.getSharedPreferences(Util.PREFS_NAMEE, Context.MODE_PRIVATE);
        Boolean is_sync = settings.getBoolean(SYNC, false);
        queue = Volley.newRequestQueue(this);

        boolean isNetworkConnected = extras.getBoolean("isNetworkConnected");
        Log.d("net", String.valueOf(isNetworkConnected));
        if(isNetworkConnected) {
            if (is_sync == true) {
                Log.d("Internet_Service", is_sync.toString());
               // Toast.makeText(getApplicationContext(), "Now you are online", Toast.LENGTH_LONG).show();
                final SQLiteHelper sqLiteHelper = new SQLiteHelper(Internet_Service.this);
                ArrayList<ContactModel> contacts = sqLiteHelper.getToBeSyncedTransactionRecords();
                Log.d("ContactSize",String.valueOf(contacts.size()));
                settings = context.getSharedPreferences("Login_details", Context.MODE_PRIVATE);
                String sellerNumber = settings.getString("phoneKey", null);
                if (contacts.size() > 0) {

                    ContactModel contactModel;
                    for (int i = 0; i < contacts.size(); i++) {
                        contactModel = contacts.get(i);
                        long number = contactModel.gettransactionPhoneNumber();
                        long amount = contactModel.gettransactionAmount();
                        String category = contactModel.getTransactionCategory();
                        int scan = contactModel.getscan_Info();
                        final String transaction_id = contactModel.getTransactionid();
                        if (category != null) {
                            Log.d("categoryy", category);

                            if (category.equals("Add")) {
                                Log.d("Hit", "Hit");
                                url = Constants.SCAN_ADD_URL + number + "&buyer_number=" +
                                        sellerNumber + "&transaction_amount=" + amount + "&transaction_id="
                                        + transaction_id + "&is_scan=" + scan;
                            } else {
                                Log.d("HitB", "HitB");
                                url = Constants.transactions_pay_url + sellerNumber + "&buyer_number" +
                                        "=" + number + "&transaction_amount=" + amount + "&transaction_id="
                                        + transaction_id + "&is_scan=" + scan;
                            }
                            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // display response
                                            Log.d("Response", response.toString());
                                            Internet_Flag = 1;
                                            sqLiteHelper.setTransactionScan(transaction_id);
                                            sqLiteHelper.setTransactionSync(transaction_id);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("Error", error.toString());
                                            Internet_Flag = 0;
                                        }
                                    }
                            );

                            queue.add(getRequest);
                        } else Log.w("Error", "bug");
                    }
                }
            } else {
              //  Toast.makeText(getApplicationContext(), "Now you are offline", Toast.LENGTH_LONG).show();
            }
        }
        //else Toast.makeText(getApplicationContext(), "No Internet Found", Toast.LENGTH_LONG).show();

    }
}