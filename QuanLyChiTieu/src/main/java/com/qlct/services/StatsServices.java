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
import javafx.event.Event;

public class StatsServices {

    private Expense expense;

    public List<Expense> getExpenseCurrentMonth(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

//        String sql = "{call stats_current_month(?, ?)}";
        String sql
                = "SELECT \n"
                + "    category.id, \n"
                + "    category.name AS name, \n"
                + "    COALESCE(SUM(transaction.amount), 0) AS amount\n"
                + "FROM \n"
                + "    user\n"
                + "JOIN \n"
                + "    user_cate ON user.id = user_cate.user_id\n"
                + "JOIN \n"
                + "    category ON category.id = user_cate.category_id\n"
                + "LEFT JOIN \n"
                + "    transaction ON transaction.user_id = user.id \n"
                + "               AND transaction.category_id = category.id\n"
                + "               AND MONTH(transaction.date) = MONTH(CURRENT_DATE)\n"
                + "               AND YEAR(transaction.date) = YEAR(CURRENT_DATE)\n"
                + "WHERE \n"
                + "    user.id = ? AND category.type = ?\n"
                + "GROUP BY \n"
                + "    category.id, category.name\n"
                + "UNION\n"
                + "SELECT \n"
                + "    category.id, \n"
                + "    category.name AS name, \n"
                + "    0 AS amount\n"
                + "FROM \n"
                + "    category\n"
                + "WHERE \n"
                + "    category.is_default = 1 AND category.type = ?\n"
                + "    AND category.id NOT IN (\n"
                + "        SELECT category.id\n"
                + "        FROM user\n"
                + "        JOIN user_cate ON user.id = user_cate.user_id\n"
                + "        JOIN category ON category.id = user_cate.category_id\n"
                + "        WHERE user.id = ?\n"
                + "    )\n"
                + "ORDER BY id;";

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setString(2, type);
            stm.setString(3, type);
            stm.setInt(4, userId);

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

//        String sql = "{call stats_filter_by_date(?, ?, ?, ?)}";
        String sql = "SELECT \n"
                + "    category.id, \n"
                + "    category.name AS name, \n"
                + "    COALESCE(SUM(transaction.amount), 0) AS amount\n"
                + "FROM \n"
                + "    user\n"
                + "JOIN \n"
                + "    user_cate ON user.id = user_cate.user_id\n"
                + "JOIN \n"
                + "    category ON category.id = user_cate.category_id\n"
                + "LEFT JOIN \n"
                + "    transaction ON transaction.user_id = user.id \n"
                + "               AND transaction.category_id = category.id\n"
                + "               AND MONTH(transaction.date) = MONTH(CURRENT_DATE)\n"
                + "               AND YEAR(transaction.date) = YEAR(CURRENT_DATE)\n"
                + "WHERE \n"
                + "    user.id =? AND category.type = ?\n"
                + "    AND transaction.date BETWEEN ? AND ?\n"
                + "GROUP BY \n"
                + "    category.id, category.name\n"
                + "UNION\n"
                + "SELECT \n"
                + "    category.id, \n"
                + "    category.name AS name, \n"
                + "    0 AS amount\n"
                + "FROM \n"
                + "    category\n"
                + "WHERE \n"
                + "    category.is_default = 1 AND category.type = ?\n"
                + "    AND category.id NOT IN (\n"
                + "        SELECT category.id\n"
                + "        FROM user\n"
                + "        JOIN user_cate ON user.id = user_cate.user_id\n"
                + "        JOIN category ON category.id = user_cate.category_id\n"
                + "        WHERE user.id = ?\n"
                + "    )\n"
                + "ORDER BY id;";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setString(2, type);
            stm.setDate(3, Date.valueOf(fromDate));
            stm.setDate(4, Date.valueOf(toDate));
            stm.setString(5, type);
            stm.setInt(6, userId);
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
        // String sql = "{call stats_month_expense(?, ?)}";
        String sql = "SELECT \n"
                + "    MONTH(transaction.date) AS month, \n"
                + "    COALESCE(SUM(transaction.amount), 0) AS total_amount\n"
                + "FROM \n"
                + "    user\n"
                + "JOIN \n"
                + "    user_cate ON user.id = user_cate.user_id\n"
                + "JOIN \n"
                + "    category ON user_cate.category_id = category.id\n"
                + "LEFT JOIN \n"
                + "    transaction ON category.id = transaction.category_id\n"
                + "WHERE \n"
                + "    (user.id = ? AND category.type = ?) \n"
                + "    AND YEAR(transaction.date) = YEAR(CURDATE())\n"
                + "GROUP BY \n"
                + "    MONTH(transaction.date)\n"
                + "ORDER BY \n"
                + "    MONTH(transaction.date);";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
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
