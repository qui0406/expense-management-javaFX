package com.qlct.services;

import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class LoginServices {
    public static boolean checkUserAuth(String username, String password) throws SQLException {
        String sql = "SELECT password FROM user WHERE username = ?";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username.trim());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(password, storedHash);
            }
        }
        return false;
    }
    
    


    
    public static boolean registerUser(String firstName, String lastName, String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO user (username, password, email, first_name, last_name, created_date) VALUES (?, ?, ?, ?, ?, NOW())";

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username.trim());
            stm.setString(2, hashedPassword);
            stm.setString(3, email.trim());
            stm.setString(4, firstName.trim());
            stm.setString(5, lastName.trim());

            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi rõ ràng
            return false;
        }
    }
    
    public static String validateRegisterInput(String firstName, String lastName, String username, String email, String password, String confirmPassword) throws SQLException {
        if (firstName.isEmpty()) return "Tên không được để trống!";
        if (lastName.isEmpty()) return "Họ không được để trống!";
        if (username.isEmpty()) return "Tên đăng nhập không được để trống!";
        if (username.length() < 3) return "Tên đăng nhập phải từ 3 ký tự trở lên!";
        if (username.length() > 30) return "Tên đăng nhập phải dưới 30 ký tự!";
        if(isValidUsername(username)) return "Username không được chứa kí tự đặc biệt";
        if(isValidEmail(email)) return "Chưa đúng định dạng email";
        if(checkEmailExist(email)) return "Email đã tồn tại";
        if (password.isEmpty()) return "Mật khẩu không được để trống!";
        if (confirmPassword.isEmpty()) return "Xác nhận mật khẩu không được để trống!";
        if (password.length() < 8) return "Mật khẩu phải có ít nhất 8 ký tự!";
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return "Mật khẩu cần có chữ hoa, chữ thường, số và ký tự đặc biệt!";
        }
        if (!password.equals(confirmPassword)) return "Mật khẩu xác nhận không khớp!";
        if(checkUsernameExist(username)){
            return "Username đã tồn tại!";
        }
    
        return "";
    }
    
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Chỉ cho phép chữ cái, số và dấu gạch dưới
        String regex = "^[A-Za-z0-9_]+$";
        return username.matches(regex);
    }


    private static boolean checkUsernameExist(String username) throws SQLException {
        String sql = "SELECT password FROM user WHERE username = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setString(1, username.trim());
            ResultSet rs = stm.executeQuery();
            return rs.next(); 
        }
    }
    
    private static boolean checkEmailExist(String email) throws SQLException {
        String sql = "SELECT password FROM user WHERE email = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, email.trim());
            ResultSet rs = stm.executeQuery();
            return rs.next(); 
        }
    }
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    
    
}
