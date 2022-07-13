package com.example.myproject;

public class Database {
    String fname;
    String lname;
    String phoneno1;
    String address1;
    String email1;
    String password1;

    public Database(String fname, String lname, String phoneno1,String address1, String email1, String password1) {
        this.fname = fname;
        this.lname = lname;
        this.phoneno1 = phoneno1;
        this.address1 = address1;
        this.email1 = email1;
        this.password1 = password1;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getPhoneno() { return phoneno1;}

    public String getAddress() {
        return address1;
    }

    public String getEmail() {
        return email1;
    }

    public String getPassword() {
        return password1;
    }


}
