package com.courcach.Server.Services.Admin.Responce;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderResponce {
    public List<Orders> allOrders() {
        String query = """
        SELECT
            o.orderNumber,
            o.userLogin, 
            o.totalPrice, 
            o.FIO, 
            o.phone,
            topay.type AS typeOfPayment, 
            o.typeOfDelivery, 
            o.addressOfDelivery, 
            o.DateOfOrder, 
            o.status,
            od.places, 
            od.quantity
        FROM orders o
        LEFT JOIN orderdetails od ON o.orderNumber = od.orderNumber
        LEFT JOIN typeOfPayment topay ON o.typeOfPaymentID = topay.typeOfPaymentID
        ORDER BY o.DateOfOrder DESC, o.orderNumber, od.orderDetailID
        """;
        List<Orders> ordersList = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int orderNumber = rs.getInt("orderNumber");

                if (!ordersMap.containsKey(orderNumber)) {
                    Orders.OrderStatus status;
                    try {
                        status = Orders.OrderStatus.valueOf(rs.getString("status"));
                    } catch (IllegalArgumentException e) {
                        status = Orders.OrderStatus.ОТКАЗАНО;
                    }
                    Orders order = new Orders(
                            orderNumber,
                            rs.getString("userLogin"),
                            rs.getFloat("totalPrice"),
                            rs.getString("FIO"),
                            rs.getString("phone"),
                            rs.getString("typeOfPayment"),
                            rs.getString("typeOfDelivery"),
                            rs.getString("addressOfDelivery"),
                            new ArrayList<>(),
                            rs.getString("DateOfOrder"),
                            status
                    );
                    ordersMap.put(orderNumber, order);
                    ordersList.add(order);
                }
                String placesString = rs.getString("places");
                if (placesString != null && !placesString.isEmpty()) {
                    String[] placeInfo = placesString.split("\\|");
                    if (placeInfo.length >= 4) {
                        String priceString = placeInfo[3].replace(",", ".");
                        Places place = new Places(
                                placeInfo[0],
                                placeInfo[1],
                                Float.parseFloat(priceString),
                                placeInfo[2],
                                rs.getInt("quantity")
                        );
                        ordersMap.get(orderNumber).getOrderPlaces().add(place);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка заказов:");
            e.printStackTrace();
        }

        return ordersList;
    }

    public void deleteOrder(int orderNumber) {
        String deleteDetailsQuery = "DELETE FROM orderdetails WHERE orderNumber = ?";
        String deleteOrderQuery = "DELETE FROM orders WHERE orderNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement deleteDetailsStmt = conn.prepareStatement(deleteDetailsQuery)) {
                    deleteDetailsStmt.setInt(1, orderNumber);
                    int detailsDeleted = deleteDetailsStmt.executeUpdate();
                    System.out.println("Удалено " + detailsDeleted + " записей из orderdetails");
                }
                try (PreparedStatement deleteOrderStmt = conn.prepareStatement(deleteOrderQuery)) {
                    deleteOrderStmt.setInt(1, orderNumber);
                    int ordersDeleted = deleteOrderStmt.executeUpdate();

                    if (ordersDeleted == 0) {
                        System.out.println("Заказ с номером " + orderNumber + " не найден");
                    } else {
                        System.out.println("Заказ №" + orderNumber + " успешно удален");
                    }
                }
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка при удалении заказа №" + orderNumber + ":");
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных:");
            e.printStackTrace();
        }
    }


    public List<Orders> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        String query = """
        SELECT 
            o.orderNumber,
            o.userLogin,
            o.totalPrice,
            o.FIO,
            o.phone,
            o.typeOfDelivery,
            o.addressOfDelivery,
            o.DateOfOrder,
            o.status,
            od.places,
            od.quantity,
            p.type AS paymentType
        FROM 
            orders o
        LEFT JOIN 
            orderdetails od ON o.orderNumber = od.orderNumber
        LEFT JOIN 
            typeOfPayment p ON o.typeOfPaymentID = p.typeOfPaymentID
        WHERE 
            o.DateOfOrder BETWEEN ? AND ?
        ORDER BY 
            o.DateOfOrder DESC
        """;

        List<Orders> orders = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderNumber = rs.getInt("orderNumber");

                if (!ordersMap.containsKey(orderNumber)) {
                    Orders order = new Orders();
                    order.setOrderNumber(orderNumber);
                    order.setUserLogin(rs.getString("userLogin"));
                    order.setTotalPrice(rs.getFloat("totalPrice"));
                    order.setFIO(rs.getString("FIO"));
                    order.setPhone(rs.getString("phone"));
                    order.setTypeOfDelivery(rs.getString("typeOfDelivery"));
                    order.setAddressOfDelivery(rs.getString("addressOfDelivery"));
                    order.setDate(rs.getString("DateOfOrder"));
                    String status = rs.getString("status");
                    order.setOrderStatus(Orders.OrderStatus.valueOf(status));

                    order.setTypeOfPayment(rs.getString("paymentType"));
                    order.setOrderPlaces(new ArrayList<>());

                    ordersMap.put(orderNumber, order);
                    orders.add(order);
                }

                String placesString = rs.getString("places");
                if (placesString != null && !placesString.isEmpty()) {
                    String[] placeInfo = placesString.split("\\|");
                    if (placeInfo.length >= 4) {
                        Places place = new Places();
                        place.setPlaceName(placeInfo[0]);
                        place.setDescription(placeInfo[1]);
                        place.setCategory(placeInfo[2]);

                        String priceString = placeInfo[3].replace(",", ".");
                        place.setPrice(Float.parseFloat(priceString));

                        place.setQuantity(rs.getInt("quantity"));

                        ordersMap.get(orderNumber).getOrderPlaces().add(place);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении заказов по датам:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ошибка при обработке данных заказа:");
            e.printStackTrace();
        }

        return orders;
    }
}
