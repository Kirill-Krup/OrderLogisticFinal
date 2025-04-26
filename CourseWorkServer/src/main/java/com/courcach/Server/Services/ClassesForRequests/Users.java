package com.courcach.Server.Services.ClassesForRequests;

import java.io.Serializable;

public class Users implements Serializable {
    private String login;
    private String firstName;
    private String lastName;
    private String role;
    private String email;
    private Boolean isBlocked;
    private double wallet;
    private String photoPath;

    public Users(){}

    public Users(String login,String firstName,String lastName,String email,double wallet,String photoPath) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.wallet = wallet;
        this.photoPath = photoPath;
    }

    public Users(String login, String firstName, String lastName, String role, String email, Boolean isBlocked,double wallet,String photoPath) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.isBlocked = isBlocked;
    }

    public String getLogin() {return login;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getRole() {return role;}
    public String getEmail() {return email;}
    public Boolean getIsBlocked() {return isBlocked;}
    public double getWallet() {return wallet;}
    public String getPhotoPath() {return photoPath;}

    public void setLogin(String login) {this.login=login;}
    public void setFirstName(String firstName) {this.firstName=firstName;}
    public void setLastName(String lastName) {this.lastName=lastName;}
    public void setRole(String role) {this.role=role;}
    public void setEmail(String email) {this.email=email;}
    public void setIsBlocked(Boolean isBlocked) {this.isBlocked=isBlocked;}
    public void setWallet(double wallet) {this.wallet=wallet;}
    public void setPhotoPath(String photoPath) {this.photoPath=photoPath;}
}