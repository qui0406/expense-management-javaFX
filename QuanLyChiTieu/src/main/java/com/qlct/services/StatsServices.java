package com.qlct.services;


import com.qlct.pojo.Expense;
import com.qlct.pojo.JdbcUtils;
import com.qlct.services.UserSessionServices;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatsServices {
    private Expense expense;

    public List<Expense> getExpenseCurrentMonth(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "SELECT category.id, category.name as name, COALESCE(SUM(transaction.amount), 0) AS amount " +
             "FROM user, category, user_cate, transaction " +
             "WHERE category.id = user_cate.category_id " +
             "AND transaction.user_id = user.id " +
             "AND user.id = ? " +
             "AND category.type = ? " +
             "AND MONTH(transaction.date) = MONTH(CURRENT_DATE) " +
             "AND YEAR(transaction.date) = YEAR(CURRENT_DATE) " +
             "GROUP BY category.id, category.name " +
             "ORDER BY category.id";
        
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

        String sql = "SELECT category.id, category.name as name, COALESCE(SUM(transaction.amount), 0) AS amount " +
             "FROM user, category, user_cate, transaction " +
             "WHERE category.id = user_cate.category_id " +
             "AND transaction.user_id = user.id " +
             "AND user.id = ? " +
             "AND category.type = ? " +
             "AND transaction.date BETWEEN ? AND ? " +
             "GROUP BY category.id, category.name " +
             "ORDER BY category.id";


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
        String sql="SELECT MONTH(transaction.date) AS month, COALESCE(SUM(transaction.amount), 0) AS total_amount \n" +
                    "FROM user \n" +
                    "JOIN user_cate ON user.id = user_cate.user_id \n" +
                    "JOIN category ON user_cate.category_id = category.id \n" +
                    "LEFT JOIN transaction ON category.id = transaction.category_id \n" +
                    "WHERE user.id = ? \n" +
                    "AND category.type = ? \n" +
                    "AND YEAR(transaction.date) = YEAR(CURRENT_DATE) \n" +
                    "GROUP BY MONTH(transaction.date) \n" +
                    "ORDER BY MONTH(transaction.date);";

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
