package com.courcach.Server.Handlers;

import com.courcach.Server.Services.ClassesForRequests.Log;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Client.ClientRequest;
import com.courcach.Server.Services.Client.ClientResponse.ClientOrderResponse;
import com.courcach.Server.Services.LogService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends RoleHandler {
    public ClientHandler(Socket userSocket, ObjectInputStream in, ObjectOutputStream out) {
        super(userSocket, in, out);
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        while (!exit) {
            ClientRequest request = (ClientRequest) in.readObject();
            if(request instanceof ClientRequest) {
                ClientOrderResponse orderResponse = new ClientOrderResponse();
                LogService logService = new LogService();
                switch (request.getRequest()){
                    case "giveMeAllMaterials"->{
                        List<Places> allPlaces =orderResponse.getPlaces();
                        out.writeObject(allPlaces);
                        out.flush();
                    }
                    case "addNewOrder"->{
                        orderResponse.newOrder(request.getOrder());
                        logService.addLog(new Log(request.getClientLogin(), "Новый заказ №" + request.getOrder().getOrderNumber() + " на сумму " + request.getOrder().getTotalPrice()));
                    }

                    case "replenishmentWallet"->{
                        double newWalletValue = request.getSum()+request.getUser().getWallet();
                        orderResponse.updateWallet(request.getUser().getLogin(),newWalletValue);
                        logService.addLog(new Log(request.getUser().getLogin(), "Пользователь пополнил кошелёк на " + request.getSum() + ". На балансе " + newWalletValue));
                    }

                    case "giveMeMyActiveOrders"->{
                        List<Orders> activeOrders = orderResponse.getActiveUserOrders(request.getUser().getLogin());
                        out.writeObject(activeOrders);
                        out.flush();
                    }

                    case "giveMeAllMyOrders"->{
                        List<Orders> allOrdersOfUser = orderResponse.historyOfUsersOrders(request.getUser().getLogin());
                        out.writeObject(allOrdersOfUser);
                        out.flush();
                    }

                    case "cancelOrder"->{
                        orderResponse.cancelOrder(request.getOrderNumber());
                        logService.addLog(new Log(request.getUser().getLogin(), "Пользователь отменил заказ №" + request.getOrderNumber()));
                        List<Orders> activeOrders = orderResponse.getActiveUserOrders(request.getUser().getLogin());
                        out.writeObject(activeOrders);
                        out.flush();
                    }

                    case "giveMeEndedOrdersAndReports"->{
                        List<Orders>allEndedOrders = orderResponse.getEndedOrders(request.getUser().getLogin());
                        List<ReportModel> allSuitReports = orderResponse.getSuitReports(request.getUser().getLogin());
                        out.writeObject(allEndedOrders);
                        out.writeObject(allSuitReports);
                        out.flush();
                    }

                    case "newReport"->{
                        ClientOrderResponse req= orderResponse.newReport(request.getReport());
                        if(req.getMessage().contains("успешно")){
                            logService.addLog(new Log(request.getReport().getUserLogin(), "Пользователь оставил отзыв: " + request.getReport().getReportMessange()));
                        }
                        out.writeObject(req.getMessage());
                        out.flush();
                    }

                    case "giveMeNewMessages"->{
                        boolean req = orderResponse.checkMessages(request.getUser().getLogin());
                        out.writeObject(req);
                        out.flush();
                    }

                    case "onlineBuy"->{
                        orderResponse.updateWallet(request.getUser().getLogin(),request.getWallet());
                        logService.addLog(new Log(request.getUser().getLogin(), "Пользователь оформил онлайн заказ, сняты деньги. Новое значение кошелька: " + request.getWallet()));
                    }

                    case "checkAnswers" ->{
                        orderResponse.checkAllUsersAnswers(request.getUser().getLogin());
                        List<ReportModel> allSuitReports = orderResponse.getSuitReports(request.getUser().getLogin());
                        out.writeObject(allSuitReports);
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
