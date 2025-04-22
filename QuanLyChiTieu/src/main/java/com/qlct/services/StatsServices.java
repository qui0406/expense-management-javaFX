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

        String sql = "{call stats_current_month(?, ?)}";
        
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

        String sql = "{call stats_filter_by_date(?, ?, ?, ?)}";


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
        String sql="{call stats_month_expense(?, ?)}";

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
