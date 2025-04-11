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
import javafx.event.ActionEvent;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class BudgetController implements Initializable {

    private BudgetServices budgetServices = new BudgetServices();
    private CategoryServices categoryServices = new CategoryServices();

    @FXML
    TextField txtInputBudget;
    @FXML
    Button btnAddBudget;
    @FXML
    Label lblStatus;
    @FXML
    ComboBox<String> cbType;
    @FXML
    ComboBox<String> cbCate;
    @FXML
    TableView<Budget> tbBudget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        budgetServices.setLblStatus(lblStatus);

        cbType.setItems(FXCollections.observableArrayList("Chi"));

        cbType.setOnAction(e -> {
            String selectedType = cbType.getValue(); // Lấy giá trị đã chọn từ ComboBox
            if (selectedType != null) {
                loadCategoriesByType(selectedType);
            }
        });

        loadTableData();
        loadColumns();

        btnAddBudget.setOnAction(e -> {
            String selectedType = cbType.getValue(); // Lấy giá trị đã chọn từ ComboBox
            String selectedCate = cbCate.getValue();
            if (selectedType == null) {
                budgetServices.updateStatus(lblStatus, "❌ Vui lòng chọn loại ngân sách!", "-fx-text-fill: red;");
                return;
            }
            try {
                int categoryId = budgetServices.getIdByName(selectedCate); // Giả sử bạn có hàm này
                if (budgetServices.isBudgetExistForCurrentMonth(categoryId, true)) {
                    budgetServices.updateStatus(lblStatus, "❌ Ngân sách cho danh mục này đã tồn tại trong tháng!", "-fx-text-fill: red;");
                    return;
                }
                budgetServices.insertBudgetTODB(txtInputBudget.getText(), selectedType, selectedCate);
            } catch (SQLException ex) {
                budgetServices.updateStatus(lblStatus, "❌ Lỗi kết nối CSDL", "-fx-text-fill: red;");
                ex.printStackTrace(); //In ra lỗi console để debug
            }
        });

    }

    private void loadCategoriesByType(String type) {
        try {
            // Gọi phương thức getCateByTypeFromBudget thay vì getCateByType
            List<Category> categories = categoryServices.getCateByTypeFromBudget(type); // Gọi phương thức từ CategoryServices

            // Chuyển đổi danh sách Category thành danh sách tên category
            List<String> categoryNames = categories.stream()
                    .map(Category::getName) // Lấy tên từ đối tượng Category
                    .collect(Collectors.toList()); // Thu thập vào danh sách

            // Đổ danh sách tên vào ComboBox
            cbCate.setItems(FXCollections.observableArrayList(categoryNames));
        } catch (SQLException ex) {
            // Cập nhật thông báo lỗi nếu không thể tải danh mục
            budgetServices.updateStatus(lblStatus, "❌ Không thể tải danh mục", "-fx-text-fill: red;");
            ex.printStackTrace(); // In ra lỗi console để debug
        }
    }

    public void switchToEditBudget(MouseEvent event) throws IOException {
        App.setRoot("editbudget");
    }

    public void loadColumns() {
        // Cột Số tiền
        TableColumn<Budget, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountBudget")); // Lấy giá trị từ thuộc tính amountBudget
        colAmount.setPrefWidth(150);

        // Cột Loại
        TableColumn<Budget, String> colType = new TableColumn<>("Loại");
        colType.setCellValueFactory(new PropertyValueFactory<>("type")); // Lấy giá trị từ thuộc tính type
        colType.setPrefWidth(100);

        // Cột Danh mục
        TableColumn<Budget, String> colCateName = new TableColumn<>("Danh mục");

        colCateName.setCellFactory(column -> new TableCell<Budget, String>() {
            @Override
            protected void updateItem(String categoryName, boolean empty) {
                super.updateItem(categoryName, empty);
                if (getTableRow() != null) {
                    System.out.println("TableRow is not null");
                    System.out.println("TableRow Item: " + getTableRow().getItem()); // In ra item của TableRow
                } else {
                    System.out.println("TableRow is null");
                }
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
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate")); // Lấy giá trị từ thuộc tính date
        colDate.setPrefWidth(160);

        // Cột Hành động
        TableColumn<Budget, Void> colAction = new TableColumn<>("Hành động");
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("Sửa");
            final HBox btnGroup = new HBox(5, btnEdit);

            {
                btnEdit.setOnMouseClicked(e -> {
                    try {
                        switchToEditBudget(e);
                    } catch (IOException ex) {
                        Logger.getLogger(SelectModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGroup);
                }
            }
        });

        // Thêm các cột vào bảng
        tbBudget.getColumns().addAll(colAmount, colType, colCateName, colDate, colAction);
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
    
    
    
//    private void loadTableData() {
//        List<Budget> budgets = budgetServices.getBudgetsForCurrentUser();
//
//        // Kiểm tra dữ liệu trong budgets
//        if (budgets != null && !budgets.isEmpty()) {
//            for (Budget budget : budgets) {
//                if (budget == null) {
//                    System.out.println("Found a null budget object.");
//                    continue; // Bỏ qua đối tượng null
//                }
//
//                // Kiểm tra các thuộc tính của Budget
//                if (budget.getCategoryId() == 0 || budget.getType() == null) {
//                    System.out.println("Invalid budget data found: " + budget);
//                    continue; // Bỏ qua đối tượng invalid
//                }
//            }
//
//            tbBudget.setItems(FXCollections.observableList(budgets)); // Thêm dữ liệu hợp lệ vào TableView
//        } else {
//            System.out.println("No data to load into the table.");
//        }
//    }

}
