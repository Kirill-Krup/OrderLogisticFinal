package com.courcach.Server.Services.User.Auth;

public class RegResponse implements java.io.Serializable {
    private final boolean success;
    private final String role;
    private final String message;

    public RegResponse(boolean success,  String message) {
        this.success = success;
        this.role = "client";
        this.message = message;
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}
