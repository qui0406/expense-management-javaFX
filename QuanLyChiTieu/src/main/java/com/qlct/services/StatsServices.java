/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Expense;
import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author QUI
 */
public class StatsServices {
    private Expense expense;
    
    public List<Expense> getExpenseCurrentMonth(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT categories.id, categories.name AS name, COALESCE(SUM(transactions.amount), 0) AS amount " +
                     "FROM user " +
                     "JOIN user_cates ON user.id = user_cates.user_id " +
                     "JOIN categories ON user_cates.category_id = categories.id " +
                     "LEFT JOIN transactions ON categories.id = transactions.category_id " +
                     "WHERE user.id = ? " +
                     "AND categories.type = ? " +
                     "AND MONTH(transactions.date) = MONTH(CURRENT_DATE) " +
                     "AND YEAR(transactions.date) = YEAR(CURRENT_DATE) " +
                     "GROUP BY categories.id, categories.name " +
                     "ORDER BY categories.id";

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setString(2, type);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int category_id = rs.getInt("id");
                String categoriesName = rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense = new Expense(category_id, categoriesName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }
    
    public List<Expense> getExpensesFilterByDate(LocalDate fromDate, LocalDate toDate, String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        // Sửa lại câu truy vấn SQL
        String sql = "SELECT categories.id, categories.name AS name, COALESCE(SUM(transactions.amount), 0) AS amount " +
                     "FROM user " +
                     "JOIN user_cates ON user.id = user_cates.user_id " +
                     "JOIN categories ON user_cates.category_id = categories.id " +
                     "LEFT JOIN transactions ON categories.id = transactions.category_id " +
                     "WHERE user.id = ? " +
                     "AND categories.type = ? " +
                     "AND transactions.date BETWEEN ? AND ? " + 
                     "GROUP BY categories.id, categories.name " +
                     "ORDER BY categories.id";

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setString(2, type);
            stm.setDate(3, Date.valueOf(fromDate));
            stm.setDate(4, Date.valueOf(toDate));
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int category_id = rs.getInt("id");
                String categoriesName = rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense = new Expense(category_id, categoriesName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }

    
    
    public List<Expense> getMonthlyExpenses(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT MONTH(transactions.date) AS month, COALESCE(SUM(transactions.amount), 0) AS total_amount " +
                     "FROM user " +
                     "JOIN user_cates ON user.id = user_cates.user_id " +
                     "JOIN categories ON user_cates.category_id = categories.id " +
                     "LEFT JOIN transactions ON categories.id = transactions.category_id " +
                     "WHERE user.id = ? " +
                     "AND categories.type = ? " +
                     "AND YEAR(transactions.date) = YEAR(CURDATE()) " +
                     "GROUP BY MONTH(transactions.date) " +
                     "ORDER BY MONTH(transactions.date)";

        try (Connection conn = JdbcUtils.getConn();
            PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, userId);
            stm.setString(2, type);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month");
                    double totalAmount = rs.getDouble("total_amount");
                    Expense expense = new Expense(month, "Tháng " + month, totalAmount);
                    expenseList.add(expense);
                }
            }
        }
        return expenseList;
    }

}
