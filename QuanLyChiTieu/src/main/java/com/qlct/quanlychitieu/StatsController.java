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
        if ("Thống kê theo bảng".equals(selectedOption)) {
            switchSceneTable("baocaothongketheobang.fxml");
        } else if ("Thống kê theo biểu đồ".equals(selectedOption)) {
            switchSceneTable("baocaothongketheobieudo.fxml");
        }
    }

    public void switchSceneTable(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) cbChoseTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.out.println("Failed to load FXML: " + e);
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
            List<Expense> expensesByMonth = statsServices.getMonthlyExpenses(cbType.getValue());
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

}
