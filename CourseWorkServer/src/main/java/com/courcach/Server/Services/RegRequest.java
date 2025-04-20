package com.courcach.Server.Services;

public class RegRequest implements java.io.Serializable{
    private final String email;
    private final String name;
    private final String surname;
    private final String login;
    private final String password;
    private final String type;

    public RegRequest(String email, String name, String surname, String login, String password,String type) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = password;
        this.type = type;
    }

    public String getEmail() {return email;}
    public String getName() {return name;}
    public String getSurname() {return surname;}
    public String getLogin() {return login;}
    public String getPassword() {return password;}
    public String getType() {return type;}
}
