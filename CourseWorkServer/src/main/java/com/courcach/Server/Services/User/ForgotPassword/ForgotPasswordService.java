package com.courcach.Server.Services.User.ForgotPassword;

import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPasswordService {
    public ForgotPasswordResponce checkMail(String mail) {
        String query = "SELECT * FROM users WHERE email = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mail);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    return new ForgotPasswordResponce("Пользователь найден",true);
                }
            }
        } catch (SQLException e) {
            return new ForgotPasswordResponce("Такого пользователя не существует",false);
        }
        return new ForgotPasswordResponce("Ошибка поиска почты",false);
    }
}
