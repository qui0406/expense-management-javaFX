/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Category;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.Transaction;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ttthu
 */
public class TransactionServices {

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(
                    "SELECT * FROM transactions WHERE date BETWEEN ? AND ? AND userId = ?");
            stm.setDate(1, java.sql.Date.valueOf(startDate));
            stm.setDate(2, java.sql.Date.valueOf(endDate));
            stm.setInt(3, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(rs.getInt("id"), rs.getDouble("amount"),
                        rs.getInt("category_id"), rs.getDate("date").toLocalDate(), rs.getString("note"), rs.getInt("userId"));
                transactions.add(t);
            }
        }
        return transactions;
    }

    public void addTransaction(Transaction tran) {
        try (Connection conn = JdbcUtils.getConn()) {
            String newTran = "INSERT INTO transactions (amount, note, date, category_id, userId) VALUE (?, ?, ?, ?,?)";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setDouble(1, tran.getAmount());
            stm.setString(2, tran.getNote());
            stm.setDate(3, java.sql.Date.valueOf(tran.getDate()));
            stm.setInt(4, tran.getCategoryId());
            stm.setInt(5, tran.getUserId());
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteTransaction(int id) {
        try (Connection conn = JdbcUtils.getConn()) {
            String newTran = "DELETE FROM transactions WHERE id =  ?";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy giao dịch với id hợp lệ");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateTransaction(int id, double amount, int categoryId, LocalDate date, String note, int userId) {
        String sql = "UPDATE transactions SET amount = ?, category_id = ?, date = ?, note = ?, userId = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setDouble(1, amount);
            stm.setInt(2, categoryId);
            stm.setDate(3, Date.valueOf(date));
            stm.setString(4, note);
            stm.setInt(5, userId);
            stm.setInt(6, id);

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllTran() throws SQLException {
        List<Transaction> trans = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        try (Connection conn = JdbcUtils.getConn()) {
            String transQuery = "SELECT t.*, c.name AS category_name FROM transactions t JOIN categories c ON t.category_id = c.id WHERE t.userId = ?";
            PreparedStatement stm = conn.prepareStatement(transQuery);
            stm.setInt(1, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                trans.add(new Transaction(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getInt("category_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("note"),
                        rs.getInt("userId")
                ));
            }
        }
        return trans;
    }

}
