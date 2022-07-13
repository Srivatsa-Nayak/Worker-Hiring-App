package com.example.myproject;

public class MainModel {

    String wname, wloc, wsalary, wdesc, wreq, wstartdate, wphone, wemail;

    MainModel(){

    }

    public MainModel(String wname, String wloc, String wsalary, String wdesc, String wreq, String wstartdate, String wphone, String wemail) {
        this.wname = wname;
        this.wloc = wloc;
        this.wsalary = wsalary;
        this.wdesc = wdesc;
        this.wreq = wreq;
        this.wstartdate = wstartdate;
        this.wphone = wphone;
        this.wemail = wemail;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public String getWloc() {
        return wloc;
    }

    public void setWloc(String wloc) {
        this.wloc = wloc;
    }

    public String getWsalary() {
        return wsalary;
    }

    public void setWsalary(String wsalary) {
        this.wsalary = wsalary;
    }

    public String getWdesc() {
        return wdesc;
    }

    public void setWdesc(String wdesc) {
        this.wdesc = wdesc;
    }

    public String getWreq() {
        return wreq;
    }

    public void setWreq(String wreq) {
        this.wreq = wreq;
    }

    public String getWstartdate() {
        return wstartdate;
    }

    public void setWstartdate(String wstartdate) {
        this.wstartdate = wstartdate;
    }

    public String getWphone() {
        return wphone;
    }

    public void setWphone(String wphone) {
        this.wphone = wphone;
    }

    public String getWemail() {
        return wemail;
    }

    public void setWemail(String wemail) {
        this.wemail = wemail;
    }
}
