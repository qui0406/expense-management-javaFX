/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Budget;
import com.qlct.pojo.Category;
import com.qlct.services.BudgetServices;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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
    Label lblStatusInEdit;
    @FXML
    TableView<Budget> tbBudget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        budgetServices.setLblStatusInEdit(lblStatusInEdit);
        loadTableData();
        loadColumns();
        btnEditBudget.setOnAction(e -> {
            try {
                int budgetId = editingBudget.getId();
                budgetServices.updateBudgetTODB(txtInputBudget.getText(), budgetId);
            } catch (SQLException ex) {
                budgetServices.updateStatus(lblStatusInEdit, "❌ Lỗi kết nối CSDL", "-fx-text-fill: red;");
                ex.printStackTrace(); //In ra lỗi console để debug
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
                        String name = categoryServices.getCategoryById(categoryId);
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
                    System.out.println("ID: " + budget.getId() + " exists in current month: " + isExists);
                    return isExists;

                })
                .collect(Collectors.toList());

        tbBudget.setItems(FXCollections.observableList(filteredBudgets));
    }
}
