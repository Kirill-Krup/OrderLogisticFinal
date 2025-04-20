package com.courcach.Server.Services;

public class AuthRequest implements java.io.Serializable {
    private final String username;
    private final String password;
    private final String type;

    public AuthRequest(String username, String password,String type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    // Геттеры
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getType() { return type; }
}