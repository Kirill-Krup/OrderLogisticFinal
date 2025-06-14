package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;
import java.sql.Timestamp;

public class Log implements Serializable {
    private String login;
    private String log;
    private Timestamp date;

    public Log(String login, String log) {
        this.login = login;
        this.log = log;
    }

    public Log(String login, String log, Timestamp date) {
        this.login = login;
        this.log = log;
        this.date = date;
    }

    public String getLogin() {return login;}
    public String getLog() {return log;}
    public Timestamp getDate() {return date;}
    public void setLogin(String login) {this.login = login;}
    public void setLog(String log) {this.log = log;}
    public void setDate(Timestamp date) {this.date = date;}

}
