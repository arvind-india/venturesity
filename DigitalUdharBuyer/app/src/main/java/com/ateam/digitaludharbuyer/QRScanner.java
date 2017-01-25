package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class QRScanner extends AppCompatActivity {
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private String text;
    private String Transactionid;
    private SharedPreference sharedPreference;
    String mydetails;
    private int Internet_Flag;
    private String balancetopay;
    Activity context = this;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new SharedPreference();
        setContentView(R.layout.activity_qrscanner);
        text = sharedPreference.getValue(context);
        queue = Volley.newRequestQueue(this);

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
                //   Toast toast = Toast.makeText(this, "Content:" + contents , Toast.LENGTH_LONG);
                Log.d("content", contents);


                byte[] decodeValue = Base64.decode(contents, Base64.DEFAULT);

                //  String decode = unHex(decodeValue.toString());
                String seconddecoding = decodeString(contents);
                Log.d("seconddecode", seconddecoding);
                String seller = seconddecoding.substring(0, 10);
                Log.d("mydetails", text);
                Log.d("seller", seller);
                //  Log.d("zen",myfrmt);
                balancetopay = seconddecoding.substring(10,seconddecoding.indexOf("z"));
                Transactionid = seconddecoding.substring(seconddecoding.indexOf("z")+1);
                Log.d("UUID",Transactionid);
                SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
                //  String FinalBalance = sqLiteHelper.selectBalance(Long.parseLong(seller))+ balancetopay;
                ContactModel contactModel = new ContactModel();
                Log.d("balance", sqLiteHelper.selectbalance(seller));
                String url = Constants.SCAN_ADD_URL + seller + "&buyer_number=" + text + "&transaction_amount=" + balancetopay + "&transaction_id=" +Transactionid +"&is_scan=1";
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
                Boolean is_transaction = sqLiteHelper.checkTransaction(Transactionid) ;
//                Log.d("is_transaction", String.valueOf(is_transaction));
                if (is_transaction) {
                    contactModel.setTransectionPhoneNumber(Long.parseLong(seller));
                    contactModel.setSync(Internet_Flag);
                    contactModel.setTransectionAmount(Long.parseLong(balancetopay));
                    contactModel.setTransactionid(Transactionid);
                    Calendar c1 = Calendar.getInstance();
                    String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + "-" + String.valueOf(c1.get(Calendar.MONTH)) + "-" + String.valueOf(c1.get(Calendar.DAY_OF_MONTH ))+ "-" + String.valueOf(c1.get((Calendar.HOUR)))+ "-" + String.valueOf(c1.get((Calendar.MINUTE)))+ "-" + String.valueOf(c1.get((Calendar.SECOND)));
                    // formattedDate have current date/time
                    contactModel.setTransectionDateTime(myfrmt);
                    contactModel.setTransactionCategory("Add");
                    contactModel.setScan(1);
                    contactModel.setTransectionDescription("ADD MONEY");
                    Log.d("transaction",Transactionid);
                    Log.d("selectbalance",sqLiteHelper.selectbalance(seller).toString());
//                sqLiteHelper.insertTransectionRecord(contactModel);
                    Long FinalBalance = Long.parseLong(balancetopay) + Long.parseLong(sqLiteHelper.selectbalance(seller));
                    Log.d("final Balance", String.valueOf(FinalBalance));
                    contactModel.setBalance(Long.parseLong(String.valueOf(FinalBalance)));
                    sqLiteHelper.updateBalance(contactModel.getTransectionPhoneNumber(), FinalBalance);
                    sqLiteHelper.insertTransectionRecord(contactModel);

                }
                else {
                    Toast.makeText(this,"Transaction Already done",Toast.LENGTH_LONG).show();
                }

                Intent intentt = new Intent();
                setResult(RESULT_OK, intentt);
                Intent Mainactivityintent = new Intent(QRScanner.this, ThreeTabsActivity.class);
                startActivity(Mainactivityintent);
                this.finish();


            }
        }
    }

    /* public static String unHex(String arg) {
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
     */
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
