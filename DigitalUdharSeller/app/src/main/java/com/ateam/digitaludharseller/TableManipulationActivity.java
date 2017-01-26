package com.ateam.digitaludharseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class TableManipulationActivity extends BaseActivity {
    private String number;
    public static final String PREFS_NAME = "Login_details";
    public static final String PhoneNumber = "phoneKey";
    Activity context = this;
    SharedPreferences settings;
    private String first;
    private String last;
    private String phnumber;
    private int Internet_Flag = 0;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    //  Button btnDML;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_manipulation);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //  getAllWidgets();
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(TableManipulationActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
        // bindWidgetsWithEvent();
        //  checkForRequest();
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
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                Toast toast = Toast.makeText(this, "Content:" + contents, Toast.LENGTH_LONG);
//                toast.show();
                first = contents.substring(0, contents.indexOf("["));
                last = contents.substring(contents.indexOf("[") + 1, contents.indexOf("]"));
                phnumber = contents.substring(contents.indexOf("]") + 1, contents.indexOf("*"));

                number = settings.getString(PhoneNumber, null);
                //Server part
                String url = Constants.url + first + last + "&buyer_phone_number=" + phnumber + "&seller_number=" + number;
                Log.i("URL",url);
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
                contactModel.setFirstName(first);
                contactModel.setLastName(last);
                contactModel.setNumber(Long.parseLong(phnumber));
                contactModel.setBalance(0);
                //TODO: add transaction support here
                if (Internet_Flag == 1) {
                    contactModel.setSync(1);
                } else {
                    contactModel.setSync(0);
                }
                sqLiteHelper.insertRecord(contactModel);
                Intent end = new Intent(TableManipulationActivity.this, ThreeTabsActivity.class);
                setResult(RESULT_OK, end);
                startActivity(end);
                this.finish();
                //   String finall ="Name" + etFirstname.getText().toString()+ etLastname.getText().toString()+" \n " +"Phone Number" + etPhone.getText().toString()+" \n " + "Balance" + balance.getText().toString()  ;
                //    Log.d("sss",finall);
//            setResult(RESULT_OK, intent);
          /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Toast.makeText(TableManipulationActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                }
                            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();
        */
                //  Intent scannerIntent = new Intent(this,Add_Money.class);
                //  scannerIntent.putExtra("message", finall);
//            Log.w("jim",finall);
//            finish();

                // startActivityForResult(scannerIntent,0);


            }
        }

    }

    @Override

    public void onBackPressed() {
        this.finish();
    }



  /*  private void bindWidgetsWithEvent() {
        btnDML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
    }



  //  private void onButtonClick() {
      //  if (etFirstname.getText().toString().equals("") || etLastname.getText().toString().equals("") || etPhone.getText().toString().equals("") ) {
        //    Toast.makeText(getApplicationContext(), "Add ALL Fields", Toast.LENGTH_LONG).show();
        //} else {
            /*
            SQLiteHelper sqLiteHelper= new SQLiteHelper(this);
            ContactModel contactModel = new ContactModel();
            contactModel.setFirstName(etFirstname.getText().toString());
            contactModel.setLastName(etLastname.getText().toString());
            contactModel.setNumber(Long.parseLong(etPhone.getText().toString()));
            contactModel.setBalance(Long.parseLong(balance.getText().toString()));
            //TODO: add transaction support here

            sqLiteHelper.insertRecord(contactModel);

            String finall ="Name" + etFirstname.getText().toString()+ etLastname.getText().toString()+" \n " +"Phone Number" + etPhone.getText().toString()+" \n " + "Balance" + balance.getText().toString()  ;
            //    Log.d("sss",finall);
//            setResult(RESULT_OK, intent);
          /*  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Toast.makeText(TableManipulationActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                                }
                            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();

            alertDialog.show();

            Intent scannerIntent = new Intent(this,Add_Money.class);
            scannerIntent.putExtra("message", finall);
//            Log.w("jim",finall);
//            finish();

            startActivityForResult(scannerIntent,0);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            this.finish();
           */
    // }
}

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

        }
    }
*/


//
