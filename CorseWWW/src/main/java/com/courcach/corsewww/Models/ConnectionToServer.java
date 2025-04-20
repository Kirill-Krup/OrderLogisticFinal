package com.courcach.corsewww.Models;

import com.courcach.Server.Services.RegRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToServer {
    private Socket userSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final int PORT = 7777;
    private final String HOST = "localhost";


    public ConnectionToServer() {
        try {
            userSocket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(userSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(userSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendObject(Object obj){
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Object receiveObject() {
        try {
            return in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}