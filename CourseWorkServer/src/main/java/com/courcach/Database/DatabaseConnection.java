package com.courcach.Database;
import com.courcach.Server.Utils.PasswordUtil;

import java.sql.*;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/logisticcompany?useUnicode=true&characterEncoding=utf8",
                "root",
                "12345"
        );
    }
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            //initializeDatabase(conn);
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }






    // тест бд
//    private static void initializeDatabase(Connection conn) throws SQLException {
//       createDefaultAdminIfNotExists(conn);
//    }
//
//    private static void createDefaultAdminIfNotExists(Connection conn) throws SQLException {
//        String checkSql = "SELECT COUNT(*) FROM users WHERE login= 'admin'";
//        try (Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(checkSql)) {
//
//            rs.next();
//            if (rs.getInt(1) == 0) {
//                String salt = PasswordUtil.generateSalt();
//                String passwordHash = PasswordUtil.hashPassword("admin", salt);
//
//                // Получаем roleID для 'admin'
//                String roleSql = "SELECT roleID FROM roles WHERE roleName = 'admin'";
//                int roleID = -1;
//                try (Statement roleStmt = conn.createStatement();
//                     ResultSet roleRs = roleStmt.executeQuery(roleSql)) {
//                    if (roleRs.next()) {
//                        roleID = roleRs.getInt("roleID");
//                    } else {
//                        throw new SQLException("Роль 'admin' не найдена в таблице roles.");
//                    }
//                }
//
//                String insertSql = "INSERT INTO users (login, password, firstName, lastName, email, salt, roleID, isBlocked, wallet) " +
//                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
//                    pstmt.setString(1, "admin");
//                    pstmt.setString(2, passwordHash);
//                    pstmt.setString(3, "System");
//                    pstmt.setString(4, "Administrator");
//                    pstmt.setString(5, "admin@gmail.com");
//                    pstmt.setString(6, salt);
//                    pstmt.setInt(7, roleID);
//                    pstmt.setBoolean(8, false);
//                    pstmt.setFloat(9, 0.0f); // по умолчанию
//
//                    int affectedRows = pstmt.executeUpdate();
//                    if (affectedRows > 0) {
//                        System.out.println("Админ создан");
//                    }
//                }
//            } else {
//                System.out.println("Админ уже существует");
//            }
//        }
//  }
}