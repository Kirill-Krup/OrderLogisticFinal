package com.courcach.Server.Services.User.ForgotPassword;

import java.io.Serializable;

public class ForgotPasswordRequest implements Serializable {
    private final String request;
    private  String mail;
    private int code;

    public ForgotPasswordRequest(String request, String mail) {
        this.request = request;
        this.mail = mail;
    }

    public ForgotPasswordRequest(String request, int code) {
        this.request = request;
        this.code = code;
    }

    public String getRequest() { return request; }
    public String getMail() { return mail; }
    public int getCode() { return code; }
}
