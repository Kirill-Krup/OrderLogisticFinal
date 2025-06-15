package com.courcach.Server.Services.User.Auth;

import com.courcach.Server.Services.ClassesForRequests.Users;

public class AuthResponse implements java.io.Serializable {
    private final boolean success;
    private final String role;
    private final String message;
    private Users user;

    public AuthResponse(boolean success,  String role, String message,Users user) {
        this.success = success;
        this.role = role;
        this.message = message;
        this.user = user;
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
    public Users getUser() { return user; }
}