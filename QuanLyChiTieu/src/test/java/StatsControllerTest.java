//import com.qlct.pojo.Expense;
//import com.qlct.quanlychitieu.StatsController;
//import com.qlct.services.StatsServices;
//import javafx.application.Platform;
//import javafx.collections.ObservableList;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.DatePicker;
//import javafx.scene.control.Label;
//import javafx.scene.control.TableView;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class StatsControllerTest {
//
//    private StatsController controller;
//    private StatsServices mockStatsServices;
//
//    @BeforeAll
//    public static void initJFX() throws Exception {
//        // Khởi tạo JavaFX Toolkit một lần duy nhất
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.startup(latch::countDown);
//        latch.await();
////        new JFXPanel(); // Đảm bảo môi trường JavaFX Swing được tạo
//    }
//
//    @BeforeEach
//    public void setUp() {
//        controller = new StatsController();
//
//        // Khởi tạo các thành phần giao diện giả lập
//        controller.cbType = new ComboBox<>();
//        controller.cbChoseTable = new ComboBox<>();
//        controller.fromDate = new DatePicker();
//        controller.toDate = new DatePicker();
//        controller.tbExpenses = new TableView<>();
//        controller.tbStatsByMonth = new TableView<>();
//        controller.lbUsername = new Label();
//
//        // Mock dịch vụ
//        mockStatsServices = Mockito.mock(StatsServices.class);
//        controller.statsServices = mockStatsServices;
//    }
//
//    @Test
//    public void testLoadComboBoxType() {
//        controller.loadComboBoxType();
//        assertEquals("Chi", controller.cbType.getValue());
//        assertTrue(controller.cbType.getItems().containsAll(List.of("Chi", "Thu")));
//    }
//
//    @Test
//    public void testLoadComboBoxTableCurrentMonth() {
//        controller.loadComboBoxTableCurrentMonth();
//        assertEquals("Thống kê theo bảng", controller.cbChoseTable.getValue());
//        assertTrue(controller.cbChoseTable.getItems().containsAll(List.of("Thống kê theo bảng", "Thống kê theo biểu đồ")));
//    }
//
//    @Test
//    public void testLoadData() throws SQLException {
//        List<Expense> mockData = Arrays.asList(
//                new Expense(1, "Ăn uống", 100000),
//                new Expense(2, "Đi lại", 50000)
//        );
//
//        when(mockStatsServices.getExpenseCurrentMonth("Chi")).thenReturn(mockData);
//        controller.cbType.setValue("Chi");
//
//        controller.loadData(mockData);
//        ObservableList<Expense> items = controller.tbExpenses.getItems();
//
//        assertEquals(2, items.size());
//        assertEquals("Ăn uống", items.get(0).getCategoryName());
//        assertEquals("Đi lại", items.get(1).getCategoryName());
//    }
//
//    @Test
//    public void testFilterDate() throws SQLException {
//        LocalDate from = LocalDate.of(2024, 4, 1);
//        LocalDate to = LocalDate.of(2024, 4, 30);
//
//        controller.fromDate.setValue(from);
//        controller.toDate.setValue(to);
//        controller.cbType.setValue("Chi");
//
//        List<Expense> filtered = List.of(new Expense(from, to, 1, "Ăn uống", 100000));
//        when(mockStatsServices.getExpensesFilterByDate(from, to, "Chi")).thenReturn(filtered);
//
//        controller.filterDate(null);
//
//        ObservableList<Expense> items = controller.tbExpenses.getItems();
//        assertEquals(1, items.size());
//        assertEquals("Ăn uống", items.get(0).getCategoryName());
//    }
//
//    @Test
//    public void testLoadDataByMonth() throws SQLException {
//        List<Expense> monthly = List.of(new Expense(1, "Tháng 4", 100000));
//        when(mockStatsServices.getMonthlyExpenses("Chi")).thenReturn(monthly);
//
//        controller.cbType.setValue("Chi");
//        controller.loadDataByMonth();
//
//        ObservableList<Expense> items = controller.tbStatsByMonth.getItems();
//        assertEquals(1, items.size());
//        assertEquals("Tháng 4", items.get(0).getCategoryName());
//    }
//
//    @Test
//    public void testHandleComboBoxTableSwitch() {
//        StatsController spyController = Mockito.spy(controller);
//
//        // Tránh gọi thật scene switch trong test đơn vị
//        doNothing().when(spyController).switchSceneTable(anyString());
//
//        spyController.cbChoseTable.setValue("Thống kê theo bảng");
//        spyController.handleComboBoxTable();
//        verify(spyController).switchSceneTable("baocaothongketheobang.fxml");
//
//        spyController.cbChoseTable.setValue("Thống kê theo biểu đồ");
//        spyController.handleComboBoxTable();
//        verify(spyController).switchSceneTable("baocaothongketheobieudo.fxml");
//    }
//}
