package com.ateam.digitaludharseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends BaseActivity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    private RequestQueue queue;
    public static final String PhoneNumber = "phoneKey";
    SharedPreferences settings;
    Activity context = this;
    public static final String PREFS_NAME = "Login_details";
    private String number;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.payment_layout);

        // Payment button created by you in XML layout
        TextView textView = (TextView) findViewById(R.id.contact) ;

        Button button = (Button) findViewById(R.id.btn_pay);
        queue = Volley.newRequestQueue(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "10000");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "sumit@startuplabs.io");
            preFill.put("contact", "8144240078");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing");
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            number = settings.getString(PhoneNumber,null);
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            String url = Constants.PaymentAddress+razorpayPaymentID+"&phone="+number;
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // display response
                                            Log.d("Response", response.toString());
                                            try {
                                                Util.updateActivationDate(PaymentActivity.this, response.getString("date"));
                                                //TODO: display progress dialog box
                                                PaymentActivity.this.finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                volleyError = error;
                            }
                            Log.d("Error", volleyError.toString());

                        }
                    }
            );

            // add it to the RequestQueue
            queue.add(getRequest);

        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }




    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

        public void callus()
        {
            Log.d(TAG,"Its Fun");
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +Long.parseLong("01414029724")));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }
}
