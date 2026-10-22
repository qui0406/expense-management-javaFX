
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.expensemanagement;

import com.expensemanagement.pojo.Budget;
import com.expensemanagement.services.BudgetServices;
import com.expensemanagement.services.CategoryServices;
import com.expensemanagement.services.UserSessionServices;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class EditBudgetController implements Initializable {

    private BudgetServices budgetServices = new BudgetServices();
    private CategoryServices categoryServices = new CategoryServices();
    private Budget editingBudget;

    @FXML
    TextField txtInputBudget;
    @FXML
    Button btnEditBudget;
    @FXML
    TableView<Budget> tbBudget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadTableData();
        loadColumns();
        btnEditBudget.setOnAction((var e) -> {
            int budgetId = editingBudget.getId();
            double amount = editingBudget.getAmountBudget();
            String inputText = txtInputBudget.getText();
            double validatedAmount = validateInput(inputText);
            if (validatedAmount <= 0) {
                return;
            }
            try {
                handleUpdateBudget(budgetId, inputText, editingBudget.getType(), editingBudget.getCategoryId());
                loadTableData();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void setEditingBudget(Budget budget) {
        this.editingBudget = budget;
    }

    public void loadColumns() {
        // Cột Số tiền
        TableColumn<Budget, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountBudget"));
        colAmount.setPrefWidth(150);

        // Format số tiền hiển thị dưới dạng: 1.000.000 VND
        colAmount.setCellFactory(column -> new TableCell<>() {
            private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount.intValue()) + " VND");
                }
            }
        });

        // Cột Loại
        TableColumn<Budget, String> colType = new TableColumn<>("Loại");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(100);

        // Cột Danh mục
        TableColumn<Budget, String> colCateName = new TableColumn<>("Danh mục");
        colCateName.setCellFactory(column -> new TableCell<Budget, String>() {
            @Override
            protected void updateItem(String categoryName, boolean empty) {
                super.updateItem(categoryName, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Budget budget = getTableRow().getItem();
                    int categoryId = budget.getCategoryId();
                    try {
                        String name = categoryServices.getCategoryNameById(categoryId);
                        setText(name != null ? name : "Không xác định");
                    } catch (Exception e) {
                        setText("Lỗi");
                        e.printStackTrace();
                    }
                }
            }
        });
        colCateName.setPrefWidth(145);

        // Cột Ngày
        TableColumn<Budget, LocalDate> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colDate.setPrefWidth(160);

        // Thêm các cột vào bảng (bỏ cột hành động)
        tbBudget.getColumns().clear();
        tbBudget.getColumns().addAll(colAmount, colType, colCateName, colDate);
    }

    private void loadTableData() {
        List<Budget> budgets = budgetServices.getBudgetsForCurrentUser(); // Lấy danh sách ngân sách từ dịch vụ
        System.out.println(budgets.size());
        List<Budget> filteredBudgets = budgets.stream()
                .filter(budget -> {
                    // Kiểm tra xem ngân sách có tồn tại trong tháng hiện tại không
                    boolean isExists = budgetServices.isBudgetExistForCurrentMonth(budget.getId(), false);
                    return isExists;
                })
                .collect(Collectors.toList());

        tbBudget.setItems(FXCollections.observableList(filteredBudgets));
    }

    public double validateInput(String input) {
        try {
            return budgetServices.validateAmount(input);
        } catch (IllegalArgumentException e) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Lỗi ngân sách nhập vào", null, e.getMessage(), "red");
            });
            return 0.0;
        }
    }

    public void handleUpdateBudget(int budgetId, String input, String budgetType, int categoryId) {
        try {
            double amount = budgetServices.validateAmount(input);
            int currentUserId = UserSessionServices.getCurrentUser().getId();
            double totalBudget = budgetServices.getTotalBudgetForCategory(categoryId, currentUserId);
            double totalUsed = budgetServices.getTotalExpensesForCategory(categoryId, currentUserId);
            String categoryName = categoryServices.getCategoryNameById(categoryId);

            // Nếu chưa có chi tiêu (totalUsed == 0), cho phép cập nhật ngân sách thoải mái
            if (totalUsed == 0) {
                boolean success = budgetServices.tryUpdateBudget(budgetId, input, budgetType, categoryId);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Chỉnh sửa ngân sách thành công!", null,
                            "Ngân sách cho danh mục \"" + categoryName + "\" đã được cập nhật.", "green");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Chỉnh sửa ngân sách thất bại!", null,
                            "Đã xảy ra lỗi khi cập nhật ngân sách cho danh mục \"" + categoryName + "\". Vui lòng thử lại.", "red");
                }
                return;
            }

            // Nếu ngân sách hiện tại > chi tiêu, điều chỉnh tùy vào số tiền đã chi tiêu
            if (totalBudget > totalUsed) {
                if (amount < totalUsed) {
                    showAlert(Alert.AlertType.WARNING, "Thiếu ngân sách", null,
                            "Bạn cần cập nhật ngân sách không dưới " + String.format("%,.0f", totalUsed)
                            + " để bù phần đã chi tiêu cho danh mục \"" + categoryName + "\".", "orange");
                    return;
                }
                boolean success = budgetServices.tryUpdateBudget(budgetId, input, budgetType, categoryId);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Chỉnh sửa ngân sách thành công!", null,
                            "Ngân sách cho danh mục \"" + categoryName + "\" đã được cập nhật.", "green");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Chỉnh sửa ngân sách thất bại!", null,
                            "Đã xảy ra lỗi khi cập nhật ngân sách cho danh mục \"" + categoryName + "\". Vui lòng thử lại.", "red");
                }
                return;
            } // Nếu đã có chi tiêu (totalUsed > 0), kiểm tra số tiền nhập vào có đủ để bù phần thiếu hay không
            double needToAdd = totalUsed - totalBudget;
            if (needToAdd > 0 && amount < totalUsed) {
                // Nếu số tiền nhập vào ít hơn số tiền thiếu
                showAlert(Alert.AlertType.WARNING, "Thiếu ngân sách", null,
                        "Bạn cần nhập ít nhất " + String.format("%,.0f", totalUsed)
                        + " để bù phần đã chi tiêu cho danh mục \"" + categoryName + "\".", "orange");
                return;
            }

            boolean success = budgetServices.tryUpdateBudget(budgetId, input, budgetType, categoryId);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Chỉnh sửa ngân sách thành công!", null, "Ngân sách cho danh mục \"" + categoryName + "\" đã được thêm.", "green");
                loadTableData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Chỉnh sửa ngân sách thất bại!", null, "Đã xảy ra lỗi khi thêm ngân sách cho danh mục \"" + categoryName + "\". Vui lòng thử lại.", "red");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Dữ liệu không hợp lệ!", null, "Vui lòng kiểm tra lại thông tin bạn đã nhập.", "orange");
            e.printStackTrace(); // In ra lỗi console để debug
        }
    }

    public void showAlert(Alert.AlertType type, String title, String header, String content, String color) {
        // Tạo một Alert với kiểu thông báo, tiêu đề, tiêu đề phụ và nội dung
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-text-fill: " + color + ";");
        alert.showAndWait();
    }
}
