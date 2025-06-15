package com.courcach.Server.Services.User.ForgotPassword;

import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.Serializable;

public class ForgotPasswordResponce implements Serializable {
    private final String message;
    private final boolean responce;



    public ForgotPasswordResponce(String message,boolean responce) {
        this.message = message;
        this.responce = responce;
    }

    public String getMessage() { return message; }
    public boolean getResponce() { return responce; }
}
