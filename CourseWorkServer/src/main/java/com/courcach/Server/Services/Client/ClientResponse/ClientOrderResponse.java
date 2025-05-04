package com.courcach.Server.Services.Client.ClientResponse;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;
import com.courcach.Server.Services.Client.ClientRequest;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientOrderResponse {
    private String message;

    public ClientOrderResponse(){}

    public ClientOrderResponse(String message) {
        this.message = message;
    }

    public String getMessage() {return message;}

    public List<Places> getPlaces() {
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

    public void newOrder(Orders order) {
        String orderQuery = "INSERT INTO orders (userLogin, totalPrice, FIO, phone, DateOfOrder, typeOfPaymentID, typeOfDelivery, addressOfDelivery) VALUES (?, ?, ?, ?, NOW(), ?, ?, ?)";
        String detailQuery = "INSERT INTO orderdetails (orderNumber, places, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement detailStmt = conn.prepareStatement(detailQuery)) {

                orderStmt.setString(1, order.getUserLogin());
                orderStmt.setBigDecimal(2, BigDecimal.valueOf(order.getTotalPrice()));
                orderStmt.setString(3, order.getFIO());
                orderStmt.setString(4, order.getPhone());
                int paymentTypeId = "Онлайн".equals(order.getTypeOfPayment()) ? 1 : 2;
                orderStmt.setInt(5, paymentTypeId);
                orderStmt.setString(6, order.getTypeOfDelivery());
                orderStmt.setString(7, order.getAddressOfDelivery());

                int affectedRows = orderStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Не удалось создать заказ");
                }

                int orderNumber;
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException("Не удалось получить ID заказа");
                    }
                    orderNumber = generatedKeys.getInt(1);
                }

                for (Places place : order.getOrderPlaces()) {
                    String placeInfo = String.format("%s|%s|%s|%.2f",
                            place.getPlaceName(),
                            place.getDescription(),
                            place.getCategory(),
                            place.getPrice());

                    detailStmt.setInt(1, orderNumber);
                    detailStmt.setString(2, placeInfo);
                    detailStmt.setInt(3, place.getQuantity());
                    detailStmt.setBigDecimal(4, BigDecimal.valueOf(place.getPrice()));
                    detailStmt.addBatch();
                }

                detailStmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Ошибка транзакции", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }

    public List<Orders> getActiveUserOrders(String userLogin) {
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
                WHERE o.userLogin = ? AND o.status != 'ОТМЕНЁН' AND o.status != 'ОТКАЗАНО'
                ORDER BY o.orderNumber, od.orderDetailID
                """;

        List<Orders> ordersList = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin);
            ResultSet rs = stmt.executeQuery();

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
            System.err.println("Ошибка при получении активных заказов пользователя:");
            e.printStackTrace();
        }

        return ordersList;
    }

    public void cancelOrder(int orderNumber) {
        String query = "UPDATE orders SET status = 'ОТМЕНЁН' WHERE orderNumber = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, orderNumber);
            stmt.executeUpdate();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Orders> historyOfUsersOrders(String userLogin) {
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
        WHERE o.userLogin = ?
        ORDER BY o.DateOfOrder DESC, o.orderNumber, od.orderDetailID
        """;

        List<Orders> ordersList = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin);
            ResultSet rs = stmt.executeQuery();

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
            System.err.println("Ошибка при получении истории заказов пользователя:");
            e.printStackTrace();
        }

        return ordersList;
    }

    public List<Orders> getEndedOrders(String userLogin) {
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
                WHERE o.userLogin = ? AND o.status = 'ВЫПОЛНЕН'
                ORDER BY o.orderNumber, od.orderDetailID
                """;

        List<Orders> ordersList = new ArrayList<>();
        Map<Integer, Orders> ordersMap = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin);
            ResultSet rs = stmt.executeQuery();

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
            System.err.println("Ошибка при получении активных заказов пользователя:");
            e.printStackTrace();
        }

        return ordersList;
    }



    public ClientOrderResponse newReport(ReportModel report) {
        String checkSql = "SELECT COUNT(*) FROM reports WHERE userLogin = ? AND orderNumber = ?";
        String insertSql = "INSERT INTO reports (dateOfReport, reportMessage, userLogin, orderNumber, stars) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, report.getUserLogin());
            checkStmt.setInt(2, report.getOrderNumber());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return new ClientOrderResponse("Отзыв на этот товар уже существует");
            }
            insertStmt.setTimestamp(1, report.getTimeOfReport());
            insertStmt.setString(2, report.getReportMessange());
            insertStmt.setString(3, report.getUserLogin());
            insertStmt.setInt(4, report.getOrderNumber());
            insertStmt.setInt(5,report.getStars());

            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                return new ClientOrderResponse("Отзыв успешно добавлен");
            } else {
                return new ClientOrderResponse("Не удалось добавить отзыв");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных: " + e.getMessage());
            e.printStackTrace();
        }
        return new ClientOrderResponse("Ошибка");
    }

    public List<ReportModel> getSuitReports(String userLogin) {
        String query = "SELECT * FROM reports WHERE userLogin = ?";
        List<ReportModel> reportsList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Timestamp timeOfReport = rs.getTimestamp("dateOfReport");
                String reportMessange = rs.getString("reportMessage");
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

    public boolean checkMessages(String userLogin) {
        String query = "SELECT CASE WHEN EXISTS ("
                + "SELECT 1 FROM reports "
                + "WHERE userLogin = ? "
                + "AND reportAnswer IS NOT NULL "
                + "AND isChecked = false"
                + ") THEN 1 ELSE 0 END AS hasNewMessage";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("hasNewMessage") == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateWallet(String userLogin, Double newWallet) {
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

    public void checkAllUsersAnswers(String userLogin) {
        if (userLogin == null || userLogin.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин пользователя не может быть пустым");
        }

        String query = "UPDATE reports SET isChecked = 1 WHERE userLogin = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userLogin.trim());
            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении статуса проверки для пользователя " + userLogin, e);
        }
    }

    private int getPlaceIdByName(Connection conn, String placeName) throws SQLException {
        String query = "SELECT placeID, quantity FROM places WHERE placeName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, placeName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("placeID");
                }
            }
        }
        throw new SQLException("Товар с названием '" + placeName + "' не найден");
    }

}
