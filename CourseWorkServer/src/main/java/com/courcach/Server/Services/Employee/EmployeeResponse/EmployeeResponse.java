package com.courcach.Server.Services.Employee.EmployeeResponse;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeResponse {
    private String message;

    public EmployeeResponse() {}

    public EmployeeResponse(String message) {
        this.message = message;
    }

    public String getMessage() {return message;}


    public int takeCounterOfNewOrders(){
        String query = "SELECT COUNT(*) AS counter FROM orders WHERE status = 'НОВЫЙ'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("counter");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int takeCounterOfReports(){
        String query = "SELECT COUNT(*) AS counter FROM reports WHERE reportAnswer IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("counter");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Orders> getAllOrders() {
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
        ORDER BY o.orderNumber, od.orderDetailID
        """;
        List<Orders> ordersList = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderNumber = rs.getInt("orderNumber");

                if (!ordersMap.containsKey(orderNumber)) {
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
                            Orders.OrderStatus.valueOf(rs.getString("status"))
                    );
                    order.setOrderNumber(orderNumber);
                    ordersMap.put(orderNumber, order);
                    ordersList.add(order);
                }

                // Добавляем детали заказа
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
            e.printStackTrace();
        }

        return ordersList;
    }

    public List<Places> getAllPlaces() {
        String query = """
        SELECT p.placeName, p.description, p.price,p.quantity, c.categoryName 
        FROM places p
        JOIN categories c ON p.categoryID = c.categoryID
        """;
        List<Places> places = new ArrayList<>();
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement stmt = connect.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Places place = new Places();
                place.setPlaceName(rs.getString("placeName"));
                place.setDescription(rs.getString("description"));
                place.setQuantity(rs.getInt("quantity"));
                place.setPrice(rs.getFloat("price"));
                place.setCategory(rs.getString("categoryName"));
                places.add(place);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return places;
    }


    public void acceptOrder(int orderNumber) {
        String updateOrderQuery = "UPDATE orders SET status = 'ПРИНЯТ' WHERE orderNumber = ?";

        String getOrderDetailsQuery = "SELECT places, quantity FROM orderdetails WHERE orderNumber = ?";

        String updatePlacesQuery = "UPDATE places SET quantity = quantity - ? WHERE placeName = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderQuery)) {
                    updateOrderStmt.setInt(1, orderNumber);
                    int affectedRows = updateOrderStmt.executeUpdate();

                    if (affectedRows == 0) {
                        System.out.println("Заказ с номером " + orderNumber + " не найден");
                        return;
                    }
                }
                try (PreparedStatement getDetailsStmt = conn.prepareStatement(getOrderDetailsQuery)) {
                    getDetailsStmt.setInt(1, orderNumber);
                    ResultSet rs = getDetailsStmt.executeQuery();
                    try (PreparedStatement updatePlacesStmt = conn.prepareStatement(updatePlacesQuery)) {
                        while (rs.next()) {
                            String placesString = rs.getString("places");
                            int orderedQuantity = rs.getInt("quantity");
                            String[] placeInfo = placesString.split("\\|");
                            if (placeInfo.length >= 1) {
                                String placeName = placeInfo[0];

                                updatePlacesStmt.setInt(1, orderedQuantity);
                                updatePlacesStmt.setString(2, placeName);
                                updatePlacesStmt.addBatch();
                            }
                        }
                        updatePlacesStmt.executeBatch();
                    }
                }
                conn.commit();
                System.out.println("Заказ №" + orderNumber + " принят и количество товаров обновлено");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Ошибка при принятии заказа:");
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

    public void refuseOrder(int orderNumber) {
        String getOrderQuery = "SELECT o.userLogin, o.totalPrice, p.type " +
                "FROM orders o " +
                "JOIN typeofpayment p ON o.typeOfPaymentID = p.typeOfPaymentID " +
                "WHERE o.orderNumber = ?";

        String updateOrderQuery = "UPDATE orders SET status = 'ОТКАЗАНО' WHERE orderNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement getStmt = conn.prepareStatement(getOrderQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateOrderQuery)) {

            getStmt.setInt(1, orderNumber);
            ResultSet rs = getStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Заказ с номером " + orderNumber + " не найден");
                return;
            }

            String userLogin = rs.getString("userLogin");
            String paymentType = rs.getString("type");
            double orderSum = rs.getDouble("totalPrice");

            updateStmt.setInt(1, orderNumber);
            int affectedRows = updateStmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("Не удалось обновить статус заказа №" + orderNumber);
                return;
            }

            if ("Онлайн".equalsIgnoreCase(paymentType)) {
                String getWalletQuery = "SELECT wallet FROM users WHERE login = ?";
                try (PreparedStatement walletStmt = conn.prepareStatement(getWalletQuery)) {
                    walletStmt.setString(1, userLogin);
                    ResultSet walletRs = walletStmt.executeQuery();

                    if (walletRs.next()) {
                        double currentWallet = walletRs.getDouble("wallet");
                        double newWallet = currentWallet + orderSum;
                        updateWallet(userLogin, newWallet);
                        System.out.println("Заказ №" + orderNumber + " отклонен. Средства в размере " +
                                orderSum + " возвращены на счет пользователя");
                    }
                }
            } else {
                System.out.println("Заказ №" + orderNumber + " отклонен (оплата: " + paymentType + ")");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при отклонении заказа:");
            e.printStackTrace();
        }
    }
    private void updateWallet(String userLogin, Double newWallet) {
        if (userLogin == null || userLogin.isEmpty()) {
            throw new IllegalArgumentException("Логин пользователя не может быть пустым");
        }

        if (newWallet == null || newWallet < 0) {
            throw new IllegalArgumentException("Некорректное значение баланса");
        }
        String query = "UPDATE users SET wallet = ? WHERE login = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            System.out.println(newWallet);
            stmt.setDouble(1, newWallet);
            stmt.setString(2, userLogin);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении баланса", e);
        }
    }



    public List<ReportModel> getAllReports() {
        String query = "SELECT * FROM reports";
        List<ReportModel> reportsList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Timestamp timeOfReport = rs.getTimestamp("dateOfReport");
                String reportMessange = rs.getString("reportMessage");
                String userLogin = rs.getString("userLogin");
                String reportAnswer = rs.getString("reportAnswer");
                Timestamp reportAnswerTime = rs.getTimestamp("reportAnswerTime");
                int orderNumber = rs.getInt("orderNumber");
                boolean status = rs.getBoolean("isChecked");
                int stars = rs.getInt("stars");
                Orders order = getOrderByNumber(orderNumber);

                ReportModel report = new ReportModel(
                        timeOfReport,
                        reportMessange,
                        userLogin,
                        reportAnswer,
                        reportAnswerTime,
                        orderNumber,
                        order,
                        status,
                        stars
                );

                reportsList.add(report);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении отзывов:");
            e.printStackTrace();
        }

        return reportsList;
    }


    private Orders getOrderByNumber(int orderNumber) {
        String query = """
        SELECT o.orderNumber, o.userLogin, o.totalPrice, o.FIO, o.phone, 
               topay.type AS typeOfPayment, o.typeOfDelivery, o.addressOfDelivery, 
               o.DateOfOrder, o.status,
               od.places, od.quantity
        FROM orders o
        LEFT JOIN orderdetails od ON o.orderNumber = od.orderNumber
        LEFT JOIN typeOfPayment topay ON o.typeOfPaymentID = topay.typeOfPaymentID
        WHERE o.orderNumber = ?
    """;

        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int ordNum = rs.getInt("orderNumber");
                if (!ordersMap.containsKey(ordNum)) {
                    Orders order = new Orders(
                            ordNum,
                            rs.getString("userLogin"),
                            rs.getFloat("totalPrice"),
                            rs.getString("FIO"),
                            rs.getString("phone"),
                            rs.getString("typeOfPayment"),
                            rs.getString("typeOfDelivery"),
                            rs.getString("addressOfDelivery"),
                            new ArrayList<>(),
                            rs.getString("DateOfOrder"),
                            Orders.OrderStatus.valueOf(rs.getString("status"))
                    );
                    ordersMap.put(ordNum, order);
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
                        ordersMap.get(ordNum).getOrderPlaces().add(place);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersMap.get(orderNumber);
    }

    public EmployeeResponse updateAnswer(int orderNumber, String answer, Timestamp time) {
        String sql = "UPDATE reports SET reportAnswer = ?, reportAnswerTime = ? WHERE orderNumber = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, answer);
            stmt.setTimestamp(2, time);
            stmt.setInt(3, orderNumber);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return new EmployeeResponse("Ответ успешно отправлен");
            } else {
                return new EmployeeResponse("Не удалось найти отзыв с таким номером заказа.");
            }

        } catch (SQLException e) {
            return new EmployeeResponse("Ошибка при обновлении ответа: " + e.getMessage());
        }
    }
}
