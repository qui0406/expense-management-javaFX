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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private ComboBox<String> cbChoseTable;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private Button filter;
    @FXML
    private TableView<Expense> tbExpenses;
    
    @FXML private TableView<Expense> tbStatsByMonth;
    
    private StatsServices statsServices;
    
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

 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (cbChoseTable == null) {
                System.out.println("cbChoseTable is null - check your FXML file!");
            } else {
                loadComboBoxTableCurrentMonth();
                cbChoseTable.setOnAction(event -> handleComboBoxTable());
            }

            loadColumnsCurrentMonth();
            loadData(statsServices.getExpenseCurrentMonth()); // Gọi loadData()

        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Gán sự kiện bấm nút lọc sau khi khối try-catch kết thúc
        filter.setOnAction(event -> {
            try {
                filterDate(event);
            } catch (SQLException ex) {
                Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        
        loadColumnsByMonth();
        try {
            loadData(statsServices.getExpenseCurrentMonth());
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadDataByMonth(LocalDate.now().getYear());

    }


    private void handleComboBoxTable() {
        String selectedOption = cbChoseTable.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            switchSceneTable("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            System.out.println(selectedOption);
            switchSceneTable("baocaothongketheobieudo.fxml");
        }
    }

    private void switchSceneTable(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) cbChoseTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
        }
    }

    private void loadColumnsCurrentMonth() {
        TableColumn<Expense, String> colCategoryId = new TableColumn<>("Số thứ tự");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);
        
        TableColumn<Expense, String> colCategoryName = new TableColumn<>("Danh mục");
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colCategoryName.setPrefWidth(200);

        TableColumn<Expense, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(300);

        tbExpenses.getColumns().addAll(colCategoryId, colCategoryName, colAmount);
    }
    
    private void loadData(List<Expense> expenseList) {
        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        tbExpenses.setItems(expenses);
    }
    
    private void filterDate(Event e) throws SQLException{
        LocalDate selectedFromDate = fromDate.getValue();
        LocalDate selectedToDate = toDate.getValue();
        
        if (selectedFromDate != null && selectedToDate != null) {
            List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate);
            loadData(filteredExpenses);
        } else {
            System.out.println("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");
        }
    }
    
    
    
    private void loadColumnsByMonth() {
        TableColumn<Expense, String> colCategoryId = new TableColumn<>("Số thứ tự");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);
        
        TableColumn<Expense, String> colCategoryName = new TableColumn<>("Tháng");
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colCategoryName.setPrefWidth(200);

        TableColumn<Expense, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(300);

        tbStatsByMonth.getColumns().addAll(colCategoryId, colCategoryName, colAmount);
    }
    
    private void loadDataByMonth(int year) {
    try {
        List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(year);
        ObservableList<Expense> expenses = FXCollections.observableArrayList(expensesByMonth);
        tbStatsByMonth.setItems(expenses);
    } catch (SQLException ex) {
        Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
    }
}


}
