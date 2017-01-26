package com.ateam.digitaludharbuyer;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SignIn extends AppCompatActivity {
    String phone;
    private EditText inputName,inputPassword;
    private TextInputLayout inputLayoutUser, inputLayoutPassword;
    private Button btnSignUp;
    // UI References
    public static final String SYNC = "sync";
    private JsonObjectRequest getRequestt;
    private ProgressDialog progressDialog;
    private String password;
    private SharedPreference sharedPreference;
    SharedPreferences settings;
    private RequestQueue queue;
    private String save;
    SharedPreferences.Editor editor;
    Activity context = this;
    public static final String PREFS_NAMEE = "Login_details";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String LName = "lastnamekey";
    public static final String MYCODE = "mycode";
    public static final String FLAG = "session";
    public static final String POINTS = "points";
    public static final String PREFS_NAME = "Login_details";
    public static final String Name = "firstnameKey";
    public static final String Phone = "phoneKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(SignIn.this);
        TextView signin = (TextView) findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(SignIn.this,Login.class);
                startActivity(intent);
            }
        });
        queue = Volley.newRequestQueue(this);
        inputLayoutUser = (TextInputLayout) findViewById(R.id.input_layout_phone);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_passw);
        inputName = (EditText) findViewById(R.id.input_user);
        inputPassword = (EditText) findViewById(R.id.input_passwordd);
        btnSignUp = (Button) findViewById(R.id.btn_signin);
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        sharedPreference = new SharedPreference();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Processing");
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                submitForm();
            }
        });


        // subscribeToPushService();

    }
    private void submitForm() {
        if (!validatePass()) {
            return;
        }
        if (!validatePassword()) {
            return;
        } else {

            ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
            if (connectivityChangeReceiver.isConnected(this)) {
                phone = inputName.getText().toString();
                // Hides the soft keyboard

                String phone_number = inputName.getText().toString();
                String pass = inputPassword.getText().toString();
                save = phone;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                Log.d("Shared", save);
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                String token = FirebaseInstanceId.getInstance().getToken();
                String url = Constants.LoginUrl + phone_number + "&registration_id=" + token + "&password=" + pass;

                Log.d("info", url);

                getRequestt = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                Log.d("Response", response.toString());

                                if (response.has("username")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                                    builder.setMessage("Password does not matches with Username");
                                    builder.setPositiveButton("OK", null);
                                    builder.show();
                                    progressDialog.dismiss();
                                }
                                else if(response.has("users"))
                                {
                                    String urll = Constants.MYDETAILS + inputName.getText().toString();
                                    Log.d("info",urll);
                                    // Save the text in SharedPreference
                                    sharedPreference.save(context, phone);
                                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urll, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // display response
                                                    settings = context.getSharedPreferences(PREFS_NAMEE, Context.MODE_PRIVATE);
                                                    editor = settings.edit();
                                                    editor.putBoolean(SYNC, true);
                                                    editor.commit();
                                                    String username = null;
                                                    try {
                                                        username = response.getString("username");
                                                        String first_name = response.getString("first_name");
                                                        String last_name = response.getString("last_name");
//                                                        String my_code = response.getString("my_code");
//                                                        String points = response.getString("points");
                                                        editor.putString(Name, first_name);
                                                        editor.putString(LName, last_name);
                                                        editor.putInt(FLAG, 1);
                                                        editor.putString(Phone, username);
//                                                        editor.putString(MYCODE, my_code);
//                                                        editor.putString(POINTS, points);
                                                        editor.commit();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    progressDialog.dismiss();
                                                    Intent Threetab = new Intent(SignIn.this, ThreeTabsActivity.class);
                                                    startActivity(Threetab);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("Error", error.toString());
                                                    progressDialog.dismiss();
                                                }
                                            });

//                //30 seconds - change to what you want
                                    RetryPolicy policy = new DefaultRetryPolicy(Constants.socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    getRequest.setRetryPolicy(policy);
                                    // add it to the RequestQueue
                                    queue.add(getRequest);
                                }
                                else {
                                    Log.i("Responce",response.toString());
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
//                                            JSONArray jsonArray =response.getJSONArray()
                                            JSONObject jsonObject = response.getJSONObject(String.valueOf(i));
                                            String firstname = jsonObject.getString("first_name").toString();
                                            String lastname = jsonObject.getString("last_name").toString();
                                            String phnumber = jsonObject.getString("username").toString();
                                            String balance = jsonObject.getString("balance").toString();
                                            SQLiteHelper sqLiteHelper = new SQLiteHelper(SignIn.this);
                                            ContactModel contactModel = new ContactModel();
                                            contactModel.setFirstName(firstname);
                                            contactModel.setLastName(lastname);
                                            contactModel.setNumber(Long.parseLong(phnumber));
                                            contactModel.setBalance(Long.parseLong(balance));
                                            sqLiteHelper.insertRecord(contactModel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        // Internet_Flag = 1;
                                    }

                                    String urll = Constants.MYDETAILS + inputName.getText().toString();
                                    // Save the text in SharedPreference
                                    sharedPreference.save(context, phone);
                                    JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, urll, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // display response
                                                    settings = context.getSharedPreferences(PREFS_NAMEE, Context.MODE_PRIVATE);
                                                    editor = settings.edit();
                                                    editor.putBoolean(SYNC, true);
                                                    editor.commit();
                                                    String username = null;
                                                    try {
                                                        username = response.getString("username");
                                                        String first_name = response.getString("first_name");
                                                        String last_name = response.getString("last_name");
//                                                        String my_code = response.getString("my_code");
//                                                        String points = response.getString("points");
                                                        editor.putString(Name, first_name);
                                                        editor.putString(LName, last_name);
                                                        editor.putInt(FLAG, 1);
                                                        editor.putString(Phone, username);
//                                                        editor.putString(MYCODE, my_code);
//                                                        editor.putString(POINTS, points);
                                                        editor.commit();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }


                                                    progressDialog.dismiss();
                                                    Intent Threetab = new Intent(SignIn.this, ThreeTabsActivity.class);
                                                    startActivity(Threetab);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.d("Error", error.toString());
                                                    progressDialog.dismiss();
                                                }
                                            });

//                //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(Constants.socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            getRequest.setRetryPolicy(policy);
                                    // add it to the RequestQueue
                                    queue.add(getRequest);

                                }

                            }


                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error", error.toString());
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                                builder.setMessage("Password does not matches with Username");
                                builder.setPositiveButton("OK", null);
                                builder.show();
                                progressDialog.dismiss();

                            }
                        });
                queue.add(getRequestt);
                RetryPolicy policy = new DefaultRetryPolicy(Constants.socketTimeout,0,0);
                getRequestt.setRetryPolicy(policy);
                // add it to the RequestQueue
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                builder.setMessage("Check Your Internet Connection");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }
    }

    private boolean validatePass() {
        if(inputPassword.getText().toString().trim().isEmpty()){
            inputLayoutPassword.setError(("Password cannot be empty!!"));
            requestFocus(inputPassword);
            return false;
        }
        else {inputLayoutPassword.setErrorEnabled(false);}
        return true;
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutUser.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutUser.setErrorEnabled(false);
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
                case R.id.input_user:
                    validateName();
                    break;
                case R.id.input_passwordd:
                    validatePass();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.getInt(FLAG, 0) == 1) {
            Intent intent = new Intent(SignIn.this, ThreeTabsActivity.class);
            startActivity(intent);
        }

    }
    }

