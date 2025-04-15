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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author QUI
 */
public class ChartController implements Initializable{
    @FXML public ComboBox<String> cbChoseChart;
    
    @FXML public ComboBox<String> cbTypeChart;

    @FXML public PieChart pieChart;
    @FXML public LineChart lineChart;
    @FXML public BarChart barChart;
    
    @FXML
    public DatePicker fromDateChart;
    @FXML
    public DatePicker toDateChart;
    @FXML
    public Button btnFilterChart;
    
    @FXML public Label lbUsernameChart;
    
    @FXML public ImageView imgViewChart;
    
    public final StatsServices statsServices;
    
   @FXML
    public void switchTransaction() throws IOException{
        App.setRoot("FXTransactions", 900, 700);
    }
    
    @FXML 
    public void switchBudget() throws IOException{
        App.setRoot("budget", 900, 700);
    }
    
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
        Utils.loadUsername(lbUsernameChart);
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
                    
                    loadDataByMonth();
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
    
    public void filterDate(Event e) throws SQLException{
        LocalDate selectedFromDate = fromDateChart.getValue();
        LocalDate selectedToDate = toDateChart.getValue();
        
        if (selectedFromDate != null && selectedToDate != null) {
            List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate, cbTypeChart.getValue());
            loadData(filteredExpenses);
        } else {
            Utils.showAlert("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!", null, "Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");

        }
    }
    
    public void handleComboBoxChart() {
        String selectedOption = cbChoseChart.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            switchSceneChart("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            System.out.println(selectedOption);
            switchSceneChart("baocaothongketheobieudo.fxml");
        }
    }
    
    public void switchSceneChart(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) cbChoseChart.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            
        }
    }
     
    public void loadPieChart(List<Expense> expenses) throws SQLException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        for (Expense expense : expenses) {
            pieChartData.add(new PieChart.Data(expense.getCategoryName(), expense.getAmount()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Biểu đồ chi tiêu");
        pieChart.setLabelsVisible(false); // ❌ Không hiển thị nhãn trực tiếp trên biểu đồ
        pieChart.setLegendVisible(true);  // ✅ Hiển thị chú thích bằng màu dưới biểu đồ
        pieChart.setMinSize(280, 280); 
        pieChart.setPrefSize(280, 280);
        
        // Nếu bạn vẫn muốn legend hiển thị phần trăm:
        for (PieChart.Data data : pieChart.getData()) {
            double percent = (data.getPieValue() / total) * 100;
            data.nameProperty().set(String.format("%s - %.1f%%", data.getName(), percent));
        }
    }

    /**
     *
     * @param expenses
     */
   
    
    public void loadBarChart(List<Expense> expenses) {
        barChart.getData().clear();
        barChart.setMinSize(330, 300); 
        barChart.setPrefSize(330, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);

            
        }

        barChart.getData().add(series);
    }
    
    public void loadLineChart(List<Expense> expenses) {
        lineChart.getData().clear();
        lineChart.setTitle("Chi tiêu theo danh mục");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chi tiêu");

        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Text dataLabel = new Text(Utils.formatCurrency(expense.getAmount()));
                    dataLabel.getStyleClass().add("chart-label");
                    newNode.parentProperty().addListener((obs2, oldParent, newParent) -> {
                        if (newParent instanceof Group) {
                            ((Group) newParent).getChildren().add(dataLabel);
                            dataLabel.layoutXProperty().bind(newNode.layoutXProperty().add(0));
                            dataLabel.layoutYProperty().bind(newNode.layoutYProperty().subtract(10));
                        }
                    });
                }
            });
        }

        lineChart.getData().add(series);
    }


    public void loadData(List<Expense> expenseList) throws SQLException {
        if (expenseList == null || expenseList.isEmpty()) {
            pieChart.setVisible(false);
            lineChart.setVisible(false);
            pieChart.getData().clear();
            lineChart.getData().clear();
            return;
        }

        pieChart.setVisible(true);
        lineChart.setVisible(true);

        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        loadPieChart(expenses);
        loadBarChart(expenseList);
    }

    
    public void loadDataByMonth() {
        try {
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(cbTypeChart.getValue());

            if (expensesByMonth == null || expensesByMonth.isEmpty()) {
                barChart.setVisible(false);
                barChart.getData().clear();
                return;
            }

            lineChart.setVisible(true);
            loadLineChart(expensesByMonth);

        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
