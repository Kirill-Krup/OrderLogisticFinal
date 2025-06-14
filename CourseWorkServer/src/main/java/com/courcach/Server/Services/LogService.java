package com.courcach.Server.Services;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Log> takeAllLogs() {
        List <Log> allLogs = new ArrayList<Log>();
        String query = "SELECT * FROM logs";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String login = rs.getString("Login");
                String log = rs.getString("Log");
                Timestamp date = rs.getTimestamp("LogDate");
                Log logObj = new Log(login,log,date);
                allLogs.add(logObj);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allLogs;
    }
}
