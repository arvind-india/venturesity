package com.ateam.digitaludharseller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.UUID;

public class Add_Money extends BaseActivity{
    public static final String PhoneNumber = "phoneKey";
    public static final String PREFS_NAME = "Login_details";
    public final static int QRcodeWidth = 500;
    SharedPreferences settings;
    ImageView imageView;
    Button button;
    Bitmap bitmap;
    Activity context = this;
    EditText Etamount;
    private Intent Qractivity;
    private String Seller_number;
    private SQLiteHelper sqLiteHelper;
    private Long phonenumber;
    private int Internet_Flag;
    private RequestQueue queue;
    private String amount;

    public static String unHex(String arg) {
        String uui="";
        String str = "";
        for(int i=0;i<arg.length();i+=2)
        {
            String s = arg.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        uui = str.substring(7);
        return uui;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoney);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        button = (Button) findViewById(R.id.button);
        Intent intent = getIntent();
        Seller_number = settings.getString(PhoneNumber,null);
        phonenumber = Long.parseLong(intent.getStringExtra("phone"));
        sqLiteHelper = new SQLiteHelper(this);
        TextView buyerphone = (TextView) findViewById(R.id.buyernumber);
        buyerphone.setText("Add Money To :" + phonenumber.toString()  + "\n" + sqLiteHelper.selectName(phonenumber));

        Etamount = (EditText) findViewById(R.id.etaddmoney);
        Qractivity = new Intent(this,QRctivity.class);
        amount = Etamount.getText().toString();
       // Log.d("money2",amount);
        imageView = (ImageView) findViewById(R.id.imageView);
        queue = Volley.newRequestQueue(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID transaction_id = UUID.randomUUID();
                Log.d("UUID",transaction_id.toString());
                ContactModel contactModel = new ContactModel();
                amount = Etamount.getText().toString();

                String url = Constants.transactions_url + Seller_number + "&buyer_number=" + phonenumber + "&transaction_amount=" + amount + "&transaction_id=" +transaction_id + "&is_scan=0";
                Log.i("URL",url);
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Log.d("Response", response.toString());
                                Internet_Flag = 1;
                                Log.d("True","got");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error", error.toString());
                                Log.d("Error","Error");
                                Internet_Flag = 0;
                            }
                        }
                );

                // add it to the RequestQueue
                queue.add(getRequest);
                Long FinalBalance = Long.parseLong(sqLiteHelper.selectbalance(String.valueOf(phonenumber))) + Long.parseLong(amount);
                contactModel.setBalance(Long.parseLong(String.valueOf(FinalBalance)));
                sqLiteHelper.updateBalance(contactModel, String.valueOf(phonenumber));
                contactModel.setScan(0);
                Calendar c1 = Calendar.getInstance();
                String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + " : " + String.valueOf(c1.get(Calendar.MONTH)+1) + " : " + String.valueOf(c1.get(Calendar.DAY_OF_MONTH ))+ " : " + String.valueOf(c1.get((Calendar.HOUR)))+ "  :  " + String.valueOf(c1.get((Calendar.MINUTE)))+ " : " + String.valueOf(c1.get((Calendar.SECOND)));
                Log.d("Dateabhi",myfrmt);
                contactModel.settransactionDateTime(myfrmt);
                contactModel.setSync(Internet_Flag);
                contactModel.settransactionPhoneNumber(phonenumber);
                contactModel.setTransactionid(transaction_id.toString());
//                Calendar c = Calendar.getInstance();
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String formattedDate = df.format(c.getTime());
                contactModel.setTransactionCategory("Add");
                contactModel.settransactionAmount(Long.parseLong(amount));
                sqLiteHelper.inserttransactionRecord(contactModel);
                Log.d("phone",phonenumber.toString());
//               Log.d("money1",sqLiteHelper.selectbalance(String.valueOf(phonenumber)));
               // Log.d("Hello","Abc");

                Log.d("money2",amount);
                String qr = Seller_number + String.valueOf(Integer.decode(amount) + "z" + transaction_id.toString());
                Log.d("errt",qr);
               Log.d("nicks", String.valueOf(FinalBalance));
               // String hexString = String.format("%040x", new BigInteger(1, qr.getBytes(/*YOUR_CHARSET?*/)));
               // Log.d("HEXX",unHex(hexString));


                String firstencode = String.format("%040x", new BigInteger(1, qr.getBytes(/*YOUR_CHARSET?*/)));
            //    byte[] encodeValue = Base64.encodeToString(qr.getBytes(), Base64.DEFAULT);
                String q = encodeString(qr);
                Log.d("Encode",q);
                Qractivity.putExtra("qr",q);
                startActivity(Qractivity);


            }
        });
    }

    @Override
    protected void onStop(){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        this.finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent MainactivityIntent = new Intent(Add_Money.this,ThreeTabsActivity.class);
        Intent intentt = new Intent();
        setResult(RESULT_OK, intentt);
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

