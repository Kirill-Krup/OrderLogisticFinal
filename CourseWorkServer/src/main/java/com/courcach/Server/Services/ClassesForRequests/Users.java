package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;
import java.sql.Timestamp;

public class Users implements Serializable {
    private String login;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private Boolean isBlocked;
    private double wallet;
    private Timestamp lastLogin;


    public Users(){}

    public Users(String login,String firstName,String lastName,String email,double wallet,Timestamp lastLogin) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.wallet = wallet;
        this.lastLogin = lastLogin;
    }

    public Users(String login, String firstName, String lastName, String role, String email, Boolean isBlocked,double wallet,Timestamp lastLogin) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.isBlocked = isBlocked;
        this.wallet = wallet;
        this.lastLogin = lastLogin;
    }

    public String getLogin() {return login;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getRole() {return role;}
    public String getEmail() {return email;}
    public Boolean getIsBlocked() {return isBlocked;}
    public double getWallet() {return wallet;}
    public Timestamp getLastLogin() {return lastLogin;}

    public void setLogin(String login) {this.login=login;}
    public void setFirstName(String firstName) {this.firstName=firstName;}
    public void setLastName(String lastName) {this.lastName=lastName;}
    public void setRole(String role) {this.role=role;}
    public void setEmail(String email) {this.email=email;}
    public void setIsBlocked(Boolean isBlocked) {this.isBlocked=isBlocked;}
    public void setWallet(double wallet) {this.wallet=wallet;}
    public void setLastLogin(Timestamp photoPath) {this.lastLogin=photoPath;}
}