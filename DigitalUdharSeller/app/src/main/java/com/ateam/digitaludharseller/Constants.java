package com.ateam.digitaludharseller;

//"http://app.digitalkhata.com"
public class Constants {
//    public static final String BASE_URL = "http://app.digitalkhata.com";
    public static final String BASE_URL = "http://aefbc591.ngrok.io/venturesity/";
    public static final String LOGIN = BASE_URL + "/api/user/login";
    public static int socketTimeout = 30000;
    public static final String WHATSAPP = "whatsapp";
    public static final String PREFS_NAMEE = "Login_details";
    public static final String MYDETAILS  = BASE_URL +"sellerdetails.php?username=";
    public static final String LoginUrl =  BASE_URL +"seller_login.php?username=";
    public static final String TRANSACTION_URL= BASE_URL +"/api/user/transactions/?seller=";
    //    public static final String PaymentAddress = "http://192.168.2.9:8000/api/user/razorpay/?payment_id=";
    public static final String PaymentAddress = BASE_URL + "/api/user/razorpay/?payment_id=";
    public static final String url = BASE_URL + "add_buyer.php?full_name=";
    public static final String transactions_url = BASE_URL + "/api/user/add_money/?seller_number=";
    public static final String transactions_pay_url = BASE_URL + "/api/user/pay_money/?seller_number=";
    public static final String isScannedurl = BASE_URL + "/api/user/scan_pay_money/?seller_number=";
    public static final String SERVER_ADDRESS = BASE_URL;
    public static final int ADD_RECORD = 0;
    public static final int UPDATE_RECORD = 1;
    public static final String DML_TYPE = "DML_TYPE";
    public static final String UPDATE = "Update";
    public static final String INSERT = "Insert";
    public static final String ADD_MONEY = "Add Money";
    public static final String DELETE = "Delete";
    public static final String FIRST_NAME = "Firstname";
    public static final String LAST_NAME = "Lastname";
    public static final String NUMBER  = "Number";
    public static final String CALL = "Call";
    public static final String BALANCE = "Balance";
    public static final String transactionS = "Transaction";
    public static final String ID = "ID";
}


