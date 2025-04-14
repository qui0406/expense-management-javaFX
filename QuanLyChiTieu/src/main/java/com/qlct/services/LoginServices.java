/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author QUI
 */
public class LoginServices {
    public static boolean checkUserAuth(String username, String password) throws SQLException{
        String sql = "SELECT password FROM user WHERE username = ?";
        try (Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(password, storedHash);  
            }
        }
        return false;
    }
    
    public static boolean registerUser(String firstName, String lastName, String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO user (username, password, email, first_name, last_name, created_date) VALUES (?, ?, ?,  ? , ?, NOW())";
         
        // Băm mật khẩu bằng BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username);
            stm.setString(2, hashedPassword);
            stm.setString(3, email);
            stm.setString(4, firstName);
            stm.setString(5, lastName);
            int affectedRows = stm.executeUpdate();
            return affectedRows > 0; // Trả về true nếu lưu thành công

        } catch (SQLException e) { // In lỗi ra console để debug
            // In lỗi ra console để debug
            return false;
        }
    }
    
    private void showAlertLogin(String info, String header, String title){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(info);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    
}
