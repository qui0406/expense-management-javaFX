/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.quanlychitieu;

import com.qlct.pojo.Expense;
import com.qlct.services.StatsServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableLongValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

/**
 *
 * @author QUI
 */
public class ChartController implements Initializable{
    @FXML private ComboBox<String> cbChoseChart;
    
    @FXML private ComboBox<String> cbTypeChart;

    @FXML private PieChart pieChart;
    @FXML private LineChart lineChart;
    @FXML private BarChart barChart;
    
    @FXML
    private DatePicker fromDateChart;
    @FXML
    private DatePicker toDateChart;
    @FXML
    private Button btnFilterChart;
    
    private final StatsServices statsServices;
    
    public ChartController() {
        this.statsServices = new StatsServices();
    }

    public void loadComboBoxChart(){
        cbChoseChart.setValue("Thống kê theo biểu đồ");
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add("Thống kê theo biểu đồ");
        choiceBox.add("Thống kê theo bảng");
        cbChoseChart.setItems(FXCollections.observableList(choiceBox));
        cbChoseChart.setValue("Thống kê theo biểu đồ");
    }
    
    public void loadComboBoxType(){
        cbTypeChart.setValue("Chi");
        List<String> choice = new ArrayList<>();
        choice.add("Chi");
        choice.add("Thu");
        cbTypeChart.setItems(FXCollections.observableList(choice));
        cbTypeChart.setValue("Chi");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       if (cbChoseChart == null) {
        System.out.println("cbChoseTable is null - check your FXML file!");
    } else {
           loadComboBoxChart();
           loadComboBoxType();
           cbChoseChart.setOnAction(event -> handleComboBoxChart());
           
           try {
               loadData(statsServices.getExpenseCurrentMonth(cbTypeChart.getValue()));
               loadDataByMonth();
           } catch (SQLException ex) {
               Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
           }

           // Khi bam vao chon loai thu/chi
            cbTypeChart.setOnAction(event -> {
                try {
                    fromDateChart.setValue(null); // Reset fromDate
                    toDateChart.setValue(null); // Reset toDate
                    loadData(statsServices.getExpenseCurrentMonth(cbTypeChart.getValue()));
                } catch (SQLException ex) {
                    Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            
            
            // Gán sự kiện bấm nút lọc sau khi khối try-catch kết thúc
            btnFilterChart.setOnAction(event -> {
                try {
                    filterDate(event);
                } catch (SQLException ex) {
                    Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
           
           
        
        }
    }
    
    private void filterDate(Event e) throws SQLException{
        LocalDate selectedFromDate = fromDateChart.getValue();
        LocalDate selectedToDate = toDateChart.getValue();
        
        if (selectedFromDate != null && selectedToDate != null) {
            List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate, cbTypeChart.getValue());
            loadData(filteredExpenses);
        } else {
            System.out.println("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");
        }
    }
    
    private void handleComboBoxChart() {
        String selectedOption = cbChoseChart.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            switchSceneChart("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            System.out.println(selectedOption);
            switchSceneChart("baocaothongketheobieudo.fxml");
        }
    }
    
    private void switchSceneChart(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) cbChoseChart.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            
        }
    }
     
    private void loadPieChart(List<Expense> expenses) throws SQLException{
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Expense expense : expenses) {
            System.out.println(expense);
            pieChartData.add(new PieChart.Data(expense.getCategoryName(), expense.getAmount()));
        }
        pieChart.setData(pieChartData);
        pieChart.setTitle("Biểu đồ chi tiêu");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
    }
    
    private void loadLineChart(List<Expense> expenses) {
        lineChart.getData().clear();
        lineChart.setTitle("Biểu đồ xu hướng chi tiêu");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chi tiêu theo danh mục");

        for (Expense expense : expenses) {
            series.getData().add(new XYChart.Data<>(expense.getCategoryName(), expense.getAmount()));
        }
        lineChart.getData().add(series);
    }
    
    private void loadBarChart(List<Expense> expenses) {
        barChart.getData().clear();
        barChart.setTitle("Chi tiêu theo danh mục");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chi tiêu");

        for (Expense expense : expenses) {
            series.getData().add(new XYChart.Data<>(expense.getCategoryName(), expense.getAmount()));
        }

        barChart.getData().add(series);
    }

    
    private void loadData(List<Expense> expenseList) throws SQLException {
        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        loadPieChart(expenses);
        
        loadLineChart(expenseList);
        
    }
    
    
    private void loadDataByMonth() {
        try {
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(cbTypeChart.getValue());
            ObservableList<Expense> expenses = FXCollections.observableArrayList(expensesByMonth);
            
            loadBarChart(expensesByMonth);
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
