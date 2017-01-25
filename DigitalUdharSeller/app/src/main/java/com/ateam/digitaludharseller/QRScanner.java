package com.ateam.digitaludharseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class QRScanner extends BaseActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private String text;

    private SharedPreference sharedPreference;
    String mydetails;
    private String balancetopay;
    private int Internet_Flag = 0;
    private String Transaction_id = "";
    Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new SharedPreference();
        setContentView(R.layout.activity_qrscanner);
        text = sharedPreference.getValue(context);
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(QRScanner.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });


        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //  sharedPreference = new SharedPreference();
        //text = sharedPreference.getValue(context);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast toast = Toast.makeText(this, "Content:" + contents, Toast.LENGTH_LONG);

                String seconddecoding = decodeString(contents);
                String buyer = seconddecoding.substring(0, 10);
                Calendar c1 = Calendar.getInstance();

                String seller = seconddecoding.substring(10, 20);
//                String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + "-" + String.valueOf(c1.get(Calendar.MONTH)) + "-" + String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
                Log.d("mydetails", text);
                Log.d("seller", seller);
                Log.d("phone", buyer);

                toast.show();
                if (text.equals(seller)) {
                    //  Log.d("zen",myfrmt)
                    Transaction_id = seconddecoding.substring(20, 56);
                    balancetopay = seconddecoding.substring(56, seconddecoding.indexOf("end"));
                    Intent intentt = new Intent();
                    setResult(RESULT_OK, intentt);
                    String url = Constants.isScannedurl + seller + "&buyer_number=" + buyer + "&transaction_amount=" +"-" + balancetopay + "&is_scan=" + 1 + "&transaction_id=" + Transaction_id;
                    Log.d("info", url);
                    RequestQueue queue = Volley.newRequestQueue(this);
                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
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
                            }
                    );

// add it to the RequestQueue
                    queue.add(getRequest);
                    SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
                    ContactModel contactModel = new ContactModel();
                    Log.d("balance", sqLiteHelper.selectbalance(buyer));
                    Log.d("Count",String.valueOf(sqLiteHelper.checkTransaction(Transaction_id)));
                    if (sqLiteHelper.checkTransaction(Transaction_id)) {

//                        java.util.Calendar c1 = java.util.Calendar.getInstance();
                        String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + " : " + String.valueOf(c1.get(Calendar.MONTH)+1) + " : " + String.valueOf(c1.get(Calendar.DAY_OF_MONTH ))+ " : " + String.valueOf(c1.get((Calendar.HOUR)))+ "  :  " + String.valueOf(c1.get((Calendar.MINUTE)))+ " : " + String.valueOf(c1.get((Calendar.SECOND)));
                        contactModel.settransactionPhoneNumber(Long.parseLong(buyer));
                        contactModel.settransactionDateTime(myfrmt);
                        contactModel.settransactionAmount(Long.parseLong("-" + balancetopay));
                        contactModel.setTransactionid(Transaction_id);
                        contactModel.setTransactionCategory("PAY_MONEY");
                        contactModel.setScan(1);

                        contactModel.setSync(Internet_Flag);
                        Log.d("here","ok");
                        sqLiteHelper.inserttransactionRecord(contactModel);
                        Long FinalBalance = Long.parseLong(sqLiteHelper.selectbalance(buyer)) - Long.parseLong(balancetopay);
                        contactModel.setBalance(FinalBalance);
                        sqLiteHelper.updateBalance(contactModel, buyer);
                        Intent Mainactivityintent = new Intent(QRScanner.this, ThreeTabsActivity.class);
                        startActivity(Mainactivityintent);
                        this.finish();
                    }
                } else
                    Toast.makeText(this, "Seller is not wright", Toast.LENGTH_LONG).show();
                Intent intentt = new Intent();
                setResult(RESULT_OK, intentt);
                Intent Mainactivityintent = new Intent(QRScanner.this, ThreeTabsActivity.class);
                startActivity(Mainactivityintent);
                this.finish();
            }
        }
    }

    @Override

    public void onBackPressed() {
        this.finish();
    }

    private String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {

            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            return decodedString;
        }
    }
}
