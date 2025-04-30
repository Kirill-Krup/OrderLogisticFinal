package com.courcach.Server.Handlers;

import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Employee.EmployeeRequest;
import com.courcach.Server.Services.Employee.EmployeeResponse.EmployeeResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class EmployeeHandler extends RoleHandler{
    public EmployeeHandler(Socket userSocket, ObjectInputStream in, ObjectOutputStream out) {
        super(userSocket, in, out);
    }

    @Override
    public void handle() throws IOException, ClassNotFoundException {
        while (!exit) {
            EmployeeRequest request = (EmployeeRequest) in.readObject();
            if (request instanceof EmployeeRequest) {
                EmployeeResponse employeeResponse = new EmployeeResponse();
                switch (request.getRequest()) {
                    case "giveMeNew" -> {
                        int counter = employeeResponse.takeCounterOfNewOrders();
                        int counterRep = employeeResponse.takeCounterOfReports();
                        out.writeObject(counter);
                        out.writeObject(counterRep);
                        out.flush();
                    }
                    case "giveMeAllOrdersAndPlaces"->{
                        List<Orders> allOrders = employeeResponse.getAllOrders();
                        List<Places> allPlaces= employeeResponse.getAllPlaces();
                        out.writeObject(allOrders);
                        out.flush();
                        out.writeObject(allPlaces);
                        out.flush();
                    }

                    case "acceptOrder"->{
                        employeeResponse.acceptOrder(request.getOrderNumber());
                        out.writeObject(employeeResponse.getAllPlaces());
                        out.flush();
                    }

                    case "refusalOrder"->{
                        employeeResponse.refuseOrder(request.getOrderNumber());
                    }

                    case "giveMeAllReports"->{
                        List<ReportModel> allReports = employeeResponse.getAllReports();
                        out.writeObject(allReports);
                        out.flush();
                    }

                    case "answerForUser"->{
                        EmployeeResponse req = employeeResponse.updateAnswer(request.getReport().getOrderNumber(),request.getReport().getReportAnswer(),request.getReport().getReportAnswerTime());
                        out.writeObject(req.getMessage());
                        out.flush();
                    }

                    case "exit"->{exit = true;}
                }
            }
        }
    }

}
