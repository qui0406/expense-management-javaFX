/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Expense;
import com.qlct.services.StatsServices;
import com.qlct.services.UserSessionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author QUI
 */
public class StatsController implements Initializable {

    @FXML
    public  ComboBox<String> cbChoseTable;
    @FXML
     public DatePicker fromDate;
    @FXML
     public DatePicker toDate;
    @FXML
    public Button filter;
    @FXML
    public TableView<Expense> tbExpenses;
    
    @FXML public TableView<Expense> tbStatsByMonth;
    
    public StatsServices statsServices;
    
    @FXML
    public  ComboBox<String> cbType;
    
    @FXML public Label lbUsername;
    
    @FXML public Button btnSwitchAddTransaction;
    
    @FXML public Button btnSwitchBudget;

    
    public StatsController() {
        this.statsServices = new StatsServices(); // Khởi tạo service
    }

    public void loadComboBoxTableCurrentMonth() {
        cbChoseTable.setValue("Thống kê theo bảng");
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add("Thống kê theo bảng");
        choiceBox.add("Thống kê theo biểu đồ");
        cbChoseTable.setItems(FXCollections.observableList(choiceBox));
        cbChoseTable.setValue("Thống kê theo bảng");
    }
    
    public void loadComboBoxType(){
        cbType.setValue("Chi");
        List<String> choice = new ArrayList<>();
        choice.add("Chi");
        choice.add("Thu");
        cbType.setItems(FXCollections.observableList(choice));
        cbType.setValue("Chi");
    }
    
    @FXML
    public void switchTransaction() throws IOException{
        App.setRoot("FXTransactions", 900, 700);
    }
    
    @FXML 
    public void switchBudget() throws IOException{
        App.setRoot("budget", 900, 700);
    }

 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utils.loadUsername(lbUsername);
        try {
            if (cbChoseTable == null) {
                System.out.println("cbChoseTable is null - check your FXML file!");
            } else {
                loadComboBoxType();
                loadComboBoxTableCurrentMonth();
                cbChoseTable.setOnAction(event -> handleComboBoxTable());
               
                
            }
            loadColumnsCurrentMonth();
            
            loadData(statsServices.getExpenseCurrentMonth(cbType.getValue())); // Gọi loadData()
            
            // Khi bam vao chon loai thu/chi
            cbType.setOnAction(event -> {
                try {
                    fromDate.setValue(null); // Reset fromDate
                    toDate.setValue(null); // Reset toDate
                    loadData(statsServices.getExpenseCurrentMonth(cbType.getValue()));
                  
                    // Cập nhật bảng thống kê theo tháng
                    loadDataByMonth();
                   
                } catch (SQLException ex) {
                    Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            
            
            // Gán sự kiện bấm nút lọc sau khi khối try-catch kết thúc
            filter.setOnAction(event -> {
                try {
                    filterDate(event);
                } catch (SQLException ex) {
                    Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            
            
            loadColumnsByMonth();
            loadData(statsServices.getExpenseCurrentMonth(cbType.getValue()));
            
            loadDataByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public void handleComboBoxTable() {
        String selectedOption = cbChoseTable.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            System.out.println(selectedOption);
            switchSceneTable("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            System.out.println(selectedOption);
            switchSceneTable("baocaothongketheobieudo.fxml");
        }
    }

    public void switchSceneTable(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) cbChoseTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { // In ra chi tiết lỗi
            // In ra chi tiết lỗi
            System.out.println("Failed to load FXML: " + e);
        }
    }

    public void loadColumnsCurrentMonth() {
        TableColumn<Expense, String> colCategoryId = new TableColumn<>("Số thứ tự");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);
        
        TableColumn<Expense, String> colCategoryName = new TableColumn<>("Danh mục");
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colCategoryName.setPrefWidth(200);

        TableColumn<Expense, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(300);
        
        colAmount.setCellFactory(column -> new TableCell<Expense, Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount));
                }
            }
        });

        tbExpenses.getColumns().addAll(colCategoryId, colCategoryName, colAmount);
    }
    
    public void loadData(List<Expense> expenseList) {
        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        tbExpenses.setItems(expenses);
    }
    
    public void filterDate(Event e) throws SQLException{
        LocalDate selectedFromDate = fromDate.getValue();
        LocalDate selectedToDate = toDate.getValue();
        
        if (selectedFromDate != null && selectedToDate != null) {
            List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate, cbType.getValue());
            loadData(filteredExpenses);
        } else {
            System.out.println("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");
            Utils.showAlert("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!", null, "Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");
        }
    }
    
    
    
    public void loadColumnsByMonth() {
        TableColumn<Expense, String> colCategoryId = new TableColumn<>("Số thứ tự");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);
        
        TableColumn<Expense, String> colCategoryName = new TableColumn<>("Tháng");
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colCategoryName.setPrefWidth(200);

        TableColumn<Expense, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(300);
        
        colAmount.setCellFactory(column -> new TableCell<Expense, Double>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(amount));
                }
            }
        });

        tbStatsByMonth.getColumns().addAll(colCategoryId, colCategoryName, colAmount);
    }
    
    public void loadDataByMonth() {
        try {
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(cbType.getValue());
            ObservableList<Expense> expenses = FXCollections.observableArrayList(expensesByMonth);
            tbStatsByMonth.setItems(expenses);
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
    