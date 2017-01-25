package com.ateam.digitaludharbuyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransectionDetails extends AppCompatActivity {
    private TextView transaction_details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout parentLayout;
        setContentView(R.layout.activity_transection_details);
        long number;
        transaction_details = (TextView) findViewById(R.id.transaction);
        SQLiteHelper sqLiteHelper= new SQLiteHelper(TransectionDetails.this);
        Intent intent = getIntent();
        number = Long.parseLong(intent.getStringExtra("number"));
        Log.d("number to pay", String.valueOf(number));
        ArrayList<ContactModel> contacts = sqLiteHelper.getTransection(number);
        Log.d("CONTANCTs", String.valueOf(contacts.size()));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.textview, getArray(contacts));

        ListView listView = (ListView) findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        }

    public class Transection{
        TextView TrDeails;
        long number;
        String describtion;
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
                final Transection transection = new Transection();
                transection.number = contactModel.getTransectionPhoneNumber();
                transection.amount = contactModel.getTransectionAmount();
                transection.time = contactModel.getTransectionDateTime();
                transection.describtion = contactModel.getTransectionDescription();
//                Log.d("describtion",transection.describtion.toString());
                //   Log.d("number",StringcontactModel.getNumber());
                String personName = transection.number + "    Rs" + transection.amount + "\n" +"TIME :   "+ transection.time  + "\n"+ "Transaction Details :"  + transection.describtion;
                details.add(personName);
            }
        }
        else {
            transaction_details.setText("No Transactions");
        }
        return details;

    }


}