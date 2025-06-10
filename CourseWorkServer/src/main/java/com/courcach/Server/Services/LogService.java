package com.courcach.Server.Services;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogService {
    public void addLog(Log log) {
        String query = "INSERT INTO logs (Login, Log) VALUES (?,?)";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1,log.getLogin());
            stmt.setString(2,log.getLog());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
