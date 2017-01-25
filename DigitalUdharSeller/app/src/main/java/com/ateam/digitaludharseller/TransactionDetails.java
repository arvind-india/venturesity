package com.ateam.digitaludharseller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionDetails extends BaseActivity {
private TextView transaction_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout parentLayout;
        setContentView(R.layout.activity_transaction_details);
        long number;
        SQLiteHelper sqLiteHelper= new SQLiteHelper(TransactionDetails.this);
        transaction_details = (TextView) findViewById(R.id.transaction);
        Intent intent = getIntent();
        number = Long.parseLong(intent.getStringExtra("number"));
        Log.d("number to pay", String.valueOf(number));
        ArrayList<ContactModel> contacts = sqLiteHelper.gettransaction(number);
        Log.d("CONTANCTs", String.valueOf(contacts.size()));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.textview, getArray(contacts));
        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
    }

    public class transaction{
        TextView TrDeails;
        long number;
        long amount;
        String time;
    }

    public ArrayList<String> getArray(ArrayList<ContactModel> contacts)
    {   ArrayList details = new ArrayList();
        if (contacts.size() > 0) {

            //tvNoRecordsFound.setVisibility(View.GONE);
            ContactModel contactModel;
            for (int i = 0; i < contacts.size(); i++) {

                contactModel = contacts.get(i);
                final transaction transaction = new transaction();
                transaction.number = contactModel.gettransactionPhoneNumber();
                transaction.amount = contactModel.gettransactionAmount();
                transaction.time = contactModel.gettransactionDateTime();
                //   Log.d("number",StringcontactModel.getNumber());
                String personName = transaction.number + "    Rs " + transaction.amount + "\n" +"TIME :   "+ transaction.time;
                details.add(personName);
            }
        }
        else {
            transaction_details.setText("No Transactions");
        }
        return details;

    }

}