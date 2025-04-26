package com.courcach.Server.Services.Client.ClientResponse;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientOrderResponse {
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

    public void updateWallet(String username, double newWallet) {
        String query = "UPDATE users SET wallet = ? WHERE login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDouble(1, newWallet);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void newOrder(Orders order) {
        String orderQuery = "INSERT INTO orders (userLogin, totalPrice, FIO, DateOfOrder, " +
                "typeOfPaymentID, typeOfDelivery, addressOfDelivery) " +
                "VALUES (?, ?, ?, NOW(), ?, ?, ?)";

        String detailQuery = "INSERT INTO orderdetails (orderNumber, places, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        String updatePlacesQuery = "UPDATE places SET quantity = quantity - ? WHERE placeID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement detailStmt = conn.prepareStatement(detailQuery);
             PreparedStatement updatePlacesStmt = conn.prepareStatement(updatePlacesQuery)) {

            conn.setAutoCommit(false);

            orderStmt.setString(1, order.getUserLogin());
            orderStmt.setBigDecimal(2, BigDecimal.valueOf(order.getTotalPrice()));
            orderStmt.setString(3, order.getFIO());

            int paymentTypeId = "Онлайн".equals(order.getTypeOfPayment()) ? 1 : 2;
            orderStmt.setInt(4, paymentTypeId);

            orderStmt.setString(5, order.getTypeOfDelivery());
            orderStmt.setString(6, order.getAddressOfDelivery());

            int affectedRows = orderStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Не удалось создать заказ, нет измененных строк.");
            }

            int orderNumber;
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderNumber = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Не удалось получить ID созданного заказа.");
                }
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

                int placeId = getPlaceIdByName(conn, place.getPlaceName());

                updatePlacesStmt.setInt(1, place.getQuantity());
                updatePlacesStmt.setInt(2, placeId);
                updatePlacesStmt.addBatch();
            }
            detailStmt.executeBatch();
            updatePlacesStmt.executeBatch();
            conn.commit();
            System.out.println("Заказ успешно создан с номером: " + orderNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании заказа", e);
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
