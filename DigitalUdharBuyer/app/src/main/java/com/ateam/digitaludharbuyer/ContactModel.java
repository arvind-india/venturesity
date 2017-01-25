package com.ateam.digitaludharbuyer;

public class ContactModel {

    private String ID, firstName, lastName,syncDateTime,transectionDateTime,transectionDescription,transactionCategory,transactionid;
    private long number,balance,transectionPhoneNumber,transactionPhoneNumber;
    private int sync,scan;
    public long transectionAmount;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
    public long gettransactionPhoneNumber(){ return transactionPhoneNumber;}
    public long gettransactionAmount(){return transectionAmount;}

    public String getFirstName() {
        return firstName;
    }
    public String getTransactionid(){
        return transactionid;
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
    public String getSyncDateTime(){
        return syncDateTime;
    }
    public long getTransectionPhoneNumber(){ return transectionPhoneNumber;}
    public long getTransectionAmount(){return transectionAmount;}
    public String getTransectionDateTime(){return transectionDateTime;}
    public String getTransectionDescription(){return transectionDescription;}
    public String getTransactionCategory()
    {
        return transactionCategory;
    }
    public int getscan_Info(){return scan;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setTransactionCategory(String transactionCategory){
        this.transactionCategory=transactionCategory;
    }
    public void settransactionPhoneNumber(long transactionPhoneNumber){
        this.transactionPhoneNumber = transactionPhoneNumber;
    }
    public void settransactionAmount(long transactionAmount){
        this.transectionAmount = transactionAmount;}
    public void setTransactionid(String transactionid)
    {
        this.transactionid = transactionid;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setNumber(long number) {
        this.number = number ;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
    public void setSync(int sync){
        this.sync = sync;
    }
    public void setTransectionPhoneNumber(long transectionPhoneNumber){
        this.transectionPhoneNumber = transectionPhoneNumber;
    }
    public void setSyncDateTime(String syncDateTime){
        this.syncDateTime = syncDateTime;
    }
    public void setScan(int scan) {this.scan = scan;}

    public void setTransectionAmount(long transectionAmount){
        this.transectionAmount = transectionAmount;}
    public void setTransectionDateTime(String transectionDateTime){
        this.transectionDateTime=transectionDateTime;}
    public void setTransectionDescription(String transectionDescription){
        this.transectionDescription = transectionDescription;
    }

}
