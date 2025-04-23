package com.courcach.Database;
import com.courcach.Server.Utils.PasswordUtil;

import java.sql.*;

public class DatabaseConnection {
    static final String PORT = "3306";
    static final String USERNAME = "root";
    static final String PASSWORD = "12345";
    static final String DB_NAME = "logisticorder";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:" + PORT + "/" + DB_NAME +
                        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                USERNAME,
                PASSWORD
        );
    }
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }







    // тест бд
    private static void initializeDatabase(Connection conn) throws SQLException {
       //createDefaultAdminIfNotExists(conn);
    }

    private static void createDefaultAdminIfNotExists(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE login= 'admin'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            rs.next();
            if (rs.getInt(1) == 0) {
                String salt = PasswordUtil.generateSalt();
                String passwordHash = PasswordUtil.hashPassword("admin", salt);

                String insertSql = "INSERT INTO Users (login, password,firstName, lastName,email, salt,roleID ) " +
                        "VALUES (?, ?, ?, ?, ?,?, (SELECT roleID FROM roles WHERE roleName = 'admin'))";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, passwordHash);
                    pstmt.setString(3, "System");
                    pstmt.setString(4, "Administrator");
                    pstmt.setString(5, "admin@gmail.com");
                    pstmt.setString(6, salt);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Админ создан");
                    }
                }
            } else {
                System.out.println("Админ уже существует");
            }
        }
    }
}