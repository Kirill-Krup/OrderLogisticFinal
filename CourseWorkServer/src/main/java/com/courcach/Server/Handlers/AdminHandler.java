package com.courcach.Server.Handlers;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.Admin.Responce.AllUsersResponce;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class AdminHandler extends RoleHandler {
    public AdminHandler(Socket userSocket, ObjectInputStream in, ObjectOutputStream out) {
        super(userSocket, in, out);
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        while (true) {
            AdminRequest request = (AdminRequest) in.readObject();
            if(request instanceof AdminRequest) {
                AllUsersResponce service = new AllUsersResponce();
                switch (request.getRequest()) {
                    case "takeAllUsers"->{
                        List<Users> users = service.takeAllUsers();
                        for (Users user : users) {
                            System.out.println(user.getIsBlocked());
                        }
                        out.writeObject(users);
                        out.flush();
                    }
                    case "blockUser"->{
                        service.blockUser(request.getUser().getLogin());
                    }
                    case "unlockUser"->{
                        service.unlockUser(request.getUser().getLogin());
                    }
                    case "delUser"->{
                        service.delUser(request.getUser().getLogin());
                    }
                    case "giveEmployeeStatus"->{
                        service.giveEmployeeStatus(request.getUser().getLogin());
                    }
                }
            }
        }
    }
}
