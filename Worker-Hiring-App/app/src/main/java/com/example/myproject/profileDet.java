package com.example.myproject;

public class profileDet {

    String fname, lname, phoneno, address;

    public profileDet(){}

    public profileDet(String fname, String lname, String phoneno, String address) {
        this.fname = fname;
        this.lname = lname;
        this.phoneno = phoneno;
        this.address = address;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
