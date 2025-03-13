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

/**
 *
 * @author QUI
 */
public class LoginServices {
    public static boolean checkUserAuth(String username, String password) throws SQLException{
        String sql= "Select * from user where username = ? and password = ?";
        boolean checkUser = false;
        try(Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);
            stm.setString(2, password);
            
            ResultSet rs = stm.executeQuery();
            if(rs.next()){
                checkUser = true;
            }
        }
        return checkUser;
    }
    
    public static boolean registerUser(String username, String password, String email) throws SQLException {
        String sql = "INSERT INTO user (username, email, password, createdDate) VALUES (?, ?, ?, NOW())";

        try (Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm= conn.prepareCall(sql);
            stm.setString(1, username);
            stm.setString(2, email);
            stm.setString(3, password);
            int affectedRows = stm.executeUpdate(); // Sử dụng executeUpdate() thay vì executeQuery()
            return affectedRows > 0;

        } catch (SQLException e) {
            return false;
        }
    }
}
