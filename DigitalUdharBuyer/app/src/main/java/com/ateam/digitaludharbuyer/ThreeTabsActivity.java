package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class ThreeTabsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Login_details";
    public static final String SYNC = "sync";
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public static final String PhoneNumber = "phoneKey";
    int backButtonCount = 0;
    Button btnAddNewRecord;
    Activity context = this;
    SQLiteHelper sQLiteHelper;
    LinearLayout parentLayout;
    LinearLayout layoutDisplayPeople;
    ImageView Sync;
    TextView tvNoRecordsFound;
    private TextView messageView;
    private BottomBar bottomBar;
    private ProgressDialog progressDialog;
    private RequestQueue queue;
    private String number;
    private String My_number;
    private String Phnonenumber;
    private String rowID = null;

    private ArrayList<HashMap<String, String>> tableData = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_tabs);


//        if (getIntent().getExtras() != null) {
//
//            for (String key : getIntent().getExtras().keySet()) {
//                String value = getIntent().getExtras().getString(key);
//
//                if (key.equals("AnotherActivity") && value.equals("True")) {
//                            Intent intent = new Intent(this, ThreeTabsActivity.class);
//                            intent.putExtra("Balance", value);
//                            startActivity(intent);
//                            finish();
//                }
//
//            }
//        }

        Sync = (ImageView) findViewById(R.id.sync);
        settings = context.getSharedPreferences(Util.PREFS_NAMEE, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean(SYNC, true);
        editor.commit();
        Sync.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                if (settings.getBoolean(SYNC, false) == false) {
                    editor.putBoolean(SYNC, true);
                    Sync.setImageResource(R.drawable.syncgreen);
                    editor.commit();
                    Log.d("SYNC", String.valueOf(settings.getBoolean(SYNC, false)));
                } else {
                    Sync.setImageResource(R.drawable.sync);
                    editor.putBoolean(SYNC, false);
                    editor.commit();
                    Log.d("SYNC", String.valueOf(settings.getBoolean(SYNC, false)));
                }
            }
        });
        // messageView = (TextView) findViewById(R.id.messageView);
        getAllWidgets();
        SharedPreferences settings;
        sQLiteHelper = new SQLiteHelper(this);
        progressDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        My_number = settings.getString(PhoneNumber, null);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_favorites);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                //  messageView.setText(TabMessage.get(tabId, false));
                Log.d("Tabid", TabMessage.get(tabId, false).trim());
                if (TabMessage.get(tabId, false).trim().equals("Content for favorites")) {
                    displayAllRecords();
                } else if (TabMessage.get(tabId, false).trim().equals("Content for friends")) {
                    onAddRecord();
                    bottomBar.setSelected(false);
                    displayAllRecords();
                } else if (TabMessage.get(tabId, false).trim().equals("Content for nearby")) {
                    Intent Qrscanner = new Intent(ThreeTabsActivity.this, QRScanner.class);
                    startActivity(Qrscanner);
                    bottomBar.setSelected(false);

                } else if (TabMessage.get(tabId, false).trim().equals("Content for setting")) {
                    Intent mydetails = new Intent(ThreeTabsActivity.this, MyDetails.class);
                    startActivity(mydetails);
                    bottomBar.setSelected(false);
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
//                Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true),
//                        Toast.LENGTH_LONG).show();
            }
        });

        Intent alarmIntent = new Intent(context, ConnectivityChangeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 30 * 1000,
                30 * 1000, pendingIntent);

    }

    private void getAllWidgets() {


        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        layoutDisplayPeople = (LinearLayout) findViewById(R.id.layoutDisplayPeople);

        tvNoRecordsFound = (TextView) findViewById(R.id.tvNoRecordsFound);
    }

    private void onAddRecord() {
        Intent intent = new Intent(ThreeTabsActivity.this, TableManipulationActivity.class);
        intent.putExtra(Constants.DML_TYPE, Constants.INSERT);
        startActivityForResult(intent, Constants.ADD_RECORD);
    }

    private void displayAllRecords() {
        Log.i("this: ", this.toString());
        ImageView msg, call, pay, records;
        com.rey.material.widget.RelativeLayout inflateParentView;
        parentLayout.removeAllViews();

        ArrayList<ContactModel> contacts = sQLiteHelper.getAllRecords();

        if (contacts.size() > 0) {
            tvNoRecordsFound.setVisibility(View.GONE);
            ContactModel contactModel;
            for (int i = 0; i < contacts.size(); i++) {

                contactModel = contacts.get(i);
                final View view = LayoutInflater.from(this).inflate(R.layout.inflate_record, null);
                records = (ImageView) view.findViewById(R.id.records);
                msg = (ImageView) view.findViewById(R.id.msg);
                pay = (ImageView) view.findViewById(R.id.pay);
                call = (ImageView) view.findViewById(R.id.call);
                Log.i("call: ", call.toString());
                final Holder holder = new Holder();
                inflateParentView = (com.rey.material.widget.RelativeLayout) view.findViewById(R.id.inflateParentView);
                holder.tvFullName = (TextView) view.findViewById(R.id.tvFullName);
                holder.text_balance = (TextView) view.findViewById(R.id.tvbalance);
//                holder.balance = sQLiteHelper.getBalance(contactModel.getNumber());
                holder.balance = contactModel.getBalance();
                holder.phone = (TextView) view.findViewById(R.id.tvphone);
                view.setTag(contactModel.getID());
                holder.firstname = contactModel.getFirstName();
                holder.lastname = contactModel.getLastName();
                //   Log.d("number",StringcontactModel.getNumber());
                holder.number = contactModel.getNumber();
                final String phone_number = String.valueOf(contactModel.getNumber());

//                call.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View call) {
////                        Log.d("Call",view.getTag().toString());
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8144240078"));
//                        if (ActivityCompat.checkSelfPermission(ThreeTabsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//                        startActivity(intent);
//                    }
//                });

//                holder.balance = contactModel.getBalance();
                String firstN = (String) holder.firstname.substring(0, 1).toUpperCase();
                String lastN = (String) holder.firstname.substring(1);
                String firstlastN = (String) holder.lastname.substring(0, 1).toUpperCase();
                String lastlastN = (String) holder.lastname.substring(1);
                String personName = firstN + lastN + " " + firstlastN + lastlastN;

                holder.phone.setText(" " + holder.number);
                holder.tvFullName.setText(personName);

                String Balance = "Rs: " + String.valueOf(holder.balance);
                holder.text_balance.setText(Balance);
                final CharSequence[] items = {Constants.PAY, Constants.TRANSECTIONS, Constants.CALL, Constants.WHATSAPP};
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Log.i("holder.phone: ", holder.phone.getText().toString());
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.phone.getText().toString()));
                        if (ActivityCompat.checkSelfPermission(ThreeTabsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                });
                msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse("smsto:" + holder.phone.getText().toString());
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
//                                    sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hai Good Morning");
//                                    sendIntent.setType("text/plain");
                        sendIntent.putExtra(sendIntent.EXTRA_TEXT, "Your Current Debt is Rs: 1000");
                        sendIntent.setPackage("com.whatsapp");
                        startActivity(sendIntent);
                    }
                });

                records.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View re) {
                        progressDialog.setCancelable(false);
                        progressDialog.setTitle("Processing");
                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();

                        ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
                        if (connectivityChangeReceiver.isConnected(ThreeTabsActivity.this)) {

                            SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                            number = sqLiteHelper.selectPhonenumber(view.getTag().toString());
                            Intent Transectiondetailintent = new Intent(ThreeTabsActivity.this, TransectionDetails.class);
                            Transectiondetailintent.putExtra("number", number);
                            String url = Constants.TRANSACTION_URL + number + "&buyer=" + My_number;
                            Log.d("info", url);
                            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                    new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response) {

                                            // display response
                                            for (int i = 0; i < response.length(); i++) {
                                                try {

                                                    JSONObject jsonObject = response.getJSONObject(String.valueOf(i));
                                                    String transaction_amount = jsonObject.getString("transaction_amount").toString();
                                                    String transaction_time = jsonObject.getString("transaction_time").toString();
                                                    String description = jsonObject.getString("description").toString();
                                                    String transaction_id = jsonObject.getString("transaction_id").toString();
                                                    SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                                                    ContactModel contactModel = new ContactModel();
                                                    contactModel.setTransectionPhoneNumber(Long.parseLong(number));
                                                    contactModel.setTransectionAmount(Long.parseLong(transaction_amount));
                                                    contactModel.setTransectionDateTime(transaction_time);
                                                    contactModel.setTransectionDescription(description);
                                                    contactModel.setTransactionid(transaction_id);
                                                    if (sqLiteHelper.checkTransaction(transaction_id)) {
                                                        sqLiteHelper.insertTransectionRecord(contactModel);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

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
                            try {
                                TimeUnit.SECONDS.sleep(1);
                                progressDialog.dismiss();

                                startActivity(Transectiondetailintent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } else {
                            SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                            number = sqLiteHelper.selectPhonenumber(view.getTag().toString());
                            Toast.makeText(ThreeTabsActivity.this, "Check Your Internet", Toast.LENGTH_LONG).show();
                            Intent Transectiondetailintent = new Intent(ThreeTabsActivity.this, TransectionDetails.class);
                            Transectiondetailintent.putExtra("number", number);
                            startActivity(Transectiondetailintent);
                        }


                    }
                });

                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vi) {
                        ContactModel contactModel;
//                        Log.d("rowId", view.getTag().toString());
//                        Log.d("view", view.getTag().toString());
                        Intent Paytobuyerintent = new Intent(ThreeTabsActivity.this, Paytobuyer.class);
                        SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                        number = sqLiteHelper.selectPhonenumber(view.getTag().toString());
                        Paytobuyerintent.putExtra("Balance", number);
                        Log.d("Number", number);
                        startActivity(Paytobuyerintent);
                    }
                });
                inflateParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ThreeTabsActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ContactModel contactModel;
                                    Log.d("rowId", view.getTag().toString());
                                    Log.d("view", view.getTag().toString());
                                    Intent Paytobuyerintent = new Intent(ThreeTabsActivity.this, Paytobuyer.class);
                                    SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                                    number = sqLiteHelper.selectPhonenumber(view.getTag().toString());


                                    Paytobuyerintent.putExtra("Balance", number);
                                    Log.d("Number", number);
                                    startActivity(Paytobuyerintent);

                                } else if (which == 1) {


                                    ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
                                    if (connectivityChangeReceiver.isConnected(ThreeTabsActivity.this)) {
                                        progressDialog.setCancelable(false);
                                        progressDialog.setTitle("Processing");
                                        progressDialog.setMessage("Please wait..");
                                        progressDialog.show();
                                        SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                                        number = sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                        String url = Constants.TRANSACTION_URL + number + "&buyer=" + My_number;
                                        Log.d("info", url);
                                        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {

                                                    @Override
                                                    public void onResponse(JSONObject response) {

                                                        // display response
                                                        for (int i = 0; i < response.length(); i++) {
                                                            try {

                                                                JSONObject jsonObject = response.getJSONObject(String.valueOf(i));
                                                                String transaction_amount = jsonObject.getString("transaction_amount").toString();
                                                                String transaction_time = jsonObject.getString("transaction_time").toString();
                                                                String description = jsonObject.getString("description").toString();
                                                                String transaction_id = jsonObject.getString("transaction_id").toString();
                                                                SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                                                                ContactModel contactModel = new ContactModel();
                                                                contactModel.setTransectionPhoneNumber(Long.parseLong(number));
                                                                contactModel.setTransectionAmount(Long.parseLong(transaction_amount));
                                                                contactModel.setTransectionDateTime(transaction_time);
                                                                contactModel.setTransectionDescription(description);
                                                                contactModel.setTransactionid(transaction_id);
                                                                Boolean is_transaction = sqLiteHelper.checkTransaction(transaction_id);
                                                                Log.d("is_transaction", String.valueOf(is_transaction));
                                                                if (is_transaction) ;
                                                                {
                                                                    sqLiteHelper.insertTransectionRecord(contactModel);
                                                                }
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

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
                                        try {
                                            TimeUnit.SECONDS.sleep(1);
                                            progressDialog.dismiss();
                                            Intent Transectiondetailintent = new Intent(ThreeTabsActivity.this, TransectionDetails.class);
                                            Transectiondetailintent.putExtra("number", number);
                                            startActivity(Transectiondetailintent);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        SQLiteHelper sqLiteHelper = new SQLiteHelper(ThreeTabsActivity.this);
                                        number = sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                        Toast.makeText(ThreeTabsActivity.this, "Check Your Internet", Toast.LENGTH_LONG).show();
                                        Intent Transectiondetailintent = new Intent(ThreeTabsActivity.this, TransectionDetails.class);
                                        Transectiondetailintent.putExtra("number", number);
                                        startActivity(Transectiondetailintent);
                                    }


                                } else if (which == 2) {

                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.number));
                                    if (ActivityCompat.checkSelfPermission(ThreeTabsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                                //9784787152
                                else if (which == 3) {
                                    Uri uri = Uri.parse("smsto:" + holder.number);
                                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
//                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hai Good Morning");
//                                    sendIntent.setType("text/plain");
                                    sendIntent.putExtra(sendIntent.EXTRA_TEXT, "Your Current Debt is Rs: 1000");
                                    sendIntent.setPackage("com.whatsapp");
                                    startActivity(sendIntent);
//                                    Uri uri = Uri.parse("smsto:" + "919784787152");
//                                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
////                                    Intent sendIntent = new Intent();
//                                    sendIntent.setAction(Intent.ACTION_SEND);
//                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Your Current Debt is Rs: 1000");
//                                    sendIntent.setType("text/plain");

                                    // Do not forget to add this to open whatsApp App specifically
//                                    sendIntent.setPackage("com.whatsapp");
//                                    startActivity(sendIntent);
                                }
                             /*   else {
                                    AlertDialog.Builder deleteDialogOk = new AlertDialog.Builder(MainActivity.this);
                                    deleteDialogOk.setTitle("Delete Contact?");
                                    deleteDialogOk.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //sQLiteHelper.deleteRecord(view.getTag().toString());
                                                    ContactModel contact = new ContactModel();
                                                    contact.setID(view.getTag().toString());
                                                    sQLiteHelper.deleteRecord(contact);
                                                    displayAllRecords();
                                                }
                                            }
                                    );
                                    deleteDialogOk.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    deleteDialogOk.show();
                                }
                                */
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    }
                });
                parentLayout.addView(view);
            }
        } else {
            tvNoRecordsFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onStart();
        bottomBar.selectTabWithId(R.id.tab_favorites);

    }

    public void onBackPressed() {
        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mydetails) {
            Intent Mydetails = new Intent(ThreeTabsActivity.this,ThreeTabsActivity.class);
            startActivity(Mydetails);
            return true;
        }
      /*  if (id == R.id.session)
        {
            settings = context.getSharedPreferences(PREFS_NAMEE, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putInt(FLAG,0);
            editor.commit();
            Intent LoginActivity = new Intent(MainActivity.this,Login.class);
            startActivity(LoginActivity);
        }



        return super.onOptionsItemSelected(item);
    }
*/
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private class Holder {
        TextView tvFullName;
        String firstname;
        long number;
        TextView phone;
        long balance;

        TextView text_balance;
        String lastname;
    }


}