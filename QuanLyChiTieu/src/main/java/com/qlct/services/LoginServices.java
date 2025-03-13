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
    public static boolean saveUser(String username, String password) throws SQLException{
        String sql= "Select * from user where username = ? and password = ?";
        boolean checkUser = false;
        try(Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);
            stm.setString(2, password);
            
            ResultSet rs = stm.executeQuery();
            if(rs.isFirst()){
                checkUser = true;
            }
        }
        return checkUser;
    }
}
