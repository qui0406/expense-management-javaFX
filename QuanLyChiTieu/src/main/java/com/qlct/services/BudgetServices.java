/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.JdbcUtils;
import com.qlct.quanlychitieu.App;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

    public void setLblStatusInEdit(Label lblStatusInEdit) {
        this.lblStatusInEdit = lblStatusInEdit;
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
            lblStatus.setText("Không được để trống ô này");
            lblStatus.setStyle("-fx-text-fill: red;");
            return 0.0;     // Tra ve gia tri mac dinh 
        }

        // Loai bo cac so 0 du thua 
        input = input.replaceFirst("^0+(?!$)", "");
        if (!isValidAmount(input)) {
            Platform.runLater(() -> {
                lblStatus.setText("Giá trị nhập vào không hợp lệ");
                lblStatus.setStyle("-fx-text-fill: red;");
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
        amount = Double.valueOf(df.format(input));
        return Math.round(amount);
    }

    public void insertBudgetTODB(String input) throws SQLException {
        double amount = validateInput(input);

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO budget (amountBudget) VALUES (?)");
            stm.setDouble(1, amount);
            int rowsInserted = stm.executeUpdate();
            Platform.runLater(() -> {
                if (rowsInserted > 0) {
                    lblStatus.setText("✅ Thêm ngân sách thành công!");
                    lblStatus.setStyle("-fx-text-fill: green;"); // Đổi màu xanh cho Label
                } else {
                    lblStatus.setText("❌ Thêm ngân sách thất bại!");
                    lblStatus.setStyle("-fx-text-fill: red;"); // Đổi màu đỏ cho Label
                }
            });
        }
    }

    public void updateBudgetTODB(String input) throws SQLException {
        double amount = validateInput(input);

        try (Connection conn = JdbcUtils.getConn()) {

            //Lấy ID cuối cùng
            int lastId = -1;
            PreparedStatement stm1 = conn.prepareCall("SELECT MAX(id) FROM budget");
            ResultSet rs = stm1.executeQuery();
            if (rs.next()) {
                lastId = rs.getInt(1);
            }

            if (lastId != -1) //Trường hợp ID hợp lệ
            {
                try (PreparedStatement stm2 = conn.prepareCall("UPDATE budget SET amountBudget =  ? WHERE id = ?")) {
                    stm2.setDouble(1, amount);
                    stm2.setInt(2, lastId);
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
                System.out.println("Không tìm thấy ID hợp lệ để cập nhật.");
            }
        }
    }

    public boolean isBudgetExistForCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT MAX(id) FROM budget WHERE MONTH(createdDate) = ?, YEAR(createdDate) = ? ");
            stm.setInt(1, currentMonth);
            stm.setInt(2, currentYear);
            ResultSet rs = stm.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Ngân sách tồn tại
            }

        } catch (SQLException ex) {
            Logger.getLogger(BudgetServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
