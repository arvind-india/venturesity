package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Paytobuyer extends AppCompatActivity {
    private String balance;
    private String number;
    private  UUID transaction_id;
    SharedPreferences settings;
    private String text;
    public static final String PREFS_NAME = "Login_details";
    private SharedPreference sharedPreference;
    public static final String PhoneNumber = "phoneKey";
    Activity context = this;
    public final static int QRcodeWidth = 500;
    private String balancetopay;
    Bitmap bitmap;
    private RequestQueue queue;
    private EditText describtion;
    private String describtioninfo;
    private String message;// seller details from listview
    private ImageView imageView;
    private int Internet_Flag;
    private EditText payet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytobuyer);
        payet = (EditText) findViewById(R.id.pay_balance);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        number = settings.getString(PhoneNumber, null);
        text = settings.getString(PhoneNumber, null);
        balancetopay = payet.getText().toString();
        TextView buyernumber = (TextView) findViewById(R.id.buyernumber);
        describtion = (EditText) findViewById(R.id.describtion);
        sharedPreference = new SharedPreference();
        SQLiteHelper sqLiteHelper = new  SQLiteHelper (this);
        Intent intent = getIntent();
        message = intent.getStringExtra("Balance");
        buyernumber.setText("Pay To : " + message + "\n" + sqLiteHelper.selectName(Long.parseLong(message)));
        imageView = (ImageView) findViewById(R.id.imageView);
        Button btn = (Button) findViewById(R.id.pay);
        queue = Volley.newRequestQueue(this);
        final Intent Qractivity = new Intent(this,QRctivity.class);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                transaction_id = UUID.randomUUID();
                Log.d("UUID",transaction_id.toString());
                describtioninfo = describtion.getText().toString();
                Log.i("describtioninfo: ", describtioninfo);

                String url = Constants.transections_url ;
                StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // display response
                                Log.d("Response", response.toString());
                                Internet_Flag = 1;
                                }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error", error.toString());
                                Internet_Flag = 0;
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("seller_number", message);
                        params.put("buyer_number", number);
                        params.put("transaction_amount", "-"+payet.getText().toString());
                        params.put("transaction_id", transaction_id.toString());
                        params.put("is_scan", "0");
                        params.put("description", describtioninfo);
                        return params;
                    }
                };

		// add it to the RequestQueue
                queue.add(getRequest);

                SQLiteHelper sQLiteHelper = new SQLiteHelper(Paytobuyer.this);
                Log.d("pay", payet.getText().toString());
                ContactModel contactModel = new ContactModel();
                contactModel.setTransectionPhoneNumber(Long.parseLong(message));
                contactModel.setTransactionCategory("Pay_Money");
                contactModel.setSync(Internet_Flag);
                java.util.Calendar c1 = java.util.Calendar.getInstance();
                String myfrmt = String.valueOf(c1.get(java.util.Calendar.YEAR)) + " : " + String.valueOf(c1.get(java.util.Calendar.MONTH)+1) + " : " + String.valueOf(c1.get(java.util.Calendar.DAY_OF_MONTH ))+ " : " + String.valueOf(c1.get((java.util.Calendar.HOUR)))+ "  :  " + String.valueOf(c1.get((java.util.Calendar.MINUTE)))+ " : " + String.valueOf(c1.get((java.util.Calendar.SECOND)));
                Log.d("Dateabhi",myfrmt);
                // formattedDate have current date/time
                contactModel.setTransectionDateTime(myfrmt);
                contactModel.setScan(0);
                contactModel.setTransactionid(transaction_id.toString());
                contactModel.setTransectionAmount(Long.parseLong("-" + payet.getText().toString()));
                contactModel.setTransectionDescription(describtioninfo);
                sQLiteHelper.insertTransectionRecord(contactModel);
                long balance, remainAmount;
                balance = Long.parseLong(sQLiteHelper.selectBalance(Long.parseLong(message)));
                remainAmount = balance - Long.parseLong(payet.getText().toString());
                Log.d("Remain amt ", String.valueOf(remainAmount));
                sQLiteHelper.updateBalance(Long.parseLong(message), remainAmount);
                Log.d("seller", message);
                Log.d("user", text);
                Log.d("die", "lost");
                String encode = text + message +transaction_id+ payet.getText().toString() + "end";
                String qr = encodeString(encode);
                Qractivity.putExtra("qr",qr);
                startActivity(Qractivity);

            }
	    });
    }



    @Override
    public void onBackPressed() {
        Intent MainactivityIntent = new Intent(Paytobuyer.this, ThreeTabsActivity.class);
        Intent intentt = new Intent();
        setResult(RESULT_OK, intentt);
        this.finish();
        startActivity(MainactivityIntent);

    }

    private String encodeString(String s) {
        byte[] data = new byte[0];

        try {
            data = s.getBytes("UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            String base64Encoded = Base64.encodeToString(data, Base64.DEFAULT);

            return base64Encoded;

        }
    }
}
