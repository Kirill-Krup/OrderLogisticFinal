package com.courcach.Server.Handlers;



import com.courcach.Server.Services.User.Auth.*;
import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordRequest;
import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordResponce;
import com.courcach.Server.Services.User.ForgotPassword.ForgotPasswordService;
import com.courcach.Server.Utils.MailUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserHandler implements Runnable {
    private final Socket userSocket;
    private final AuthService authService;
    private String code;

    public UserHandler(Socket clientSocket) {
        this.userSocket = clientSocket;
        this.authService = new AuthService();
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(userSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(userSocket.getInputStream())) {

            out.flush();

            while (true) {
                Object request = in.readObject();
                System.out.println("Received request: " + request);
                if (request instanceof AuthRequest authRequest) {
                    AuthResponse authResponse = authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
                    out.writeObject(authResponse);
                    if (authResponse.isSuccess()) {
                        RoleHandler handler = createRoleHandler(authResponse.getRole(), userSocket, in, out);
                        if (handler != null) {
                            handler.handle();
                        }
                    }
                }
                if (request instanceof RegRequest regRequest) {
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
                if(request instanceof ForgotPasswordRequest forgotPasswordRequest) {
                    ForgotPasswordService forgotPasswordService = new ForgotPasswordService();
                    switch(forgotPasswordRequest.getRequest()){
                        case "forgotPassword"->{
                            ForgotPasswordResponce passwordResponce = forgotPasswordService.checkMail(forgotPasswordRequest.getMail());
                            if(passwordResponce.getResponce()){
                                MailUtil mailUtil = new MailUtil();
                                code = mailUtil.sendVerificationCode(forgotPasswordRequest.getMail());
                                out.writeObject(passwordResponce);
                                out.flush();
                            }else{
                                out.writeObject(passwordResponce);
                                out.flush();
                            }
                        }

                        case "checkCodeFromEmail"->{
                            if(code.equals(String.valueOf(forgotPasswordRequest.getCode()))){
                                out.writeObject(new ForgotPasswordResponce("ОТЛИЧНО, КОД ВЕРНЫЙ",true));
                                out.flush();
                            }else{
                                out.writeObject(new ForgotPasswordResponce("АЙ АЙ АЙ НИНАДА ЧУЖИЕ АККАУНТЫ ВЗЛАМЫВАТЬ",false));
                                out.flush();
                            }
                        }
                    }

                }
            }


        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                userSocket.close();
                System.out.println("Connection closed");
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }


    private RoleHandler createRoleHandler(String role, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        System.out.println("Creating handler for role: " + role);
        return switch (role.toLowerCase()) {
            case "admin" -> new AdminHandler(socket, in, out);
            case "client" -> new ClientHandler(socket, in, out);
            case "employee" -> new EmployeeHandler(socket, in, out);
            default -> null;
        };
    }
}



