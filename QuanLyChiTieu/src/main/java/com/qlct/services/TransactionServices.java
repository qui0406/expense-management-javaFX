/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Category;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.Transaction;
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
public class TransactionServices {
    
    public void addTransaction(Transaction tran){
        try(Connection conn = JdbcUtils.getConn()){
            String newTran = "INSERT INTO transactions (amount, note, date, category_id) VALUE (?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setDouble(1, tran.getAmount());
            stm.setString(2, tran.getNote());
            stm.setDate(3, java.sql.Date.valueOf(tran.getDate()));
            stm.setInt(4, tran.getCategoryId());
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteTransaction(int id){
        try(Connection conn = JdbcUtils.getConn()){
            String newTran = "DELETE FROM transactions WHERE id =  ?";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0){
                throw new SQLException("Không tìm thấy giao dịch với id hợp lệ");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Transaction> getAllTran() throws SQLException{
        List<Transaction> trans = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            String transQery = "SELECT t.*, c.name AS category_name FROM transactions t JOIN categories c ON t.category_id = c.id";
            PreparedStatement stm = conn.prepareStatement(transQery);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                trans.add(new Transaction(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getInt("category_id") ,
                        rs.getDate("date").toLocalDate(),
                        rs.getString("note")
                ));
            }
        } 
        return trans;
    }
        
}


