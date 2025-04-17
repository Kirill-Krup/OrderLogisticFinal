package com.courcach.Server.Handlers;



import com.courcach.Server.Services.AuthResponse;
import com.courcach.Server.Services.AuthService;
import com.courcach.Server.Services.RegResponse;

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
            String operationType = (String) in.readObject();
            switch (operationType) {
                case "LOGIN": {
                    String login = (String) in.readObject();
                    String password = (String) in.readObject();
                    AuthService authService = new AuthService();
                    AuthResponse response = authService.authenticate(login, password);
                    String role = response.getRole();
                    out.writeObject(role);
                    out.flush();

                    if (role != null) {
                        switch (role) {
                            case "admin" -> new AdminHandler(userSocket, in, out).handle();
                            case "employee" -> new EmployeeHandler(userSocket, in, out).handle();
                            case "client" -> new ClientHandler(userSocket, in, out).handle();
                        }
                    }
                    else {
                        out.writeObject("Не правильно введён логин или пароль:" + response.getMessage());
                        out.flush();
                    }
                    break;
                }

                case "REGISTER":{
                    String email = (String) in.readObject();
                    String name =  (String) in.readObject();
                    String surname = (String) in.readObject();
                    String login = (String) in.readObject();
                    String password = (String) in.readObject();
                    System.out.println("Получены данные от клиента на регистрацию:");
                    System.out.println("Почта: " +email);
                    System.out.println("Имя: " +name);
                    System.out.println("Фамилия: " +surname);
                    System.out.println("Логин: " +login);
                    System.out.println("Пароль: " +password);
                    AuthService authService = new AuthService();
                    RegResponse response = authService.register(email, name, surname, login, password);
                    Boolean isSuccess = response.isSuccess();
                    out.writeObject(isSuccess);
                    out.flush();
                    if(isSuccess) {
                        new ClientHandler(userSocket, in, out).handle();
                    }else {
                        System.out.println(response.getMessage());
                    }
                    break;
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