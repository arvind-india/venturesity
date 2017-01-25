package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {

    String phone;
    private EditText inputName, inputEmail, inputPassword, inputpass, inputrefer;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutpass, inputLayoutrrfer;
    private Button btnSignUp;
    // UI References
    private String Refer;
    private String LastName;
    private String password;
    private String FirstName;
    private SharedPreference sharedPreference;
    SharedPreferences settings;
    private String save;
    SharedPreferences.Editor editor;
    Activity context = this;
    public static final String PREFS_NAMEE = "Login_details";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String LName = "lastnamekey";
    public static final String FLAG = "session";
    public static final String PREFS_NAME = "Login_details";
    public static final String Name = "firstnameKey";
    public static final String Phone = "phoneKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inputLayoutrrfer = (TextInputLayout) findViewById(R.id.input_layout_refer);
        inputLayoutpass = (TextInputLayout) findViewById(R.id.input_layout_pass);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputrefer = (EditText) findViewById(R.id.input_refer);
        inputName = (EditText) findViewById(R.id.input_name);
        inputpass = (EditText) findViewById(R.id.input_pass);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));


        sharedPreference = new SharedPreference();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });


       // subscribeToPushService();

    }


    private void submitForm() {
        if(!validatePass()){
            return;
        }
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        } else {

            ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
            if(connectivityChangeReceiver.isConnected(this)) {
                FirstName = inputName.getText().toString();
                LastName = inputPassword.getText().toString();
                phone = inputEmail.getText().toString();
                Refer= inputrefer.getText().toString();
                // Hides the soft keyboard
                settings = context.getSharedPreferences(PREFS_NAMEE, Context.MODE_PRIVATE);
                editor = settings.edit();
                String fname = inputName.getText().toString();
                String lname = inputPassword.getText().toString();
                String phone_number = inputEmail.getText().toString();
                password = inputpass.getText().toString();
                save = phone;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                Log.d("Shared", save);
                if (getIntent().getExtras() != null) {

                    for (String key : getIntent().getExtras().keySet()) {
                        String value = getIntent().getExtras().getString(key);

                        if (key.equals("AnotherActivity") && value.equals("True")) {
//                            Intent intent = new Intent(this, ThreeTabsActivity.class);
//                            intent.putExtra("value", value);
//                            startActivity(intent);
//                            finish();
                        }

                    }
                }
                FirebaseMessaging.getInstance().subscribeToTopic("news");

//                Log.d("AndroidBash", "Subscribed");
//                Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();

                String token = FirebaseInstanceId.getInstance().getToken();
                editor.putString(Name, FirstName);

                editor.putString(LName, LastName);
                editor.putString(Phone, phone);
                editor.commit();
                // Log and toast
//                Log.d("AndroidBash", token);
//                Toast.makeText(Login.this, token, Toast.LENGTH_SHORT).show();
                // Save the text in SharedPreference
                sharedPreference.save(context, phone);
                Contact contact = new Contact(fname, lname, password, phone_number, token, Refer);
                ServerRequests serverRequests = new ServerRequests(this);
                serverRequests.storeDataInBackground(contact, new GetUserCallback() {
                    @Override
                    public void done(Contact returnedContact) {
                        if(ServerRequests.error==true) {

                            Intent Basectivity = new Intent(Login.this, SignIn.class);
                            startActivity(Basectivity);
                        }
                        else {
                            Toast.makeText(Login.this,"User already exists",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setMessage("Check Your Internet Connection");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }
    }

    private boolean validatePass() {
        if(inputpass.getText().toString().trim().isEmpty()){
            inputLayoutpass.setError(("Password cannot be empty!!"));
            requestFocus(inputpass);
            return false;
        }
        else {inputLayoutName.setErrorEnabled(false);}
        return true;
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.PHONE.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_pass:
                    validatePass();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }



    private void subscribeToPushService() {

    }

}