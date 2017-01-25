package com.ateam.digitaludharseller;

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

import java.util.UUID;

public class Login extends AppCompatActivity {
    String phone;
    private EditText inputName, inputEmail, inputPassword , inputAgentCode, inputReferCode, inputPass;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutAgent, inputLayoutRefer , inputLayoutpass;
    private Button btnSignUp;
    // UI References
    private String my_code;
    private String LastName;
    private String FirstName;
    private SharedPreference sharedPreference;
    SharedPreferences settings;
    private String save;
    SharedPreferences.Editor editor;
    Activity context = this;
    public static final String PREFS_NAMEE = "Login_details";
    public static final String POINTS = "points";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String LName = "lastnamekey";
    public static final String FLAG = "session";
    public static final String REFER = "mycode";
    public static final String PREFS_NAME = "Login_details";
    public static final String Name = "firstnameKey";
    public static final String Phone = "phoneKey";
    private String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutAgent = (TextInputLayout) findViewById(R.id.input_layout_agent_code);
        inputLayoutRefer = (TextInputLayout) findViewById(R.id.input_layout_refer_code);
        inputLayoutpass = (TextInputLayout) findViewById(R.id.input_layout_pas_code);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputAgentCode = (EditText) findViewById(R.id.input_agent);
        inputReferCode = (EditText)  findViewById(R.id.inputrefer_code);
        inputPass = (EditText) findViewById(R.id.inputpass);

//        inputName.setText("Sumit");
//        inputEmail.setText("8144240078");
//        inputPassword.setText("Mehra");
//        inputAgentCode.setText("sss727");
//        inputReferCode.setText("aaaaa");
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputAgentCode.addTextChangedListener(new MyTextWatcher(inputAgentCode));
        inputReferCode.addTextChangedListener(new MyTextWatcher(inputReferCode));
        sharedPreference = new SharedPreference();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        } else {
            //FIXME: DRY ?
            FirstName = inputName.getText().toString();
            LastName = inputPassword.getText().toString();
            phone = inputEmail.getText().toString();
            // Hides the soft keyboard
            settings = context.getSharedPreferences(PREFS_NAMEE, Context.MODE_PRIVATE);
            editor = settings.edit();
            String fname = inputName.getText().toString();
            String lname = inputPassword.getText().toString();
            String phone_number =  inputEmail.getText().toString();
            String Agent = inputAgentCode.getText().toString();
            String Refer = inputReferCode.getText().toString();
            Password = inputPass.getText().toString();
            UUID uuid = UUID.randomUUID();
            String firstN = FirstName.substring(0,1);
            String lastN = LastName.substring(0,1);
            String phoneN = phone.substring(0,1);
            String stringuuid = uuid.toString().substring(0,3);
            my_code = firstN + lastN + phoneN + stringuuid;
            Log.d("My Code",my_code);
            save = phone;
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
            Log.d("Shared", save);
            if (getIntent().getExtras() != null) {

                for (String key : getIntent().getExtras().keySet()) {
                    String value = getIntent().getExtras().getString(key);
//TODO I AM HERE
//                    if (key.equals("AnotherActivity") && value.equals("True")) {
//                        Intent intent = new Intent(this, ThreeTabsActivity.class);
//                        intent.putExtra("value", value);
//                        startActivity(intent);
//                        finish();
//                    }

                }
            }
            FirebaseMessaging.getInstance().subscribeToTopic("news");

        //    Log.d("AndroidBash", "Subscribed");
         //   Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();

            String token = FirebaseInstanceId.getInstance().getToken();

            //TODO: if there is not token, retry auto or manual
            // Log and toast
//            Log.d("AndroidBash", token);
          //  Toast.makeText(Login.this, token, Toast.LENGTH_SHORT).show();
            // Save the text in SharedPreference
            ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
            sharedPreference.save(context, phone);
            if(validateAgent()) {
                if (connectivityChangeReceiver.isConnected(this)) {
                    Log.d("Yo!","Y");
                    Contact contact = new Contact(fname, lname, phone_number, token, Refer, Agent, my_code,Password);
                    final ServerRequests serverRequests = new ServerRequests(this);
                    serverRequests.storeDataInBackground(contact, new GetUserCallback() {

                        @Override
                        public void done(Contact returnedContact) {
                            if(ServerRequests.error==false) {
                                Log.i("Server Responce",String.valueOf(ServerRequests.error));
                                Intent Basectivity = new Intent(Login.this, SignIn.class);
                                startActivity(Basectivity);
                            }
                            else {
                                Toast.makeText(Login.this,"User already exists",Toast.LENGTH_LONG).show();
                            }

//                              {
//
////                                editor.putString(Name, FirstName);
////
////                                editor.putString(LName, LastName);
////                                editor.putString(POINTS,"0");
////                                editor.putString(Phone, phone);
////                                editor.putString(REFER, my_code);
////                                editor.commit();
////                                Intent Mainactivity = new Intent(Login.this, SignIn.class);
////                                startActivity(Mainactivity);
//                            }
                        }
                    });
                }
                else
                {
                    Log.d("third","ja");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage("Check Your Internet Connection");
                    builder.setPositiveButton("OK", null);
                    //  Log.d("qq","qq");
                    builder.show();

                }
            }

        }
    }

    private boolean validateAgent(){
        if (inputAgentCode.getText().toString().trim().isEmpty()) {
            inputLayoutAgent.setError("Enter Agent Code");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

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
            Log.d("Yo!!","It happens");
            ServerRequests.error  = false;

        }


        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_agent:
                    validateAgent();
                    break;
            }
        }
    }





}