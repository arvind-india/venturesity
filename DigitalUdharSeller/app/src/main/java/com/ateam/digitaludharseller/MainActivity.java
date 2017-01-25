package com.ateam.digitaludharseller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends BaseActivity {
    public static final String FLAG = "session";
    int backButtonCount=0;
    Button btnAddNewRecord;
    private EditText textEtxt;
    private Button saveButton;
    private Button activity2Button;
    private String LastName;
    private String FirstName;
    private SharedPreference sharedPreference;
    public static final String PREFS_NAME = "Login_details";
    SharedPreferences settings;
    private EditText textphone;
    private EditText lname;
    private String save;
    SharedPreferences.Editor editor;

    SharedPreferences sharedpreferences;
    Activity context = this;
    public static final String PREFS_NAMEE = "Login_details";
    SQLiteHelper sQLiteHelper;
//    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private String Phnonenumber;
    LinearLayout parentLayout;
    LinearLayout layoutDisplayPeople;

    TextView tvNoRecordsFound;
    private String rowID = null;

    private ArrayList<HashMap<String, String>> tableData = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(getResources().getColor(R.color.color_blue));
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddRecord();
            }
        });
        getAllWidgets();
        sQLiteHelper = new SQLiteHelper(MainActivity.this);
        bindWidgetsWithEvent();
        displayAllRecords();
    }

  /*  private void scanQR() {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }
*/

  /*  private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
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
   /* public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
*/
 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
           // String format = data.getStringExtra("SCAN_RESULT_FORMAT");
           // Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
           // toast.show();
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
*/

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
                Intent Qrscanner = new Intent(MainActivity.this,QRScanner.class);
                startActivity(Qrscanner);

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
//                Log.d("BALANCEFROMTRANSACTION", String.valueOf());
                String personName = holder.firstname + " " + holder.lastname + " " + holder.number + " " + holder.balance;
                holder.tvFullName.setText(personName);

                final CharSequence[] items = {Constants.ADD_MONEY, Constants.transactionS};

                inflateParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                    Intent Addmoney = new Intent(MainActivity.this,Add_Money.class);
                                    SQLiteHelper sqLiteHelper= new SQLiteHelper(MainActivity.this);
                                    Phnonenumber =  sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                    Addmoney.putExtra("phone",Phnonenumber);
                                    startActivity(Addmoney);
                                }
                                else if(which==1)
                                {   String number;
                                    SQLiteHelper sqLiteHelper= new SQLiteHelper(MainActivity.this);
                                    number =sqLiteHelper.selectPhonenumber(view.getTag().toString());
                                    Intent transactiondetailintent = new Intent(MainActivity.this, TransactionDetails.class);
                                    transactiondetailintent.putExtra("number",number);
                                    startActivity(transactiondetailintent);
                                }
                            /*    else if(which==1) {
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
            Intent Mydetails = new Intent(MainActivity.this,ThreeTabsActivity.class);
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


    private class Holder {
        TextView tvFullName;
        String firstname;
        long number;
        long balance;
        String lastname;
    }
    @Override
    protected void onResume() {
        super.onStart();
        if(checkValidation())
        {
            Toast.makeText(this,"Product is Activated",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Renew Your Product", Toast.LENGTH_LONG).show();
            Intent PaymentActivity = new Intent(this, PaymentActivity.class);
            startActivity(PaymentActivity);
        }


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
