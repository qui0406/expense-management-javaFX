package com.qlct.quanlychitieu;

import com.qlct.pojo.Expense;
import com.qlct.services.StatsServices;
import com.qlct.services.UserSessionServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author QUI
 */
public class StatsController implements Initializable {

    @FXML private ComboBox<String> cbChoseTable;
    @FXML private ComboBox<String> cbType;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private Button filter;
    @FXML private Button btnSwitchAddTransaction;
    @FXML private Button btnSwitchBudget;
    @FXML private Button logoutStats;
    @FXML private Label lbUsername;
    @FXML private TableView<Expense> tbExpenses;
    @FXML private TableView<Expense> tbStatsByMonth;
    @FXML private ComboBox<String> cbChoseYearStats;

    public StatsServices statsServices;

    public StatsController() {
        this.statsServices = new StatsServices();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utils.loadUsername(lbUsername);
        try {
            loadComboBoxType(); // cbType cần được gán giá trị trước
            loadComboBoxTableCurrentMonth();
            loadComboBoxChoseYear();
            cbChoseTable.setOnAction(event -> handleComboBoxTable());

            if (cbType.getValue() != null) {
//                statsServices.getValidatedFilteredExpenses(fromDate, toDate, cbType.getValue());
                loadColumnsCurrentMonth();
                loadData(statsServices.getExpenseCurrentMonth(cbType.getValue()));
            }

            setUpComboBoxStats();
            setUpFilterStats();
            logoutStats.setOnAction(event -> Utils.logout(logoutStats, "login.fxml"));

            loadColumnsByMonth();
            loadDataByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        checkChoseFutureDate();
    }

    private void loadComboBoxTableCurrentMonth() {
        cbChoseTable.setValue("Thống kê theo bảng");
        List<String> choiceBox = Arrays.asList("Thống kê theo bảng", "Thống kê theo biểu đồ");
        cbChoseTable.setItems(FXCollections.observableList(choiceBox));
    }

    private void loadComboBoxType() {
        cbType.setValue("Chi");
        List<String> choice = Arrays.asList("Chi", "Thu");
        cbType.setItems(FXCollections.observableList(choice));
    }

    @FXML
    public void switchTransaction() throws IOException {
        App.setRoot("FXTransactions", 1200, 800);
    }

    @FXML
    public void switchBudget() throws IOException {
        App.setRoot("budget", 1200, 800);
    }

    private void setUpComboBoxStats() {
        cbType.setOnAction(event -> {
            try {
                fromDate.setValue(null);
                toDate.setValue(null);
                loadData(statsServices.getExpenseCurrentMonth(cbType.getValue()));
                loadDataByMonth();
            } catch (SQLException ex) {
                Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        cbChoseYearStats.setOnAction(event -> {
            try {
                loadDataByMonth();
            } catch (Exception ex) {
                Logger.getLogger(ChartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
    
    private void loadComboBoxChoseYear() {
        int currentYear = LocalDate.now().getYear();
        cbChoseYearStats.setValue(String.valueOf(currentYear));
        List<String> choiceBox = new ArrayList<>();
        choiceBox.add(String.valueOf(currentYear));
        choiceBox.add(String.valueOf(currentYear - 1));
        choiceBox.add(String.valueOf(currentYear - 2));
        cbChoseYearStats.setItems(FXCollections.observableList(choiceBox));
    }

    private void setUpFilterStats() {
        filter.setOnAction(event -> {
            try {
                filterDate(event);
            } catch (SQLException ex) {
                Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void checkChoseFutureDate() {
        fromDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        toDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
    }
    
    

    public void handleComboBoxTable() {
        String selectedOption = cbChoseTable.getValue();
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


    public void loadColumnsCurrentMonth() {
        tbExpenses.getColumns().setAll(createCommonColumns());
    }

    public void loadColumnsByMonth() {
        tbStatsByMonth.getColumns().setAll(createCommonColumns());
    }

    private List<TableColumn<Expense, ?>> createCommonColumns() {
        TableColumn<Expense, String> colCategoryId = new TableColumn<>("Số thứ tự");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);

        TableColumn<Expense, String> colCategoryName = new TableColumn<>("Danh mục / Tháng");
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colCategoryName.setPrefWidth(200);

        TableColumn<Expense, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(300);
        colAmount.setCellFactory(column -> new TableCell<>() {
            private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : currencyFormat.format(amount));
            }
        });

        return Arrays.asList(colCategoryId, colCategoryName, colAmount);
    }

    public void loadData(List<Expense> expenseList) {
        ObservableList<Expense> expenses = FXCollections.observableArrayList(expenseList);
        tbExpenses.setItems(expenses);
    }

    public void loadDataByMonth() {
        try {
            int currentYear = Integer.parseInt(cbChoseYearStats.getValue());
            String type = cbType.getValue();
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(cbType.getValue(), currentYear);
            ObservableList<Expense> expenses = FXCollections.observableArrayList(expensesByMonth);
            tbStatsByMonth.setItems(expenses);
        } catch (SQLException ex) {
            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void filterDate(Event e) throws SQLException {
        LocalDate selectedFromDate = fromDate.getValue();
        LocalDate selectedToDate = toDate.getValue();
        LocalDate today = LocalDate.now();

        if (selectedFromDate == null || selectedToDate == null) {
            Utils.showAlert("Thiếu thông tin!", null, "Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc.");
            return;
        }

        if (selectedFromDate.isAfter(today) || selectedToDate.isAfter(today)) {
            Utils.showAlert("Ngày không hợp lệ!", null, "Không được chọn ngày trong tương lai.");
            return;
        }

        if (selectedFromDate.isAfter(selectedToDate)) {
            Utils.showAlert("Khoảng ngày không hợp lệ!", null, "Ngày bắt đầu không được lớn hơn ngày kết thúc.");
            return;
        }

        List<Expense> filteredExpenses = statsServices.getExpensesFilterByDate(selectedFromDate, selectedToDate, cbType.getValue());
        loadData(filteredExpenses);
    }
    
//    public void loadDataByMonthFiltered(LocalDate from, LocalDate to) {
//        try {
//            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(from, to, cbType.getValue());
//            ObservableList<Expense> expenses = FXCollections.observableArrayList(expensesByMonth);
//            tbStatsByMonth.setItems(expenses);
//        } catch (SQLException ex) {
//            Logger.getLogger(StatsController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }


}
