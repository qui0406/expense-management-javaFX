/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author QUI
 */
public class UserSessionServices {
    private static UserSessionServices instance;
    private User currentUser;
    private String username;
    
    public UserSessionServices(String username) {
        this.username= username;
    }
    
    public User getDataUser() throws SQLException{
         String sql = "SELECT id, firstName, lastName, email FROM user WHERE username = ?";
        try (Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next()){
                currentUser = new User(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), username,  rs.getString("email"));
            }
            return currentUser;
        }
    }
    
     public static void login(String username) throws SQLException {
        if (instance == null) {
            instance = new UserSessionServices(username);
            instance.getDataUser(); // Lấy dữ liệu từ database và lưu vào currentUser
        }
    }

    public static void logout() {
        instance = null;
    }

    public static User getCurrentUser() {
        return instance != null ? instance.currentUser : null;
    }
    
    public String getUsername(){
        return username;
    }
}
