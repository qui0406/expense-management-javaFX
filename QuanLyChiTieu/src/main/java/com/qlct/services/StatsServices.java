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
        int user_id = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT category.id, category.name AS name, COALESCE(SUM(transaction.amount), 0) AS amount " +
                     "FROM user " +
                     "JOIN user_cate ON user.id = user_cate.user_id " +
                     "JOIN category ON user_cate.category_id = category.id " +
                     "LEFT JOIN transaction ON category.id = transaction.category_id " +
                     "WHERE user.id = ? " +
                     "AND category.type = ? " +
                     "AND MONTH(transaction.date) = MONTH(CURRENT_DATE) " +
                     "AND YEAR(transaction.date) = YEAR(CURRENT_DATE) " +
                     "GROUP BY category.id, category.name " +
                     "ORDER BY category.id";

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, user_id);
            stm.setString(2, type);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                int category_id = rs.getInt("id");
                String categoryName = rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense = new Expense(category_id, categoryName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }
    
    public List<Expense> getExpensesFilterByDate(LocalDate fromDate, LocalDate toDate, String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int user_id = UserSessionServices.getCurrentUser().getId();

        // Sửa lại câu truy vấn SQL
        String sql = "SELECT category.id, category.name AS name, COALESCE(SUM(transaction.amount), 0) AS amount " +
                     "FROM user " +
                     "JOIN user_cate ON user.id = user_cate.user_id " +
                     "JOIN category ON user_cate.category_id = category.id " +
                     "LEFT JOIN transaction ON category.id = transaction.category_id " +
                     "WHERE user.id = ? " +
                     "AND category.type = ? " +
                     "AND transaction.date BETWEEN ? AND ? " + 
                     "GROUP BY category.id, category.name " +
                     "ORDER BY category.id";

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, user_id);
            stm.setString(2, type);
            stm.setDate(3, Date.valueOf(fromDate));
            stm.setDate(4, Date.valueOf(toDate));
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int category_id = rs.getInt("id");
                String categoryName = rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense = new Expense(category_id, categoryName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }

    
    
    public List<Expense> getMonthlyExpenses(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int user_id = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT MONTH(transaction.date) AS month, COALESCE(SUM(transaction.amount), 0) AS total_amount " +
                     "FROM user " +
                     "JOIN user_cate ON user.id = user_cate.user_id " +
                     "JOIN category ON user_cate.category_id = category.id " +
                     "LEFT JOIN transaction ON category.id = transaction.category_id " +
                     "WHERE user.id = ? " +
                     "AND category.type = ? " +
                     "AND YEAR(transaction.date) = YEAR(CURDATE()) " +
                     "GROUP BY MONTH(transaction.date) " +
                     "ORDER BY MONTH(transaction.date)";

        try (Connection conn = JdbcUtils.getConn();
            PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, user_id);
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
