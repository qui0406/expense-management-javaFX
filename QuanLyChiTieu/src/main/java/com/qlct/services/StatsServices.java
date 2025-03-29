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
    
    public List<Expense> getExpenseCurrentMonth() throws SQLException {
        List<Expense> expenseList= new ArrayList<>();
        int userId= UserSessionServices.getCurrentUser().getId();
        String sql = "SELECT category.id, category.name as name , COALESCE(SUM(transaction.amount), 0) as amount " +
                 "FROM user " +
                 "LEFT JOIN category ON user.id = category.userId " +
                 "LEFT JOIN transaction ON category.id = transaction.categoryId " +
                 "WHERE user.id = ? AND category.type = 'Chi tiêu' " +
                 "AND MONTH(transaction.transactionDate) = MONTH(CURRENT_DATE) " +
                 "AND YEAR(transaction.transactionDate) = YEAR(CURRENT_DATE) " +
                 "GROUP BY category.id, category.name ORDER BY category.id" ;

        try (Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                int categoryId = rs.getInt("id");
                String categoryName= rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense= new Expense(categoryId, categoryName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }
    
    public List<Expense> getExpensesFilterByDate(LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<Expense> expenseList= new ArrayList<>();
        int userId= UserSessionServices.getCurrentUser().getId();
        String sql = "SELECT category.id, category.name as name , COALESCE(SUM(transaction.amount), 0) as amount " +
                 "FROM user " +
                 "LEFT JOIN category ON user.id = category.userId " +
                 "LEFT JOIN transaction ON category.id = transaction.categoryId " +
                 "WHERE user.id = ? AND category.type = 'Chi tiêu' " +
                 "AND transaction.transactionDate BETWEEN ? AND ? " + 
                 "GROUP BY category.id, category.name ORDER BY category.id" ;

        try (Connection conn = JdbcUtils.getConn()){
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setDate(2, Date.valueOf(fromDate));
            stm.setDate(3, Date.valueOf(toDate));
            ResultSet rs = stm.executeQuery();
            while(rs.next()){
                int categoryId = rs.getInt("id");
                String categoryName= rs.getString("name");
                double sumAmount = rs.getDouble("amount");
                expense= new Expense(categoryId, categoryName, sumAmount);
                expenseList.add(expense);
            }
        }
        return expenseList;
    }
    
    
    public List<Expense> getMonthlyExpenses(int year) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT MONTH(transaction.transactionDate) AS month, " +
                     "COALESCE(SUM(transaction.amount), 0) AS total_amount " +
                     "FROM user " +
                     "LEFT JOIN category ON user.id = category.userId " +
                     "LEFT JOIN transaction ON category.id = transaction.categoryId " +
                     "WHERE user.id = ? AND category.type = 'Chi tiêu' " +
                     "AND YEAR(transaction.transactionDate) = ? " +
                     "GROUP BY MONTH(transaction.transactionDate) " +
                     "ORDER BY MONTH(transaction.transactionDate)";

        try (Connection conn = JdbcUtils.getConn();
            PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, userId);
            stm.setInt(2, year);

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
