package com.ateam.digitaludharseller;

public class ContactModel {

    private String ID, firstName, lastName,syncDateTime,transactionDateTime,transactionDescription,transactionCategory,transactionid;
    private long number,balance,transactionPhoneNumber;
    public long transactionAmount;
    private int sync ,scan;
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public long getNumber() {
        return number;
    }

    public long getBalance() {
        return balance;
    }
    public int getSync(){
        return sync;
    }
    public String getTransactionCategory()
    {
        return transactionCategory;
    }
    public String getTransactionid(){
        return transactionid;
    }

    public String getSyncDateTime(){
        return syncDateTime;
    }
    public long gettransactionPhoneNumber(){ return transactionPhoneNumber;}
    public long gettransactionAmount(){return transactionAmount;}
    public String gettransactionDateTime(){return transactionDateTime;}
    public String gettransactionDescription(){return transactionDescription;}
    public int getscan_Info(){return scan;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setNumber(long number) {
        this.number = number ;
    }
    public void settransactionPhoneNumber(long transactionPhoneNumber){
        this.transactionPhoneNumber = transactionPhoneNumber;
    }
    public void setTransactionCategory(String transactionCategory){
        this.transactionCategory=transactionCategory;
    }
    public void setTransactionid(String transactionid)
    {
        this.transactionid = transactionid;
    }
    public void setSyncDateTime(String syncDateTime){
        this.syncDateTime = syncDateTime;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }
    public void setSync(int sync){
        this.sync = sync;
    }
    public void setScan(int scan) {this.scan = scan;}
    public void settransactionAmount(long transactionAmount){
        this.transactionAmount = transactionAmount;}
    public void settransactionDateTime(String transactionDateTime){
        this.transactionDateTime=transactionDateTime;}
    public void settransactionDescription(String transactionDescription){
        this.transactionDescription = transactionDescription;
    }


}
