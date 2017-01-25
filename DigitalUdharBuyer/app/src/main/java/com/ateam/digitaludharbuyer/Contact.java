package com.ateam.digitaludharbuyer;

/**
 * Created by Sumit on 12/14/2016.
 */

public class Contact {

    String first_name , last_name , phone_number , Token, Password, Refer;

    public Contact(String first_name ,String last_name, String password , String phone_number , String Token, String Refer)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.Token = Token;
        this.Password = password;
        this.Refer = Refer;
    }

    public Contact(String phone_number, String password, String token )
    {
        this.phone_number = phone_number;
        this.Password = password;
        this.Token = token;
    }
}
