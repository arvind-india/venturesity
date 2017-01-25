package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    Button btnAddNewRecord;
    SQLiteHelper sQLiteHelper;
    int backButtonCount=0;
    LinearLayout parentLayout;
    LinearLayout layoutDisplayPeople;
    public static final String PREFS_NAME = "Login_details";
    TextView tvNoRecordsFound;
    SharedPreferences settings;
    Activity context = this;
    SharedPreferences.Editor editor;
    public static final String PhoneNumber = "phoneKey";
    private String rowID = null;
    public static final String FLAG = "session";
    private String number;
    private String ph_num;
    private ArrayList<HashMap<String, String>> tableData = new ArrayList<HashMap<String, String>>();
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAllWidgets();
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(20000);
        ph_num = settings.getString(PhoneNumber,null);
        Bundle bundle = new Bundle();
        String id = "1year";
        String name = "Annual membership subscription";
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ph_num);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "RUPEES");
        bundle.putString(FirebaseAnalytics.Param.PRICE, "299.00");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    //    FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
// Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Qrscanner = new Intent(MainActivity.this,QRScanner.class);
                startActivity(Qrscanner);
            }
        });
        sQLiteHelper = new SQLiteHelper(MainActivity.this);
        bindWidgetsWithEvent();
        displayAllRecords();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String firstname = data.getStringExtra(Constants.FIRST_NAME);
            String lastname = data.getStringExtra(Constants.LAST_NAME);
            long number = Long.parseLong(data.getStringExtra(Constants.NUMBER));
            long balance = Long.parseLong(data.getStringExtra(Constants.BALANCE));
            Log.d("sumit",data.getStringExtra(Constants.BALANCE));
            ContactModel contact = new ContactModel();
            contact.setFirstName(firstname);
            contact.setLastName(lastname);
            contact.setNumber(number);
            contact.setBalance(balance);
            if (requestCode == Constants.ADD_RECORD) {
                //sQLiteHelper.insertRecord(firstname, lastname);
                sQLiteHelper.insertRecord(contact);
            } else if (requestCode == Constants.UPDATE_RECORD) {
                contact.setID(rowID);
                //sQLiteHelper.updateRecord(firstname, lastname, rowID);
                sQLiteHelper.updateRecord(contact);
            }
            displayAllRecords();
        }
    }


    private void getAllWidgets() {
        btnAddNewRecord = (Button) findViewById(R.id.btnAddNewRecord);

        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        layoutDisplayPeople = (LinearLayout) findViewById(R.id.layoutDisplayPeople);

        tvNoRecordsFound = (TextView) findViewById(R.id.tvNoRecordsFound);
    }

    private void bindWidgetsWithEvent() {
        btnAddNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddRecord();
            }
        });
    }

    private void onAddRecord() {
        Intent intent = new Intent(MainActivity.this, TableManipulationActivity.class);
        intent.putExtra(Constants.DML_TYPE, Constants.INSERT);
        startActivityForResult(intent, Constants.ADD_RECORD);
    }

    private void onUpdateRecord(String firstname, String lastname , long number , long balance) {
        Intent intent = new Intent(MainActivity.this, TableManipulationActivity.class);
        intent.putExtra(Constants.FIRST_NAME, firstname);
        intent.putExtra(Constants.LAST_NAME, lastname);
        intent.putExtra(Constants.NUMBER,number);
        intent.putExtra(Constants.BALANCE,balance);
        intent.putExtra(Constants.DML_TYPE, Constants.UPDATE);
        startActivityForResult(intent, Constants.UPDATE_RECORD);
    }


    private void displayAllRecords() {

        com.rey.material.widget.RelativeLayout inflateParentView;
        parentLayout.removeAllViews();

        ArrayList<ContactModel> contacts = sQLiteHelper.getAllRecords();

        if (contacts.size() > 0) {
            tvNoRecordsFound.setVisibility(View.GONE);
            ContactModel contactModel;
            for (int i = 0; i < contacts.size(); i++) {

                contactModel = contacts.get(i);

                final Holder holder = new Holder();
                final View view = LayoutInflater.from(this).inflate(R.layout.inflate_record, null);
                inflateParentView = (com.rey.material.widget.RelativeLayout) view.findViewById(R.id.inflateParentView);
                holder.tvFullName = (TextView) view.findViewById(R.id.tvFullName);


                view.setTag(contactModel.getID());
                holder.firstname = contactModel.getFirstName();
                holder.lastname = contactModel.getLastName();
             //   Log.d("number",StringcontactModel.getNumber());
                holder.number = contactModel.getNumber();
                holder.balance = sQLiteHelper.getBalance(contactModel.getNumber());
                String personName = holder.firstname + " " + holder.lastname + " " + holder.number + " " + holder.balance;
                holder.tvFullName.setText(personName);

                final CharSequence[] items = {Constants.PAY,Constants.TRANSECTIONS  };
                inflateParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                 if(which == 0) {
                                    ContactModel contactModel;
                                Log.d("rowId",view.getTag().toString());
                                    Log.d("view",view.getTag().toString());
                                  //  ContactModel contact = new ContactModel();
                                   // contact.getNumber();
                                    Intent Paytobuyerintent = new Intent(MainActivity.this, Paytobuyer.class);
                                    SQLiteHelper sqLiteHelper= new SQLiteHelper(MainActivity.this);
                                    number =  sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                    Paytobuyerintent.putExtra("Balance", number);
                                    Log.d("Number",number);
                                    startActivity(Paytobuyerintent);

                                }
                                else if(which==1)
                                {   SQLiteHelper sqLiteHelper= new SQLiteHelper(MainActivity.this);
                                    number =sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                    Intent Transectiondetailintent = new Intent(MainActivity.this, TransectionDetails.class);
                                    Transectiondetailintent.putExtra("number",number);
                                    startActivity(Transectiondetailintent);
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

    private class Holder {
        TextView tvFullName;
        String firstname;
        long number;
        long balance;
        String lastname;
    }
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
            Intent Mydetails = new Intent(MainActivity.this,MyDetails.class);
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
*/

        return super.onOptionsItemSelected(item);
    }
    @Override

    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

}
