/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.expensemanagement.services;

import com.expensemanagement.pojo.Category;
import com.expensemanagement.pojo.JdbcUtils;
import com.expensemanagement.pojo.Transaction;
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
                    "SELECT * FROM transaction WHERE DATE(date) BETWEEN ? AND ? AND user_id = ?");
            stm.setDate(1, java.sql.Date.valueOf(startDate));
            stm.setDate(2, java.sql.Date.valueOf(endDate));
            stm.setInt(3, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(rs.getInt("id"), rs.getDouble("amount"),
                        rs.getInt("category_id"), rs.getDate("date").toLocalDate(), rs.getString("note"), rs.getInt("user_id"));
                transactions.add(t);
            }
        }
        return transactions;
    }

    public boolean addTransaction(Transaction tran) {
        tran.validate();

        try (Connection conn = JdbcUtils.getConn()) {
            // Kiểm tra nếu auto-commit đã bị tắt
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }

            String newTran = "INSERT INTO transaction (amount, note, date, category_id, user_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setDouble(1, tran.getAmount());
            stm.setString(2, tran.getNote());
            stm.setDate(3, java.sql.Date.valueOf(tran.getDate()));
            stm.setInt(4, tran.getCategoryId());
            stm.setInt(5, tran.getUserId());

            int rowsInserted = stm.executeUpdate();

            // Kiểm tra xem dữ liệu đã được thêm thành công chưa
            if (rowsInserted > 0) {
                System.out.println("---- Giao dịch đã được thêm thành công ----");
                System.out.println("Amount: " + tran.getAmount());
                System.out.println("Note: " + tran.getNote());
                System.out.println("Date: " + tran.getDate());
                System.out.println("CategoryId: " + tran.getCategoryId());
                System.out.println("UserId: " + tran.getUserId());
                return true;
            } else {
                System.out.println("---- Không thể thêm giao dịch ----");
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, "Lỗi khi thêm giao dịch", ex);
            return false;
        }
    }

    public void deleteTransaction(int id) throws SQLException{
        try (Connection conn = JdbcUtils.getConn()) {
            String newTran = "DELETE FROM transaction WHERE id =  ?";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy giao dịch với id hợp lệ");
            }
        } 
    }

    public void updateTransaction(int id, double amount, int categoryId, LocalDate date, String note, int userId) {
        Transaction tran = new Transaction(id, amount, categoryId, date, note, userId);
        tran.validate();
        String sql = "UPDATE transaction SET amount = ?, category_id = ?, date = ?, note = ?, user_id = ? WHERE id = ?";

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
            String transQuery = "SELECT t.*, c.name AS category_name FROM transaction t JOIN category c ON t.category_id = c.id WHERE t.user_id = ?";
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
                        rs.getInt("user_id")
                ));
            }
        }
        return trans;
    }
}
