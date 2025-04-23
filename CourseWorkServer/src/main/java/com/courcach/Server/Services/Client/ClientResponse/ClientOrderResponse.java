package com.courcach.Server.Services.Client.ClientResponse;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Places;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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



}
