package com.courcach.Server.Services;

public class AuthRequest implements java.io.Serializable {
    private final String username;
    private final String password;
    private final String message;

    public AuthRequest(String username, String password,String message) {
        this.username = username;
        this.password = password;
        this.message = message;
    }

    public AuthRequest(String message) {
        this.username = null;
        this.password = null;
        this.message = message;
    }

    // Геттеры
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getMessage() { return message; }
}