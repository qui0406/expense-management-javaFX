/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Budget;
import com.qlct.pojo.Category;
import com.qlct.pojo.User;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

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
    @FXML
    Label lblTotal;
    @FXML
    Label lblTotalMoneyUsed;
    @FXML
    ProgressBar progressBarTotal;
    @FXML
    ProgressBar progressBarUsed;
    @FXML
    Label lblPercentage;
    @FXML
    private Text txtUsername;
    @FXML
    private Button btAddTrans;
    @FXML
    private Button btBCTK;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        budgetServices.setLblStatus(lblStatus);
        budgetServices.setLblTotal(lblTotal);
        budgetServices.setLblTotalMoneyUsed(lblTotalMoneyUsed);

        double currentUsed = budgetServices.getTotalUsed();
        System.out.println("Tiền đã chi" + currentUsed);
        updateBudgetProgress(progressBarTotal, progressBarUsed, budgetServices.getTotalBudget(), budgetServices.getTotalUsed());

        cbType.setItems(FXCollections.observableArrayList("Chi"));

        cbType.setOnAction(e -> {
            String selectedType = cbType.getValue(); // Lấy giá trị đã chọn từ ComboBox
            if (selectedType != null) {
                loadCategoriesByType(selectedType);
            }
        });

        // Hiển thị username của người dùng hiện tại
        User currentUser = UserSessionServices.getCurrentUser();
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
        } else {
            txtUsername.setText("Chưa đăng nhập");
        }

//        btAddTrans.setOnAction(e -> {
//            try {
//                switchTransaction();
//            } catch (IOException ex) {
//                Logger.getLogger(BudgetController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
//
//        btBCTK.setOnAction(e -> {
//            try {
//                switchBCTK();
//            } catch (IOException ex) {
//                Logger.getLogger(BudgetController.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
        loadTableData();
        loadColumns();

        btnAddBudget.setOnAction(e -> {
            String selectedType = cbType.getValue(); // Lấy giá trị đã chọn từ ComboBox
            String selectedCate = cbCate.getValue();
            String inputText = txtInputBudget.getText();
            if (selectedType == null) {
                budgetServices.updateStatus(lblStatus, "❌ Vui lòng chọn loại ngân sách!", "-fx-text-fill: red;");
                return;
            }
            if (selectedCate == null) {
                budgetServices.updateStatus(lblStatus, "❌ Vui lòng chọn danh mục!", "-fx-text-fill: red;");
                return;
            }
            double validatedAmount = validateInput(inputText);
            if (validatedAmount <= 0) {
                return;
            }
            try {
                int categoryId = budgetServices.getIdByName(selectedCate);
                if (budgetServices.isBudgetExistForCurrentMonth(categoryId, true)) {
                    budgetServices.updateStatus(lblStatus, "❌ Ngân sách cho danh mục này đã tồn tại trong tháng!", "-fx-text-fill: red;");
                    return;
                }
                budgetServices.insertBudgetTODB(inputText, selectedType, selectedCate);
                handleAddBudget(inputText, selectedType, selectedCate);
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

    public void switchToEditBudget(MouseEvent event, Budget selectedBudget) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/qlct/quanlychitieu/editbudget.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(loader.load());
        EditBudgetController controller = loader.getController();
        controller.setEditingBudget(selectedBudget);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
//        dialog.initModality(Modality.APPLICATION_MODAL);
//        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.showAndWait();
    }

    public void loadColumns() {
        // Cột Số tiền
        TableColumn<Budget, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amountBudget")); // Lấy giá trị từ thuộc tính amount
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
                        String name = categoryServices.getCategoryByIdForBudget(categoryId);
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
                    Budget selectedBudget = getTableView().getItems().get(getIndex());
                    try {
                        switchToEditBudget(e, selectedBudget);
                        loadTableData();
                    } catch (IOException ex) {
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

    public void updateBudgetProgress(ProgressBar progressBarTotal, ProgressBar progressBarUsed, double totalBudget, double spentAmount) {
        // Đảm bảo ngân sách không bị chia 0
        if (totalBudget <= 0) {
            progressBarUsed.setProgress(0);
            progressBarUsed.setStyle("-fx-accent: grey;");
            lblPercentage.setText("0%");
            return;
        }

        // Tính tỷ lệ phần trăm đã chi
        double ratio = spentAmount / totalBudget;
        ratio = Math.min(ratio, 1.0); // Giới hạn max là 100%

        // Set tiến trình
        progressBarTotal.setProgress(1.0); // Luôn là 100%
        progressBarUsed.setProgress(ratio);

        // Set màu
        if (!budgetServices.isBudgetWithinLimit(spentAmount, totalBudget)) {
            showBudgetWarning();
            progressBarUsed.setStyle("-fx-accent: red;");
        } else {
            progressBarUsed.setStyle("-fx-accent: green;");
        }

        if (lblPercentage != null) {
            int percent = (int) Math.round(ratio * 100);
            lblPercentage.setText(percent + "% đã chi");
        }
    }

    private void showBudgetWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Cảnh báo ngân sách");
        alert.setHeaderText(null);
        alert.setContentText("Chi tiêu đã vượt quá 80% ngân sách!\nVui lòng kiểm tra lại kế hoạch chi tiêu.");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                try {
                    // Gọi hàm chuyển sang trang Budget
                    switchToBudgetScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void switchToBudgetScreen() throws IOException {
        App.setRoot("budget", 935, 750);
    }

    public void updateStatus(Label label, String message, String style) {
        Platform.runLater(() -> {
            label.setText(message);
            label.setStyle(style);
        });
    }

    public double validateInput(String input) {
        try {
            return budgetServices.validateAmount(input);
        } catch (IllegalArgumentException e) {
            Platform.runLater(() -> {
                updateStatus(lblStatus, e.getMessage(), "-fx-text-fill: red;");
            });
            return 0.0;
        }
    }

    public void handleAddBudget(String input, String budgetType, String categoryName) {
        try {
            boolean success = budgetServices.tryInsertBudget(input, budgetType, categoryName);
            if (success) {
                updateStatus(lblStatus, "✅ Thêm ngân sách thành công!", "-fx-text-fill: green;");
                loadTableData();
            } else {
                updateStatus(lblStatus, "❌ Thêm ngân sách thất bại!", "-fx-text-fill: red;");
            }
        } catch (Exception e) {
            updateStatus(lblStatus, "⚠️ Dữ liệu không hợp lệ!", "-fx-text-fill: orange;");
        }
    }

    private void switchTransaction() throws IOException {
        App.setRoot("FXTransactions", 935, 750);
    }

    private void switchBCTK() throws IOException {
        App.setRoot("baocaothongketheobang", 935, 750);
    }

}