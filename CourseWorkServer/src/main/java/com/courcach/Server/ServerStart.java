package com.courcach.Server;
import com.courcach.Database.DatabaseConnection;

public class ServerStart {
    public static void main(String[] args) {
        ServerConnection connection = new ServerConnection(7777);
        if(DatabaseConnection.testConnection()){
            System.out.println("DB IS CONNECTED");
        }
        else{
            System.out.println("CONNECTION FAILED");
        }
        new Thread(connection).start();
    }
}

