package com.courcach.Server.Services.Admin.Responce;
import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AllUsersResponce {
    public List<Users> takeAllUsers(){
        String query = """
        SELECT u.login, u.firstName, u.lastName, u.email,u.isBlocked, r.roleName 
        FROM users u
        JOIN roles r ON u.roleID = r.roleID
        """;
        List<Users> users = new ArrayList<>();
        try (Connection connect = DatabaseConnection.getConnection();
             PreparedStatement stmt = connect.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Users user = new Users();
                user.setLogin(rs.getString("login"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("roleName"));
                user.setIsBlocked(rs.getBoolean("isBlocked"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void blockUser(String login){
        String query = "UPDATE users SET isBlocked = 1 WHERE login = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);        }
    }

    public void unlockUser(String login){
        String query = "UPDATE users SET isBlocked = 0 WHERE login = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);        }
    }

    public void delUser(String login){
        String query = "DELETE FROM users WHERE login = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);        }
    }

    public void giveEmployeeStatus(String login){
        String query = "UPDATE users SET roleID = 3 WHERE login = ?";
        try(Connection connection = DatabaseConnection.getConnection(); PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);}
    }
}
