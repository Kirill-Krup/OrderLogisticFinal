package com.courcach.Server.Services.Admin.Responce;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Orders;
import com.courcach.Server.Services.ClassesForRequests.Places;
import com.courcach.Server.Services.ClassesForRequests.ReportModel;

import java.sql.*;
import java.util.*;

public class PlacesResponce {
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

    public void delUser(String placeName){
        String query = "DELETE FROM places WHERE placeName = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, placeName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllCategories(){
        List<String> categories = new ArrayList<>();
        String query = "SELECT categoryName FROM categories";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("categoryName"));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error with DB",e);
        }
        return categories;
    }

    public void addPlace(Places place) {
        int categoryId = 0;
        try {
            categoryId = getCategoryIDByName(place.getCategory());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String query = "INSERT INTO places (placeName, description,price,categoryID,quantity) VALUES (?,?,?,?,?)";
        try(Connection connection = DatabaseConnection.getConnection();PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, place.getPlaceName());
            stmt.setString(2, place.getDescription());
            stmt.setFloat(3, place.getPrice());
            stmt.setInt(4, categoryId);
            stmt.setInt(5, place.getQuantity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getCategoryIDByName(String categoryName) throws SQLException{
        String query = "Select categoryID from categories where categoryName = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("categoryID");
            }else{
                throw new SQLException("Ошибка при поиске категории");
            }
        }
    }

    public void addNewCategory(String categoryName){
        String query = "INSERT INTO categories (categoryName) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, categoryName);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editPlace(Places oldPlace, Places newPlace) {
        StringBuilder query = new StringBuilder("UPDATE places SET ");
        List<Object> params = new ArrayList<>();

        if (!Objects.equals(oldPlace.getPlaceName(), newPlace.getPlaceName())) {
            query.append("placeName = ?, ");
            params.add(newPlace.getPlaceName());
        }
        if (!Objects.equals(oldPlace.getDescription(), newPlace.getDescription())) {
            query.append("description = ?, ");
            params.add(newPlace.getDescription());
        }
        if (!Objects.equals(oldPlace.getCategory(), newPlace.getCategory())) {
            query.append("category = ?, ");
            params.add(newPlace.getCategory());
        }
        if (oldPlace.getPrice() != newPlace.getPrice()) {
            query.append("price = ?, ");
            params.add(newPlace.getPrice());
        }
        if (oldPlace.getQuantity() != newPlace.getQuantity()) {
            query.append("quantity = ?, ");
            params.add(newPlace.getQuantity());
        }

        if (params.isEmpty()) {
            return "Ничего не изменилось";
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE placeName = ?");
        params.add(oldPlace.getPlaceName());
        try (Connection connection = DatabaseConnection.getConnection();PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
            return "Успешно обновлено";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Ошибка при обновлении: " + e.getMessage();
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

}
