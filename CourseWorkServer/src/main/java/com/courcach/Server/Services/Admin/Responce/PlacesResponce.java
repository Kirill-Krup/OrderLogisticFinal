package com.courcach.Server.Services.Admin.Responce;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Places;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

}
