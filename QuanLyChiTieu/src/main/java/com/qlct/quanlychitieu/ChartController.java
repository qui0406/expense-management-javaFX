package com.qlct.quanlychitieu;

import com.qlct.pojo.Expense;
import com.qlct.services.ChartServices;
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
import javafx.scene.control.Button;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;

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
    @FXML private ComboBox<String> cbChoseYear;

    private final StatsServices statsServices;
    private final ChartServices chartServices;

    public ChartController() {
        this.statsServices = new StatsServices();
        this.chartServices = new ChartServices();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize username
        Utils.loadUsername(lbUsernameChart);

        loadComboBoxChart();
        loadComboBoxType();
        loadComboBoxChoseYear();
        setupComboBoxHandlers();
        setupButtonHandlers();

        logoutChart.setOnAction(event -> Utils.logout(logoutChart, "login.fxml"));

        try {
            loadData(statsServices.getExpenseCurrentMonth(cbTypeChart.getValue()));
            loadDataByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
        }
        checkChoseFutureDate();
    }
    
    private void checkChoseFutureDate() {
        fromDateChart.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        toDateChart.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }

    // Load combo box options for chart type selection
    private void loadComboBoxChart() {
        cbChoseChart.setValue("Thống kê theo biểu đồ");
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add("Thống kê theo biểu đồ");
        choiceBox.add("Thống kê theo bảng");
        cbChoseChart.setItems(FXCollections.observableList(choiceBox));
    }

    private void loadComboBoxChoseYear() {
        int currentYear = LocalDate.now().getYear();
        cbChoseYear.setValue(String.valueOf(currentYear));
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add(String.valueOf(currentYear));
        choiceBox.add(String.valueOf(currentYear - 1));
        choiceBox.add(String.valueOf(currentYear - 2));
        cbChoseYear.setItems(FXCollections.observableList(choiceBox));
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

        cbChoseYear.setOnAction(event -> {
            try {
                loadDataByMonth();
            } catch (Exception ex) {
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
    public void handleComboBoxChart() {
        String selectedOption = cbChoseChart.getValue();
        try {
            if ("Thống kê theo bảng".equals(selectedOption)) {
                App.setRoot("baocaothongketheobang", 1200, 800);
            } else if ("Thống kê theo biểu đồ".equals(selectedOption)) {
                App.setRoot("baocaothongketheobieudo", 1200, 800);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi chuyển scene: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể chuyển màn hình");
            alert.setContentText("Vui lòng thử lại sau.");
            alert.showAndWait();
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

    // Load expenses data into charts
    public void loadData(List<Expense> expenseList) throws SQLException {
        if (expenseList == null || expenseList.isEmpty()) {
            pieChart.setVisible(false);
            lineChart.setVisible(false);
            barChart.setVisible(false);
            pieChart.getData().clear();
            lineChart.getData().clear();
            barChart.getData().clear();
            return;
        }

        pieChart.setVisible(true);
        lineChart.setVisible(true);
        barChart.setVisible(true);

        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        chartServices.loadPieChart(pieChart, expenses);
        chartServices.loadBarChart(barChart, expenses);
    }

    // Load monthly expenses data for the line chart
    public void loadDataByMonth() {
        try {
            int currentYear = Integer.parseInt(cbChoseYear.getValue());
            String type = cbTypeChart.getValue();
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(type , currentYear);
            if (expensesByMonth == null || expensesByMonth.isEmpty()) {
                lineChart.setVisible(false);
                lineChart.getData().clear();
                return;
            }
            lineChart.setVisible(true);
            chartServices.loadLineChart(lineChart, expensesByMonth);
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void switchTransaction() throws IOException {
        App.setRoot("FXTransactions", 1200, 800);
    }

    @FXML
    public void switchBudget() throws IOException {
        App.setRoot("budget", 1200, 800);
    }
}
