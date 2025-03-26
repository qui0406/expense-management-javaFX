/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Category;
import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ttthu
 */
public class CategoryServices {
    public List<Category> getCatetogires() throws SQLException{
        List<Category> cates = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareCall("SELECT * FROM categories");
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                Category c = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"));
                cates.add(c);
            }
        } 
        return cates; 
    }
    
    public List<Category> getCateByType(String type) throws SQLException{
        List<Category> cates = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareCall("SELECT * FROM categories WHERE type = ?");
            stm.setString(1, type);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                Category c = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"));
                cates.add(c);
            }
        }
        return cates;
    }
    
    public void addCategory(Category cate){
        try(Connection conn = JdbcUtils.getConn()){
            String newTran = "INSERT INTO categories (name, type) VALUE (?, ?)";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setString(1, cate.getName());
            stm.setString(2, cate.getType());
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteCategory(int id){
        try(Connection conn = JdbcUtils.getConn()){
            String delCate = "DELETE FROM categories WHERE id =  ?";
            PreparedStatement stm = conn.prepareStatement(delCate);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0){
                throw new SQLException("Không tìm thấy giao dịch với id hợp lệ");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void updateCategory(int id, String name, String type) {
    String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";

    try (Connection conn = JdbcUtils.getConn();
        PreparedStatement stm = conn.prepareStatement(sql)) {
        stm.setString(1, name);
        stm.setString(2, type);
        stm.setInt(3, id);
        stm.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
