package com.courcach.Server;

import com.courcach.Server.Handlers.UserHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private final int PORT;
    private ServerSocket serverSocket;
    private boolean isStopped = false;

    public ServerConnection(int port) {
        this.PORT = port;
    }


    @Override
    public void run() {
        openServerSocket();
        while (!isStopped) {
            Socket userSocket = null;
            try {
                userSocket = serverSocket.accept();
                System.out.println("Client connected...");
                new Thread(new UserHandler(userSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
                if (isStopped()) {
                    System.out.println("Сервер остановлен");
                    return;
                }
            }
        }
    }

    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    public synchronized void setStopped(boolean stopped) {
        isStopped = stopped;
    }
}
