/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

//import com.mysql.cj.util.Util;
import com.qlct.pojo.Category;
import com.qlct.pojo.Transaction;
import com.qlct.pojo.User;
import com.qlct.services.CategoryServices;
import com.qlct.services.TransactionServices;
import com.qlct.services.UserCategoryServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ttthu
 */
public class FXTransactionsController implements Initializable {

    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtNote;
    @FXML
    private DatePicker btDate;
    @FXML
    private ComboBox<String> cbType;
    @FXML
    private ComboBox<Category> cbCates;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private TableView<Transaction> tbTransaction;
    @FXML
    private Button btnEditCategory;
    @FXML
    private ComboBox<String> cbFilterType;
    @FXML
    private DatePicker dpFilterDate;
    @FXML
    private Button btnFilter;
    @FXML
    private Text txtUsername;
    @FXML
    private Text txtTotalAmount;
    @FXML
    private Text txtExpenseAmount;
    @FXML
    private Button logoutBtnTransaction;
    /**
     * Initializes the controller class.
     */
    private Map<Integer, Category> categoryCache = new HashMap<>();
    
    private UserCategoryServices userCategoryServices = new UserCategoryServices();
    
    private List<Integer> listDefaultCategory = new ArrayList<>();
    private static final int DEFAULT_CATEGORY = 6;



    @Override
    public void initialize(URL url, ResourceBundle rb) {

        CategoryServices catService = new CategoryServices();
        cbType.setItems(FXCollections.observableArrayList("Chi", "Thu"));

        cbFilterType.setItems(FXCollections.observableArrayList("Ngày", "Tuần", "Tháng", "Quý", "Năm"));
        dpFilterDate.setValue(LocalDate.now());
        
        try {
            addCategoryDefault();
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Tải danh mục vào cache
        try {
            categoryCache.clear();
            List<Category> categories = catService.getCatetogires();
            for (Category c : categories) {
                categoryCache.put(c.getId(), c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Hiển thị username của người dùng hiện tại
        User currentUser = UserSessionServices.getCurrentUser();
        if (currentUser != null) {
            txtUsername.setText(currentUser.getUsername());
        } else {
            txtUsername.setText("Chưa đăng nhập");
        }

        btDate.setValue(LocalDate.now());
        this.loadColumns();
        this.loadTableData();
        logoutBtnTransaction.setOnAction(event -> Utils.logout(logoutBtnTransaction, "login.fxml"));
        btnSave.setOnAction(this::addHandler);

    }
    
    public void addCategoryDefault() throws SQLException{
        listDefaultCategory = userCategoryServices.getListDefaultCategory();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        
        System.out.println(currentUserId);
        System.out.println(listDefaultCategory);
        for (int x : listDefaultCategory){
            if(userCategoryServices.isUserDefaultCategoryExists(currentUserId, x)==0){
                userCategoryServices.assignDefaultCategoriesToUser(currentUserId, x);
            }
        }
    }

    public void filterTransactions(ActionEvent e) {
        TransactionServices tranService = new TransactionServices();

        String filterType = cbFilterType.getSelectionModel().getSelectedItem();
        LocalDate filterDate = dpFilterDate.getValue();

        if (filterType == null || filterDate == null) {
            Utils.getAlert("Vui lòng chọn kiểu lọc và ngày!").show();
            return;
        }

        LocalDate startDate;
        LocalDate endDate;

        switch (filterType) {
            case "Ngày":
                startDate = filterDate;
                endDate = filterDate;
                break;
            case "Tuần":
                startDate = filterDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                endDate = startDate.plusDays(6);
                break;
            case "Tháng":
                startDate = filterDate.withDayOfMonth(1);
                endDate = filterDate.withDayOfMonth(filterDate.lengthOfMonth());
                break;
            case "Quý":
                int quarter = (filterDate.getMonthValue() - 1) / 3 + 1;
                startDate = LocalDate.of(filterDate.getYear(), (quarter - 1) * 3 + 1, 1);
                endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());
                break;
            case "Năm":
                startDate = LocalDate.of(filterDate.getYear(), 1, 1);
                endDate = LocalDate.of(filterDate.getYear(), 12, 31);
                break;
            default:
                return;
        }

        try {
            List<Transaction> filteredTransactions = tranService.getTransactionsByDateRange(startDate, endDate);
//            tbTransaction.setItems(FXCollections.observableList(filteredTransactions));
            ObservableList<Transaction> transactions = FXCollections.observableList(filteredTransactions);
            tbTransaction.setItems(transactions);
            updateTotalAmount(transactions);
        } catch (SQLException ex) {
            Utils.getAlert("Lỗi khi lọc giao dịch: " + ex.getMessage()).show();
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateCategoryCombobox(ActionEvent e) {
        CategoryServices catService = new CategoryServices();
        String selectedType = cbType.getSelectionModel().getSelectedItem();

        if (selectedType != null) {
            try {
                cbCates.setItems(FXCollections.observableArrayList(catService.getCateByType(selectedType)));
            } catch (SQLException ex) {
                Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadTableData() {
        TransactionServices tranService = new TransactionServices();

        try {
//            this.tbTransaction.setItems(FXCollections.observableList(tranService.getAllTran()));
            ObservableList<Transaction> transactions = FXCollections.observableList(tranService.getAllTran());
            this.tbTransaction.setItems(transactions);
            updateTotalAmount(transactions);
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateTotalAmount(ObservableList<Transaction> transactions) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        double incomeAmount = transactions.stream()
                .filter(t -> {
                    Category category = categoryCache.get(t.getCategoryId());
                    return category != null && "Thu".equals(category.getType());
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expenseAmount = transactions.stream()
                .filter(t -> {
                    Category category = categoryCache.get(t.getCategoryId());
                    return category != null && "Chi".equals(category.getType());
                })
                .mapToDouble(Transaction::getAmount)
                .sum();

        // Cập nhật tổng Thu
        if (incomeAmount > 0) {
            txtTotalAmount.setText(currencyFormat.format(incomeAmount) + " VND");
            txtTotalAmount.setVisible(true);
        } else {
            txtTotalAmount.setVisible(false); // Ẩn nếu bằng 0
        }

        // Cập nhật tổng Chi
        if (expenseAmount > 0) {
            txtExpenseAmount.setText(currencyFormat.format(expenseAmount) + " VND");
            txtExpenseAmount.setVisible(true);
        } else {
            txtExpenseAmount.setVisible(false); // Ẩn nếu bằng 0
        }
    }

    public void loadColumns() {
        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(100);

        TableColumn<Transaction, String> colNote = new TableColumn<>("Ghi chú");
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colNote.setPrefWidth(140);

        TableColumn<Transaction, LocalDate> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(100);

        TableColumn<Transaction, Integer> colCate = new TableColumn<>("Danh mục");
        colCate.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCate.setCellFactory(column -> new TableCell<Transaction, Integer>() {
            @Override
            protected void updateItem(Integer categoryId, boolean empty) {
                super.updateItem(categoryId, empty);
                if (empty || categoryId == null) {
                    setText(null);
                } else {
                    Category category = categoryCache.get(categoryId);
                    setText(category != null ? category.getName() : "Không xác định");
                }
            }
        });
        colCate.setPrefWidth(120);

        TableColumn<Transaction, Void> colAction = new TableColumn<>();
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnDelete = new Button("Xóa");
            final Button btnEdit = new Button("Sửa");
            final HBox btnGroup = new HBox(5, btnEdit, btnDelete);

            {
                btnDelete.setOnAction(evt -> {
                    Transaction tran = getTableView().getItems().get(getIndex());

                    // Thêm hộp thoại xác nhận
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Xác nhận xóa");
                    confirmation.setHeaderText("Bạn có chắc chắn muốn xóa giao dịch này?");
                    confirmation.setContentText("Số tiền: " + tran.getAmount() + "\n"
                            + "Ngày: " + tran.getDate() + "\n"
                            + "Ghi chú: " + tran.getNote());

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            TransactionServices ts = new TransactionServices();
                            ts.deleteTransaction(tran.getIdTran());
                            Utils.getAlert("Xóa thành công!").show();
                            loadTableData();
                        }
                    });
                });

                btnEdit.setOnAction(evt -> {
                    Transaction tran = getTableView().getItems().get(getIndex());
                    txtAmount.setText(String.valueOf(tran.getAmount()));
                    txtNote.setText(tran.getNote());
                    btDate.setValue(tran.getDate());

                    // Chọn danh mục phù hợp
                    cbCates.getSelectionModel().select(new Category(tran.getCategoryId()));

                    // Gán sự kiện cập nhật
                    btnSave.setOnAction(event -> updateTransaction(tran.getIdTran()));
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

        tbTransaction.getColumns().addAll(colCate, colAmount, colNote, colDate, colAction);

        // Áp dụng CSS động cho các hàng
        tbTransaction.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    getStyleClass().removeAll("expense", "income");
                } else {
                    Category category = categoryCache.get(item.getCategoryId());
                    if (category != null) {
                        getStyleClass().removeAll("expense", "income");
                        if ("Chi".equals(category.getType())) {
                            getStyleClass().add("expense");
                        } else if ("Thu".equals(category.getType())) {
                            getStyleClass().add("income");
                        }
                    }
                }
            }
        });
    }

    private void updateTransaction(int transactionId) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            String note = txtNote.getText();
            LocalDate date = btDate.getValue();
            Category selectedCategory = cbCates.getSelectionModel().getSelectedItem();
            TransactionServices ts = new TransactionServices();
            ts.updateTransaction(transactionId, amount, selectedCategory.getId(), date, note, UserSessionServices.getCurrentUser().getId());

            Utils.getAlert("Cập nhật thành công!").show();
            clearForm();
            loadTableData();
        } catch (NumberFormatException ex) {
            Utils.getAlert("Số tiền phải là một số hợp lệ!").show();
        } catch (IllegalArgumentException ex) {
            Utils.getAlert(ex.getMessage()).show();
        }
    }

    public void addHandler(ActionEvent e) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            String note = txtNote.getText();
            LocalDate date = btDate.getValue();
            Category selectedCategory = cbCates.getSelectionModel().getSelectedItem();

            if (selectedCategory == null) {
                Utils.getAlert("Vui lòng chọn danh mục!").show();
                return;
            }
            TransactionServices ts = new TransactionServices();
            Transaction transaction = new Transaction(amount, selectedCategory.getId(),
                    date, note, UserSessionServices.getCurrentUser().getId());
            ts.addTransaction(transaction);

            Utils.getAlert("Add successful!").show();
            clearForm();
            loadTableData();
        } catch (NumberFormatException ex) {
            Utils.getAlert("Số tiền phải là một số hợp lệ!").show();
        } catch (IllegalArgumentException ex) {
            Utils.getAlert(ex.getMessage()).show();
        }
    }

    private void clearForm() {
        cbType.setValue(null);
        cbCates.setValue(null);
        txtAmount.clear();
        txtNote.clear();
        btDate.setValue(LocalDate.now());
        btnSave.setOnAction(this::addHandler);
    }

    public void clearHandler(ActionEvent e) {
        clearForm();
    }

    @FXML
    private void switchBCTK() throws IOException {
        App.setRoot("baocaothongketheobang", 1200, 800);
    }

    @FXML
    public void switchBudget() throws IOException {
        App.setRoot("budget", 1200, 800);
    }

    public void editCategory(MouseEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXEditCategory.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((ImageView) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
        CategoryServices catService = new CategoryServices();
        // Cập nhật cache và bảng sau khi quay lại
        try {
            categoryCache.clear();
            for (Category c : catService.getCatetogires()) {
                categoryCache.put(c.getId(), c);
            }
            loadTableData();
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
