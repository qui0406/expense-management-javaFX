/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Budget;
import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 *
 * @author DELL
 */
public class BudgetServices {

    @FXML
    Label lblStatus;
    @FXML
    Label lblStatusInEdit;
    @FXML
    Label lblTotal;
    @FXML
    Label lblTotalMoneyUsed;
    @FXML
    Label lblRemainingBudget;
    private double totalBudget;
    private double totalUsed;
    private double totalRemainingBudget;

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

    public void setLblStatusInEdit(Label lblStatusInEdit) {
        this.lblStatusInEdit = lblStatusInEdit;
    }

    public void setLblTotal(Label lblTotal) {
        totalBudget = sumBudgetInMonth();
        lblTotal.setText(String.format("%,d VND", (int) totalBudget));
        this.lblTotal = lblTotal;
    }

    public void setLblTotalMoneyUsed(Label lblTotalMoneyUsed) {
        totalUsed = sumMoneyUsedInMonth();
        lblTotalMoneyUsed.setText(String.format("%,d VND", (int) totalUsed));
        this.lblTotalMoneyUsed = lblTotalMoneyUsed;
    }

    public void setLblRemainingBudget(Label lblRemainingBudget) {
        totalRemainingBudget = totalBudget - totalUsed;
        lblRemainingBudget.setText(String.format("%,d VND", (int) totalRemainingBudget));
        this.lblRemainingBudget = lblRemainingBudget;
    }

    /**
     * @return the totalBudget
     */
    public double getTotalBudget() {
        return totalBudget;
    }

    /**
     * @return the totalUsed
     */
    public double getTotalUsed() {
        return totalUsed;
    }

    public void updateStatus(Label label, String message, String style) {
        Platform.runLater(() -> {
            label.setText(message);
            label.setStyle(style);
        });
    }

    public boolean isValidAmount(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double validateAmount(String input) {
        if (input == null || input.trim().isEmpty()) { // Kiem tra o nhap lieu
            throw new IllegalArgumentException("Không được để trống ô này");
        }

        // Kich thuoc du lieu nhap vao qua dai
        if (input.length() > 20) {
            throw new IllegalArgumentException("Giá trị nhập vào quá lớn");
        }

        // Loai bo cac so 0 du thua
        input = input.replaceFirst("^0+(?!$)", "");

        // Kiem tra kieu gia tri nhap vao da hop le chua
        if (!isValidAmount(input)) {
            throw new IllegalArgumentException("Giá trị nhập vào không hợp lệ");
        }

        double amount = Double.parseDouble(input);
        // Khong cho phep nhap so am
        if (amount < 0) {
            throw new IllegalArgumentException("Không được nhập số âm.");
        }

        // Lam tron 2 chu so thap phan
        DecimalFormat df = new DecimalFormat("#.##");
        double parsedAmount = Double.parseDouble(input);
        amount = Double.valueOf(df.format(parsedAmount));
        return Math.round(amount);
    }

    public void insertBudgetTODB(String input, String budgetType, String categoryName) throws SQLException {
        double amount = validateAmount(input);
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int categoryId = getIdByName(categoryName, currentUserId);

        // CHẶN LUÔN nếu không tìm thấy ID danh mục
        if (categoryId == -1) {
            throw new SQLException("Không tìm thấy danh mục có tên: " + categoryName + " cho userId: " + currentUserId);
        }

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)");
            stm.setDouble(1, amount);
            stm.setInt(2, currentUserId);
            stm.setString(3, budgetType);
            stm.setInt(4, categoryId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Không thể thêm ngân sách vào cơ sở dữ liệu!", e);
        }
    }

    public boolean tryInsertBudget(String input, String budgetType, String categoryName) {
        try {
            insertBudgetTODB(input, budgetType, categoryName);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double sumBudgetInMonth() {
        LocalDate currentDate = LocalDate.now();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        double total = 0.0; // Bien luu gia tri tong chi tieu

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT SUM(amount) "
                    + "FROM budget WHERE MONTH(date) = ? AND YEAR(date) = ? AND user_id = ? "
                    + "AND type = N'Chi'");
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, currentUserId);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1); // Lay gia tri tong
            }
        } catch (SQLException e) {
            System.out.println("Không thể truy xuất dữ liệu");
        }
        return total;
    }

    public double sumMoneyUsedInMonth() {
        LocalDate currentDate = LocalDate.now();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        double total = 0.0; // Bien luu gia tri tong chi tieu
        String sql = "SELECT SUM(t.amount) "
                + "FROM transaction t JOIN category c ON t.category_id = c.id "
                + "WHERE c.type = N'Chi' AND t.user_id =  ? AND MONTH(t.date) = ? AND YEAR(t.date) = ?";
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, currentUserId);
            stm.setInt(2, currentMonth);
            stm.setInt(3, currentYear);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1); // Lay gia tri tong
            }
        } catch (SQLException e) {
            System.out.println("Không thể truy xuất dữ liệu");
        }
        return total;
    }

    public boolean isBudgetValid(double newBudget, double oldBudget) {
        return newBudget >= oldBudget;
    }

    public void updateBudgetTODB(String input, int budgetId) throws SQLException {
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        double amount = validateAmount(input);
        try (Connection conn = JdbcUtils.getConn()) {
            try (PreparedStatement stm2 = conn
                    .prepareCall("UPDATE budget SET amount =  ? WHERE id = ? AND user_id = ?")) {
                stm2.setDouble(1, amount);
                stm2.setInt(2, budgetId);
                stm2.setInt(3, currentUserId);
                stm2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("Không thể cập nhật ngân sách vào cơ sở dữ liệu!", e);
        }
    }

    public boolean tryUpdateBudget(int budgetId, String input, String budgetType, int categoryId) {
        try {
            updateBudgetTODB(input, budgetId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isBudgetExistForCurrentMonth(int id, boolean byCategory) {
        LocalDate currentDate = LocalDate.now();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        String sql;
        if (byCategory == true) {
            sql = "SELECT id, date "
                    + "FROM budget "
                    + "WHERE MONTH(date) = ? AND YEAR(date) = ? AND user_id = ? AND category_id = ?";
        } else {
            sql = "SELECT id, date "
                    + "FROM budget "
                    + "WHERE MONTH(date) = ? AND YEAR(date) = ? AND user_id = ? AND id = ?";
        }
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, currentUserId);
            stm.setInt(4, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Ngân sách tồn tại
            }

        } catch (SQLException ex) {
            Logger.getLogger(BudgetServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<Budget> getBudgetsForCurrentUser() {
        List<Budget> budgets = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId(); // Lấy ID người dùng hiện tại

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM budget WHERE user_id = ?");
            stm.setInt(1, currentUserId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setAmountBudget(rs.getDouble("amount"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setType(rs.getString("type"));
                budget.setCreatedDate(rs.getObject("date", LocalDate.class));
                budget.setCategoryId(rs.getInt("category_id"));
                budgets.add(budget); // Thêm ngân sách vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("Không thể truy xuất danh sách ngân sách: " + e.getMessage());
        }
        return budgets; // Trả về danh sách ngân sách
    }

    // Phương thức lấy ID từ tên danh mục
    public int getIdByName(String name, int userId) throws SQLException {
        int id = -1; // Giá trị mặc định nếu không tìm thấy
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT c.id "
                    + "FROM category c "
                    + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                    + "WHERE c.name = ? AND (c.is_default = 1 OR (c.is_default = 0 AND uc.user_id = ?)) "
                    + "LIMIT 1;");
            stm.setString(1, name);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ
            throw e;
        }
        return id;
    }

    //Phương thức kiểm tra xem chi tiêu có nằm trong giới hạn ngân sách không
    // Nếu chi tiêu lớn hơn 80% ngân sách thì trả về false, ngược lại trả về true
    public boolean isBudgetWithinLimit(double expenses, double budget) {
        if (budget == 0) {
            return false;
        }
        double percentage = (expenses / budget) * 100;
        return percentage < 80;
    }

    //Phương thức lấy tổng ngân sách cho một danh mục cụ thể theo tháng hiện tại
    public double getTotalBudgetForCategory(int categoryId, int userId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        String sql = "SELECT SUM(b.amount) " +
                     "FROM budget b " +
                     "WHERE MONTH(b.date) = ? AND YEAR(b.date) = ? " +
                     "AND b.user_id = ? AND b.category_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, userId);
            stm.setInt(4, categoryId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy tổng ngân sách: " + e.getMessage());
        }
        return 0;
    }

    //Phương thức lấy tổng chi tiêu cho một danh mục cụ thể theo tháng hiện tại
    public double getTotalExpensesForCategory(int categoryId, int userId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        String sql = "SELECT SUM(t.amount) "+
                     "FROM transaction t " +
                     "WHERE MONTH(t.date) = ? AND YEAR(t.date) = ? " +
                     "AND t.user_id = ? AND t.category_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, userId);
            stm.setInt(4, categoryId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy tổng chi tiêu: " + e.getMessage());
        }
        return 0;
    }
    
    //Phương thức kiểm tra xem chi tiêu có vượt quá giới hạn không
    public boolean isOverBudget(double totalExpenses, double totalBudget) {
        return !isBudgetWithinLimit(totalExpenses, totalBudget);
    }

}
