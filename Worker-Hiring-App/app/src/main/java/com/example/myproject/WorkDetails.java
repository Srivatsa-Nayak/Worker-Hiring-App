package com.example.myproject;

import android.widget.EditText;

public class WorkDetails {
    String wname;
    String wemail;
    String wdesc;
    String wreq;
    String wphone;
    String wsalary;
    String wstartdate;
    String wloc;

    public WorkDetails(String wname, String wemail, String wdesc, String wreq, String wphone, String wsalary, String wstartdate, String wloc) {
        this.wname = wname;
        this.wemail = wemail;
        this.wdesc = wdesc;
        this.wreq = wreq;
        this.wphone = wphone;
        this.wsalary = wsalary;
        this.wstartdate = wstartdate;
        this.wloc = wloc;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public String getWemail() {
        return wemail;
    }

    public void setWemail(String wemail) {
        this.wemail = wemail;
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

    public String getWphone() {
        return wphone;
    }

    public void setWphone(String wphone) {
        this.wphone = wphone;
    }

    public String getWsalary() {
        return wsalary;
    }

    public void setWsalary(String wsalary) {
        this.wsalary = wsalary;
    }

    public String getWstartdate() {
        return wstartdate;
    }

    public void setWstartdate(String wstartdate) {
        this.wstartdate = wstartdate;
    }

    public String getWloc() {
        return wloc;
    }

    public void setWloc(String wloc) {
        this.wloc = wloc;
    }
}
