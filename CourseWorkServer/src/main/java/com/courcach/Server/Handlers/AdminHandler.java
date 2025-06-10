package com.courcach.Server.Handlers;

import com.courcach.Server.Services.Admin.AdminRequest;
import com.courcach.Server.Services.Admin.Responce.AllUsersResponce;
import com.courcach.Server.Services.Admin.Responce.OrderResponce;
import com.courcach.Server.Services.Admin.Responce.PlacesResponce;
import com.courcach.Server.Services.ClassesForRequests.*;
import com.courcach.Server.Services.LogService;

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
        while (!exit) {
            AdminRequest request = (AdminRequest) in.readObject();
            if(request instanceof AdminRequest) {
                AllUsersResponce service = new AllUsersResponce();
                PlacesResponce placesService = new PlacesResponce();
                OrderResponce orderService = new OrderResponce();
                LogService logService = new LogService();
                switch (request.getRequest()) {
                    // USERS
                    case "takeAllUsers"->{
                        List<Users> users = service.takeAllUsers();
                        out.writeObject(users);
                        out.flush();
                    }
                    case "blockUser"->{
                        service.blockUser(request.getUser().getLogin());
                        logService.addLog(new Log(request.getAdminLogin(), "Блокировка пользователя " + request.getUser().getLogin()));
                    }
                    case "unlockUser"->{
                        service.unlockUser(request.getUser().getLogin());
                        logService.addLog(new Log(request.getAdminLogin(), "Разблокировка пользователя " + request.getUser().getLogin()));
                    }
                    case "delUser"->{
                        service.delUser(request.getUser().getLogin());
                    }
                    case "giveEmployeeStatus"->{
                        service.giveEmployeeStatus(request.getUser().getLogin());
                    }

                    // WORK WITH PLACES
                    case "takeAllPlaces"->{
                        List<Places>allPlaces = placesService.getAllPlaces();
                        out.writeObject(allPlaces);
                        out.flush();
                    }

                    case "delPlace"->{
                        placesService.delUser(request.getPlace().getPlaceName());
                    }

                    case "giveMeAllCategories"->{
                        List<String> allCategories = placesService.getAllCategories();
                        out.writeObject(allCategories);
                        out.flush();
                    }

                    case "addPlace"->{
                        placesService.addPlace(request.getPlace());
                    }

                    case "addCategory"->{
                        placesService.addNewCategory(request.getCategory());
                    }

                    case "editPlace"->{
                        System.out.println(request.getSelectedPlace().getPlaceName()+" " + request.getPlace().getPlaceName());
                        String req = placesService.editPlace(request.getPlace(), request.getSelectedPlace());
                        out.writeObject(req);
                        out.flush();
                    }

                    case "giveMeAllOrders"->{
                        List<Orders> allHistory= orderService.allOrders();
                        out.writeObject(allHistory);
                        out.flush();
                    }

                    case "deleteOrder"->{
                        orderService.deleteOrder(request.getOrder().getOrderNumber());
                    }

                    case "giveMeOrdersInDates"->{
                        List<Orders> inDates = orderService.getOrdersByDateRange(request.getFirstDate(), request.getLastDate());
                        out.writeObject(inDates);
                        out.flush();
                    }

                    case "giveMeAllReports"->{
                        List<ReportModel> allReports = placesService.getAllReports();
                        out.writeObject(allReports);
                        out.flush();
                    }

                    case "exit"->{
                        exit = true;
                    }
                }
            }
        }
    }
}
