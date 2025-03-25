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
        cbType.getSelectionModel().selectFirst();
//        try {
//            this.cbCates.setItems(FXCollections.observableList(catService.getCatetogires()));
//        } catch (SQLException ex) {
//            Logger.getLogger(FXTransactionsController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        cbType.setOnAction(event -> updateCategoryCombobox());
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
        TableColumn colAmount = new TableColumn("So tien");
        colAmount.setCellValueFactory(new PropertyValueFactory("amount"));
        colAmount.setPrefWidth(100);

        TableColumn colNote = new TableColumn("Ghi chu");
        colNote.setCellValueFactory(new PropertyValueFactory("note"));
        colNote.setPrefWidth(150);

        TableColumn colDate = new TableColumn("Ngay");
        colDate.setCellValueFactory(new PropertyValueFactory("date"));
        colDate.setPrefWidth(150);

        TableColumn colCate = new TableColumn("Danh muc");
        colCate.setCellValueFactory(new PropertyValueFactory("categoryId"));
        colCate.setPrefWidth(150);
        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(e -> {
            Button btn = new Button("Xóa");
            btn.setOnAction(evt -> {
//                int id = (int) ((TableRow)((Button) evt.getSource()).getParent().getParent()).getItem();
                Transaction tran = (Transaction) ((TableRow)((Button) evt.getSource()).getParent().getParent()).getItem();
                int id = tran.getIdTran();
                TransactionServices ts = new TransactionServices();
                ts.deleteTransaction(id);
                Utils.getAlert("Delete successful!").show();
                loadTableData();
            });
            
            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });
//        TransactionServices ts = new TransactionServices();

//        TableColumn<Transaction, Void> colAction = new TableColumn<>();
//        colAction.setCellFactory(col -> new TableCell<Transaction, Void>() {
//            Button btn = new Button("Xóa");
//
//            {
//                btn.setOnAction(event -> {
//                    Transaction tran = getTableView().getItems().get(getIndex());
//                    if (tran != null) {
//                        ts.deleteTransaction(tran.getIdTran());
//                        Utils.getAlert("Delete successful!").show();
//                        loadTableData();
//                    }
//
//                });
//
//            }
//
//        }
      
        tbTransaction.getColumns().addAll(colCate, colAmount, colNote, colDate, colAction);
    }

    public void addHandler(ActionEvent e) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            String note = txtNote.getText();
            LocalDate date = btDate.getValue();
            Category selectedCategory = cbCates.getSelectionModel().getSelectedItem();

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
