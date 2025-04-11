/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Budget;
import com.qlct.pojo.Category;
import com.qlct.pojo.JdbcUtils;
import com.qlct.quanlychitieu.App;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

    public void setLblStatusInEdit(Label lblStatusInEdit) {
        this.lblStatusInEdit = lblStatusInEdit;
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

    public double validateInput(String input) {
        if (input == null || input.trim().isEmpty()) {     // Kiem tra o nhap lieu
            updateStatus(lblStatus, "Không được để trống ô này", "-fx-text-fill: red;");
            return 0.0;     // Tra ve gia tri mac dinh 
        }

        // Loai bo cac so 0 du thua 
        input = input.replaceFirst("^0+(?!$)", "");
        if (!isValidAmount(input)) {
            Platform.runLater(() -> {
                updateStatus(lblStatus, "Giá trị nhập vào không hợp lệ", "-fx-text-fill: red;");
            });
            return 0.0;     // Tra ve gia tri mac dinh 

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
        double amount = validateInput(input);
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int categoryId = getIdByName(categoryName);
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO budget (amountBudget, userId,type, category_id) VALUES (?,?,?,?)");
            stm.setDouble(1, amount);
            stm.setInt(2, currentUserId);
            stm.setString(3, budgetType);
            stm.setInt(4, categoryId);
            int rowsInserted = stm.executeUpdate();
            Platform.runLater(() -> {
                if (rowsInserted > 0) {
                    updateStatus(lblStatus, "✅ Thêm ngân sách thành công!", "-fx-text-fill: green;");
                } else {
                    updateStatus(lblStatus, "❌ Thêm ngân sách thất bại!", "-fx-text-fill: red;");
                }
            });
        }
    }

    public double sumMethod() {
        LocalDate currentDate = LocalDate.now();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        double total = 0.0; // Bien luu gia tri tong chi tieu

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT SUM(amountBudget) "
                    + "FROM budget WHERE MONTH(createdDate) = ? AND YEAR(createdDate) = ? AND userId = ? "
                    + "AND type = N'Chi'");
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, currentUserId);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1);    // Lay gia tri tong
            }
        } catch (SQLException e) {
            System.out.println("Không thể truy xuất dữ liệu");
        }
        return total;
    }

    public boolean isBudgetValid(double newBudget, double totalSpent) {
        return newBudget >= totalSpent;
    }

    public void updateBudgetTODB(String input) throws SQLException {
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        double amount = validateInput(input);
        double totalSpent = sumMethod();

        try (Connection conn = JdbcUtils.getConn()) {

            //Lấy ID cuối cùng
            int lastId = -1;
            PreparedStatement stm1 = conn.prepareCall("SELECT MAX(id) FROM budget WHERE userId = ?");
            stm1.setInt(1, currentUserId);
            ResultSet rs = stm1.executeQuery();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }

            if (lastId != -1) //Trường hợp ID hợp lệ
            {
                if (isBudgetValid(amount, totalSpent)) {
                    try (PreparedStatement stm2 = conn.prepareCall("UPDATE budget SET amountBudget =  ? WHERE id = ? AND userId = ?")) {
                        stm2.setDouble(1, amount);
                        stm2.setInt(2, lastId);
                        stm1.setInt(3, currentUserId);
                        int rowsUpdated = stm2.executeUpdate();

                        Platform.runLater(() -> {
                            if (rowsUpdated > 0) {
                                lblStatusInEdit.setText("✅ Cập nhật ngân sách thành công!");
                                lblStatusInEdit.setStyle("-fx-text-fill: green;");
                            } else {
                                lblStatusInEdit.setText("❌ Cập nhật ngân sách thất bại!");
                                lblStatusInEdit.setStyle("-fx-text-fill: red;");
                            }
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        lblStatusInEdit.setText("Ngân sách không hợp lệ. "
                                + "Bạn đã chi tiêu trong tháng này. "
                                + "Vui lòng nhập ngân sách mới lớn hơn hoặc bằng ngân sách hiện tại!!!");
                        lblStatusInEdit.setStyle("-fx-text-fill: red;");
                    });
                    return;    //Dung thuc hien neu ngan sach khong hop le
                }
            } else {
                System.out.println("Không tìm thấy ID hợp lệ để cập nhật.");
            }
        }
    }

    public boolean isBudgetExistForCurrentMonth(int id, boolean byCategory) {
        LocalDate currentDate = LocalDate.now();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        String sql;
        if (byCategory == true) {
            sql = "SELECT id, createdDate FROM budget WHERE MONTH(createdDate) = ? AND YEAR(createdDate) = ? AND userId = ? AND categoryId = ?";
        } else {
            sql = "SELECT id, createdDate FROM budget WHERE MONTH(createdDate) = ? AND YEAR(createdDate) = ? AND userId = ? AND id = ?";
        }
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            stm.setInt(3, currentUserId);
            stm.setInt(4, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                int budgetId = rs.getInt("id");
                LocalDate date = rs.getObject("createdDate", LocalDate.class);
                System.out.println("Budget ID: " + budgetId + " | Created Date: " + date);
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
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM budget WHERE userId = ?");
            stm.setInt(1, currentUserId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setAmountBudget(rs.getDouble("amountBudget"));
                budget.setUserId(rs.getInt("userId"));
                budget.setType(rs.getString("type"));
                budget.setCreatedDate(rs.getObject("createdDate", LocalDate.class));
                budget.setCategoryId(rs.getInt("category_id"));
                budgets.add(budget); // Thêm ngân sách vào danh sách
            }
        } catch (SQLException e) {
            System.out.println("Không thể truy xuất danh sách ngân sách: " + e.getMessage());
        }
        return budgets; // Trả về danh sách ngân sách
    }

    // Phương thức lấy ID từ tên danh mục
    public int getIdByName(String name) {
        int id = -1; // Giá trị mặc định nếu không tìm thấy
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT id FROM categories WHERE name = ?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ
        }
        return id;
    }

}
