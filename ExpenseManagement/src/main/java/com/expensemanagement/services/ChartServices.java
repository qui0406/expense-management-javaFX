package com.expensemanagement.services;

import com.expensemanagement.pojo.Expense;
import com.expensemanagement.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.List;

public class ChartServices {

    private Expense expense;

    public void loadPieChart(PieChart chart, List<Expense> expenses) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        for (Expense expense : expenses) {
            data.add(new PieChart.Data(expense.getCategoryName(), expense.getAmount()));
        }

        chart.setData(data); 
        chart.setLabelsVisible(false);
        chart.setLegendVisible(true);
        chart.setMinSize(350, 320);
        chart.setPrefSize(100, 100);   

        for (PieChart.Data d : chart.getData()) {
            double percent = (d.getPieValue() / total) * 100;
            d.nameProperty().set(String.format("%s - %.1f%%", d.getName(), percent));
        }
    }

    public void loadBarChart(BarChart<String, Number> chart, List<Expense> expenses) {
        // Xóa hết dữ liệu cũ trong biểu đồ
        chart.getData().clear();
        chart.setMinSize(600, 300);
        chart.setPrefSize(600, 300);

        // Tạo một series mới để thêm vào biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        String[] colors = {
            "#00BCD4", // Xanh ngọc
            "#CDDC39", // Vàng chanh
            "#673AB7", // Tím đậm
            "#E91E63", // Hồng đậm
            "#3F51B5", // Xanh lam đậm
            "#8BC34A", // Xanh lá non
            "#FF9800", // Cam
        };

        int i = 0;
        // Thêm các dữ liệu vào biểu đồ
        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);

            final int colorIndex = i % colors.length;
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + colors[colorIndex] + ";");
                }
            });

            i++;
        }

        // Thêm series vào biểu đồ
        chart.getData().add(series);
    }

    public void loadLineChart(LineChart<String, Number> chart, List<Expense> expenses) {
        chart.getData().clear();
        chart.setTitle("Chi tiêu theo danh mục");
        chart.setPrefSize(850, 350);
        chart.setMinSize(850, 350);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Expense expense : expenses) {
            series.getData().add(new XYChart.Data<>(expense.getCategoryName(), expense.getAmount()));
        }

        chart.getData().add(series);
    }

    public List<Expense> getExpenseCurrentMonthChart(String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "{call stats_current_month_chart(?, ?)}";
//        String sql = "WITH ranked_categories AS (\n"
//                + "    SELECT \n"
//                + "        category.id, \n"
//                + "        category.name, \n"
//                + "        COALESCE(SUM(transaction.amount), 0) AS amount\n"
//                + "    FROM \n"
//                + "        user\n"
//                + "    JOIN \n"
//                + "        user_cate ON user.id = user_cate.user_id\n"
//                + "    JOIN \n"
//                + "        category ON category.id = user_cate.category_id\n"
//                + "    LEFT JOIN \n"
//                + "        transaction ON transaction.user_id = user.id \n"
//                + "                   AND transaction.category_id = category.id\n"
//                + "                   AND MONTH(transaction.date) = MONTH(CURRENT_DATE)\n"
//                + "                   AND YEAR(transaction.date) = YEAR(CURRENT_DATE)\n"
//                + "    WHERE \n"
//                + "        user.id = ? AND category.type = ?\n"
//                + "    GROUP BY \n"
//                + "        category.id, category.name\n"
//                + "    HAVING \n"
//                + "        amount > 0\n"
//                + "),\n"
//                + "top_5 AS (\n"
//                + "    SELECT * FROM ranked_categories\n"
//                + "    ORDER BY amount DESC\n"
//                + "    LIMIT 5\n"
//                + "),\n"
//                + "others AS (\n"
//                + "    SELECT \n"
//                + "        0 AS id,\n"
//                + "        'Khác' AS name,\n"
//                + "        SUM(amount) AS amount\n"
//                + "    FROM (\n"
//                + "        SELECT * FROM ranked_categories\n"
//                + "        ORDER BY amount DESC\n"
//                + "        LIMIT 18446744073709551615 OFFSET 5\n"
//                + "    ) AS other_cats\n"
//                + ")\n"
//                + "SELECT * FROM top_5\n"
//                + "UNION ALL\n"
//                + "SELECT * FROM others\n"
//                + "WHERE amount IS NOT NULL"
//                + "ORDER BY amount DESC;";

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

    public List<Expense> getExpensesFilterByDateChart(LocalDate fromDate, LocalDate toDate, String type) throws SQLException {
        List<Expense> expenseList = new ArrayList<>();
        int userId = UserSessionServices.getCurrentUser().getId();

        String sql = "{call stats_filter_by_date_chart(?, ?, ?, ?)}";
//        String sql = "WITH ranked_categories AS (\n"
//                + "    SELECT \n"
//                + "        category.id, \n"
//                + "        category.name, \n"
//                + "        COALESCE(SUM(transaction.amount), 0) AS amount\n"
//                + "    FROM \n"
//                + "        user\n"
//                + "    JOIN \n"
//                + "        user_cate ON user.id = user_cate.user_id\n"
//                + "    JOIN \n"
//                + "        category ON category.id = user_cate.category_id\n"
//                + "    LEFT JOIN \n"
//                + "        transaction ON transaction.user_id = user.id \n"
//                + "                   AND transaction.category_id = category.id\n"
//                + "                   AND transaction.date BETWEEN ? AND ?"
//                + "    WHERE \n"
//                + "        user.id = ? AND category.type = ?\n"
//                + "    GROUP BY \n"
//                + "        category.id, category.name\n"
//                + "    HAVING \n"
//                + "        amount > 0\n"
//                + "),\n"
//                + "top_5 AS (\n"
//                + "    SELECT * FROM ranked_categories\n"
//                + "    ORDER BY amount DESC\n"
//                + "    LIMIT 5\n"
//                + "),\n"
//                + "others AS (\n"
//                + "    SELECT \n"
//                + "        0 AS id,\n"
//                + "        'Khác' AS name,\n"
//                + "        SUM(amount) AS amount\n"
//                + "    FROM (\n"
//                + "        SELECT * FROM ranked_categories\n"
//                + "        ORDER BY amount DESC\n"
//                + "        LIMIT 18446744073709551615 OFFSET 5\n"
//                + "    ) AS other_cats\n"
//                + ")\n"
//                + "SELECT * FROM top_5\n"
//                + "UNION ALL\n"
//                + "SELECT * FROM others\n"
//                + "WHERE amount IS NOT NULL"
//                + "ORDER BY amount DESC;";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setDate(1, Date.valueOf(fromDate));
            stm.setDate(2, Date.valueOf(toDate));
            stm.setInt(3, userId);
            stm.setString(4, type);
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

}
