package com.ateam.digitaludharbuyer;


public class Constants {
    public static final int ADD_RECORD = 0;

    public static final int UPDATE_RECORD = 1;
    //    public static final String BASE_URL = "http://app.digitalkhata.com";
    public static final String BASE_URL = "http://aefbc591.ngrok.io/venturesity/";
    public static final String SERVER_ADDRESS = BASE_URL + "/api/user/";
    public static final String Signup = BASE_URL ;
    public static final String CALL = "Call";
    public static final String DML_TYPE = "DML_TYPE";
    public static final String UPDATE = "Update";
    public static final String WHATSAPP = "whatsapp";
    public static final String PAY = "Pay";
    public static int socketTimeout = 30000;
    public static final String INSERT = "Insert";
    public static final String DELETE = "Delete";
    public static final String LoginUrl =  BASE_URL +"buyer_login.php?username=";
    public static final String FIRST_NAME = "Firstname";
    public static final String LAST_NAME = "Lastname";
    public static final String NUMBER  = "Number";
    public static final String BALANCE = "Balance";
    public static final String TRANSECTIONS = "Transaction";
    public static final String MYDETAILS  = BASE_URL + "buyerdetails.php?username=";
    public static final String TRANSACTION_URL= BASE_URL +"/api/user/transactions/?seller=";
    public static final String transections_url =  BASE_URL+ "pay_money.php?seller_number=";
    public static final String transactions_url = BASE_URL +  "/api/user/add_money/?seller_number=";
    public static final String transactions_pay_url = BASE_URL+ "pay_money.php?seller_number=";
    public static final String ADD_SELLER_URL = BASE_URL + "add_seller.php?full_name=";
    public static final String SCAN_ADD_URL =  BASE_URL + "/api/user/scan_add_money/?seller_number=";
    public static final String ID = "ID";
}
