package com.courcach.Server.Services;


import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.Server.Utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    public AuthResponse authenticate(String username, String password) {
        String query = "SELECT u.login, u.password, u.firstName,u.lastName, u.email, u.salt, u.roleID, u.isBlocked,u.wallet,u.photoPath FROM users u WHERE u.login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String salt = rs.getString("salt");
                int roleId = rs.getInt("roleID");
                Boolean isBlocked = rs.getBoolean("isBlocked");
                if (PasswordUtil.checkPassword(password, storedHash, salt)) {
                    if(!isBlocked){
                        String roleQuery = "SELECT r.roleName FROM roles r WHERE r.roleID = ?";
                        try (PreparedStatement roleStmt = conn.prepareStatement(roleQuery)) {
                            roleStmt.setInt(1, roleId);
                            ResultSet roleRs = roleStmt.executeQuery();

                            if (roleRs.next()) {
                                String roleName = roleRs.getString("roleName");
                                Users user = new Users();
                                user.setLogin(username);
                                user.setFirstName(rs.getString("firstName"));
                                user.setLastName(rs.getString("lastName"));
                                user.setEmail(rs.getString("email"));
                                user.setWallet(rs.getDouble("wallet"));
                                user.setPhotoPath(rs.getString("photoPath"));
                                return new AuthResponse(true, roleName, "Аутентификация успешна",user);
                            }
                        }
                    }
                    else{
                        return new AuthResponse(false, null, "Ваш аккаунт был заблокирован",null);
                    }

                }
            }
            return new AuthResponse(false, null, "Неверно введены данные",null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthResponse(false, null, "Ошибка БД",null);
        }
    }

    public RegResponse register(String email, String name, String surname, String login, String password) {
        String regQuery = "INSERT INTO users (login, password, firstName, lastName, roleID, email, salt, isBlocked) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(regQuery)){
            String salt = PasswordUtil.generateSalt();
            String hashPassword = PasswordUtil.hashPassword(password, salt);
            stmt.setString(1, login);
            stmt.setString(2, hashPassword);
            stmt.setString(3, name);
            stmt.setString(4, surname);
            stmt.setInt(5, 2);
            stmt.setString(6, email);
            stmt.setString(7, salt);
            stmt.setBoolean(8, false);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return new RegResponse(true, "Регистрация успешна");
            } else {
                return new RegResponse(false, "Не удалось зарегистрировать пользователя");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("login")) {
                    return new RegResponse(false, "Логин уже занят");
                } else if (e.getMessage().contains("email")) {
                    return new RegResponse(false, "Email уже используется");
                }
            }
            return new RegResponse(false, "Ошибка БД");
        }
    }
}
