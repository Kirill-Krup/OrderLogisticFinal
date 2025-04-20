package com.courcach.Server.Handlers;



import com.courcach.Server.Services.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserHandler implements Runnable {
    private final Socket userSocket;

    public UserHandler(Socket clientSocket) {
        this.userSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(userSocket.getInputStream())) {
            out.flush();
            Object autRequest = in.readObject();
            if (autRequest instanceof AuthRequest authRequest) {
                AuthService authService = new AuthService();
                AuthResponse response = authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
                out.writeObject(response);
                out.flush();

                if (response.getRole() != null) {
                    switch (response.getRole() ) {
                        case "admin" -> new AdminHandler(userSocket, in, out).handle();
                        case "employee" -> new EmployeeHandler(userSocket, in, out).handle();
                        case "client" -> new ClientHandler(userSocket, in, out).handle();
                    }
                }
                else {
                    out.writeObject("Не правильно введён логин или пароль:" + response.getMessage());
                    out.flush();
                }
            }
            if (autRequest instanceof RegRequest regRequest) {
                AuthService authService = new AuthService();
                RegResponse response = authService.register(regRequest.getEmail(),regRequest.getName(), regRequest.getSurname(), regRequest.getLogin(),regRequest.getPassword());
                out.writeObject(response);
                out.flush();
                if(response.isSuccess()) {
                    new ClientHandler(userSocket, in, out).handle();
                }else {
                    System.out.println(response.getMessage());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка обработки подключения: " + e.getMessage());
        } finally {
            try {
                userSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка закрытия сокета: " + e.getMessage());
            }
        }
    }
}