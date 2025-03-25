/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qlct.quanlychitieu;

//import com.mysql.cj.util.Util;
import com.qlct.pojo.Category;
import com.qlct.pojo.Transaction;
import com.qlct.services.CategoryServices;
import com.qlct.services.TransactionServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.layout.HBox;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CategoryServices catService = new CategoryServices();
        cbType.setItems(FXCollections.observableArrayList("Chi", "Thu"));

        btDate.setValue(LocalDate.now());
        this.loadColumns();
        this.loadTableData();
        List<Category> categories = null;
        try {
            categories = catService.getCatetogires();
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Categories: " + categories);

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
            this.tbTransaction.setItems(FXCollections.observableList(tranService.getAllTran()));
        } catch (SQLException ex) {
            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumns() {
        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(100);

        TableColumn<Transaction, String> colNote = new TableColumn<>("Ghi chú");
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colNote.setPrefWidth(150);

        TableColumn<Transaction, LocalDate> colDate = new TableColumn<>("Ngày");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setPrefWidth(130);

        TableColumn<Transaction, Integer> colCate = new TableColumn<>("Danh mục");
        colCate.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCate.setPrefWidth(150);

        TableColumn<Transaction, Void> colAction = new TableColumn<>();
        colAction.setCellFactory(tc -> new TableCell<>() {
            final Button btnDelete = new Button("Xóa");
            final Button btnEdit = new Button("Sửa");
            final HBox btnGroup = new HBox(5, btnEdit, btnDelete);

            {
                btnDelete.setOnAction(evt -> {
                    Transaction tran = getTableView().getItems().get(getIndex());
                    int id = tran.getIdTran();
                    TransactionServices ts = new TransactionServices();
                    ts.deleteTransaction(id);
                    Utils.getAlert("Xóa thành công!").show();
                    loadTableData();
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
    }

    private void updateTransaction(int transactionId) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            String note = txtNote.getText();
            LocalDate date = btDate.getValue();
            Category selectedCategory = cbCates.getSelectionModel().getSelectedItem();

            if (amount <= 0 || amount > 1_000_000_000_000.0) {
                Utils.getAlert("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!").show();
                return;
            }
            if (date.isAfter(LocalDate.now())) {
                Utils.getAlert("Ngày giao dịch không được vượt quá ngày hiện tại!").show();
                return;
            }
            if (date == null || selectedCategory == null) {
                Utils.getAlert("Vui lòng điền đầy đủ thông tin!").show();
                return;
            }

            TransactionServices ts = new TransactionServices();
            ts.updateTransaction(transactionId, amount, selectedCategory.getId(), date, note);

            Utils.getAlert("Cập nhật thành công!").show();
            clearForm();
            loadTableData();
        } catch (NumberFormatException ex) {
            Utils.getAlert("Số tiền phải là một số hợp lệ!").show();
        }
    }

    public void addHandler(ActionEvent e) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            String note = txtNote.getText();
            LocalDate date = btDate.getValue();
            Category selectedCategory = cbCates.getSelectionModel().getSelectedItem();

            if (amount <= 0 || amount > 1_000_000_000_000.0) {
                Utils.getAlert("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!").show();
                return;
            }
            if (date.isAfter(LocalDate.now())) {
                Utils.getAlert("Ngày giao dịch không được vượt quá ngày hiện tại!").show();
                return;
            }
            if (date == null || selectedCategory == null) {
                Utils.getAlert("Vui lòng điền đầy đủ thông tin!").show();
                return;
            }
            TransactionServices ts = new TransactionServices();
            Transaction transaction = new Transaction(amount, selectedCategory.getId(),
                    date, note);
            ts.addTransaction(transaction);

            Utils.getAlert("Add successful!").show();
            clearForm();
            loadTableData();
        } catch (NumberFormatException ex) {
            Utils.getAlert("Số tiền phải là một số hợp lệ!").show();
        }
    }

    private void clearForm() {
        cbType.setValue(null);
        cbCates.setValue(null);
        txtAmount.clear();
        txtNote.clear();
        btDate.setValue(LocalDate.now());
    }

    public void clearHandler(ActionEvent e) {
        clearForm();
    }
}
