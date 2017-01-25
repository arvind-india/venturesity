package com.ateam.digitaludharseller;

/**
 * Created by Sumit on 12/14/2016.
 */

public class Contact {

    String first_name , last_name , phone_number , Token , Refer , Agent , my_code , password;

    public Contact(String first_name ,String last_name , String phone_number , String Token , String Refer , String Agent , String my_code, String password)
    {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.Token = Token;
        this.Agent = Agent;
        this.Refer = Refer;
        this.my_code = my_code;
        this.password = password;
    }

    public Contact(String phone_number )
    {
        this.phone_number = phone_number;

    }
}
