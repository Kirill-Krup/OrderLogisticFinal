package com.courcach.Server.Handlers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class RoleHandler {
    protected Socket userSocket;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;

    public RoleHandler(Socket userSocket,ObjectInputStream in,ObjectOutputStream out) {
        this.userSocket = userSocket;
        this.in = in;
        this.out = out;
    }

    public abstract void handle() throws IOException, ClassNotFoundException;
}
