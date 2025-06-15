package com.courcach.Server.Services.User.Auth;


import com.courcach.Database.DatabaseConnection;
import com.courcach.Server.Services.ClassesForRequests.Users;
import com.courcach.Server.Utils.PasswordUtil;

import java.sql.*;

public class AuthService {
    public AuthResponse authenticate(String username, String password) {
        String query = "SELECT u.login, u.password, u.firstName, u.lastName, u.email, " +
                "u.salt, u.roleID, u.isBlocked, u.wallet, u.lastLogin " +
                "FROM users u WHERE u.login = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String salt = rs.getString("salt");
                int roleId = rs.getInt("roleID");
                boolean isBlocked = rs.getBoolean("isBlocked");

                if (PasswordUtil.checkPassword(password, storedHash, salt)) {
                    if (!isBlocked) {
                        updateLastLogin(conn, username);

                        String roleName = getRoleName(conn, roleId);
                        if (roleName != null) {
                            Users user = new Users();
                            user.setLogin(username);
                            user.setFirstName(rs.getString("firstName"));
                            user.setLastName(rs.getString("lastName"));
                            user.setEmail(rs.getString("email"));
                            user.setWallet(rs.getDouble("wallet"));
                            user.setLastLogin(rs.getTimestamp("lastLogin"));

                            return new AuthResponse(true, roleName, "Аутентификация успешна", user);
                        }
                    } else {
                        return new AuthResponse(false, null, "Ваш аккаунт был заблокирован", null);
                    }
                }
            }
            return new AuthResponse(false, null, "Неверно введены данные", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthResponse(false, null, "Ошибка БД", null);
        }
    }

    private void updateLastLogin(Connection conn, String username) throws SQLException {
        String updateQuery = "UPDATE users SET lastLogin = CURRENT_TIMESTAMP WHERE login = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, username);
            updateStmt.executeUpdate();
        }
    }

    private String getRoleName(Connection conn, int roleId) throws SQLException {
        String roleQuery = "SELECT r.roleName FROM roles r WHERE r.roleID = ?";
        try (PreparedStatement roleStmt = conn.prepareStatement(roleQuery)) {
            roleStmt.setInt(1, roleId);
            ResultSet roleRs = roleStmt.executeQuery();
            return roleRs.next() ? roleRs.getString("roleName") : null;
        }
    }

    public RegResponse register(String email, String name, String surname, String login, String password) {
        String regQuery = "INSERT INTO users (login, password, firstName, lastName, roleID, email, salt, isBlocked, wallet, lastLogin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(regQuery, Statement.RETURN_GENERATED_KEYS)) {

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
            stmt.setDouble(9, 0.0);
            stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return new RegResponse(true, "Регистрация успешна");
                    }
                }
                return new RegResponse(true, "Регистрация успешна");
            } else {
                return new RegResponse(false, "Не удалось зарегистрировать пользователя");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error message: " + e.getMessage());
            return handleRegistrationError(e);
        }
    }

    private RegResponse handleRegistrationError(SQLException e) {
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            if (message.contains("users.PRIMARY")) {
                return new RegResponse(false, "Логин уже занят");
            } else if (message.contains("users.email")) {
                return new RegResponse(false, "Email уже используется");
            }
            return new RegResponse(false, "Данные уже используются");
        }
        return new RegResponse(false, "Ошибка базы данных: " + e.getMessage());
    }

}
