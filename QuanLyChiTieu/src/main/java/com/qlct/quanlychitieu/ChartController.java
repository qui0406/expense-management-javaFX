package com.qlct.quanlychitieu;

import com.qlct.pojo.Expense;
import com.qlct.services.StatsServices;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChartController implements Initializable {

    @FXML public ComboBox<String> cbChoseChart;
    @FXML public ComboBox<String> cbTypeChart;
    @FXML public PieChart pieChart;
    @FXML public LineChart lineChart;
    @FXML public BarChart barChart;
    @FXML public DatePicker fromDateChart;
    @FXML public DatePicker toDateChart;
    @FXML public Button btnFilterChart;
    @FXML public Label lbUsernameChart;
    @FXML public ImageView imgViewChart;
    @FXML private Button logoutChart;

    private final StatsServices statsServices;

    public ChartController() {
        this.statsServices = new StatsServices();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize username
        Utils.loadUsername(lbUsernameChart);

        loadComboBoxChart();
        loadComboBoxType();
        setupComboBoxHandlers();
        setupButtonHandlers();
        
        logoutChart.setOnAction(event -> Utils.logout(logoutChart, "login.fxml"));


        try {
            loadData(statsServices.getExpenseCurrentMonth(cbTypeChart.getValue()));
            loadDataByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Load combo box options for chart type selection
    private void loadComboBoxChart() {
        cbChoseChart.setValue("Thống kê theo biểu đồ");
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add("Thống kê theo biểu đồ");
        choiceBox.add("Thống kê theo bảng");
        cbChoseChart.setItems(FXCollections.observableList(choiceBox));
    }

    // Load combo box options for expense type selection (Chi, Thu)
    private void loadComboBoxType() {
        cbTypeChart.setValue("Chi");
        List<String> choice = new ArrayList<>();
        choice.add("Chi");
        choice.add("Thu");
        cbTypeChart.setItems(FXCollections.observableList(choice));
    }

    // Setup handlers for combo box selection
    private void setupComboBoxHandlers() {
        cbChoseChart.setOnAction(event -> handleComboBoxChart());
        cbTypeChart.setOnAction(event -> {
            try {
                fromDateChart.setValue(null);
                toDateChart.setValue(null);
                loadData(statsServices.getExpenseCurrentMonth(cbTypeChart.getValue()));
                loadDataByMonth();
            } catch (SQLException ex) {
                Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Setup button handlers
    private void setupButtonHandlers() {
        btnFilterChart.setOnAction(event -> {
            try {
                filterDate(event);
            } catch (SQLException ex) {
                Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }


    // Handle date filtering
    private void filterDate(Event e) throws SQLException {
        LocalDate selectedFromDate = fromDateChart.getValue();
        LocalDate selectedToDate = toDateChart.getValue();

        if (selectedFromDate != null && selectedToDate != null) {
            List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate, cbTypeChart.getValue());
            loadData(filteredExpenses);
        } else {
            Utils.showAlert("Vui lòng chọn cả hai ngày bắt đầu và kết thúc!", null, "Vui lòng chọn cả hai ngày bắt đầu và kết thúc!");
        }
    }

    // Handle the change of chart view
    private void handleComboBoxChart() {
        String selectedOption = cbChoseChart.getValue();
        if (selectedOption.equals("Thống kê theo bảng")) {
            switchSceneChart("baocaothongketheobang.fxml");
        } else if (selectedOption.equals("Thống kê theo biểu đồ")) {
            switchSceneChart("baocaothongketheobieudo.fxml");
        }
    }

    // Switch between different scenes based on chart selection
    private void switchSceneChart(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) cbChoseChart.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    // Load pie chart with expenses data
    public void loadPieChart(List<Expense> expenses) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        for (Expense expense : expenses) {
            pieChartData.add(new PieChart.Data(expense.getCategoryName(), expense.getAmount()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Biểu đồ chi tiêu");
        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(true);
        pieChart.setMinSize(280, 280);
        pieChart.setPrefSize(280, 280);

        for (PieChart.Data data : pieChart.getData()) {
            double percent = (data.getPieValue() / total) * 100;
            data.nameProperty().set(String.format("%s - %.1f%%", data.getName(), percent));
        }
    }

    // Load bar chart with expenses data
    public void loadBarChart(List<Expense> expenses) {
        barChart.getData().clear();
        barChart.setMinSize(600, 300);
        barChart.setPrefSize(600, 300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);
        }

        barChart.getData().add(series);
    }

    // Load line chart with expenses data
    public void loadLineChart(List<Expense> expenses) {
        lineChart.getData().clear();
        lineChart.setTitle("Chi tiêu theo danh mục");
        lineChart.setPrefSize(850, 350);
        lineChart.setMinSize(850, 350);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Expense expense : expenses) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(expense.getCategoryName(), expense.getAmount());
            series.getData().add(data);
        }

        lineChart.getData().add(series);
    }

    // Load expenses data into charts
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

    // Load monthly expenses data for the line chart
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
