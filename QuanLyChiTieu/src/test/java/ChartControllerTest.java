//import com.qlct.pojo.Expense;
//import com.qlct.quanlychitieu.ChartController;
//import com.qlct.services.StatsServices;
//import javafx.application.Platform;
//import javafx.collections.ObservableList;
//import javafx.scene.chart.BarChart;
//import javafx.scene.chart.LineChart;
//import javafx.scene.chart.PieChart;
//import javafx.scene.control.*;
//import javafx.scene.image.ImageView;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ChartControllerTest {
//
//    private ChartController controller;
//    private StatsServices mockStatsServices;
//
//    @BeforeAll
//    public static void initJFX() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.startup(latch::countDown);
//        latch.await();
//    }
//
//    @BeforeEach
//    public void setUp() {
//        controller = new ChartController();
//
//        controller.cbChoseChart = new ComboBox<>();
//        controller.cbTypeChart = new ComboBox<>();
//        controller.pieChart = new PieChart();
//        controller.lineChart = new LineChart<>(null, null);
//        controller.barChart = new BarChart<>(null, null);
//        controller.fromDateChart = new DatePicker();
//        controller.toDateChart = new DatePicker();
//        controller.btnFilterChart = new Button();
//        controller.lbUsernameChart = new Label();
//        controller.imgViewChart = new ImageView();
//
//        // Mock dịch vụ
//        mockStatsServices = Mockito.mock(StatsServices.class);
//        controller.statsServices = mockStatsServices;
//    }
//
//    @Test
//    public void testLoadComboBoxTypeChart() {
//        controller.loadComboBoxType();
//        assertEquals("Chi", controller.cbTypeChart.getValue());
//        assertTrue(controller.cbTypeChart.getItems().containsAll(List.of("Chi", "Thu")));
//    }
//
//    @Test
//    public void testLoadComboBoxChart() {
//        controller.loadComboBoxChart();
//        assertEquals("Thống kê theo biểu đồ", controller.cbChoseChart.getValue());
//        assertTrue(controller.cbChoseChart.getItems().containsAll(List.of("Thống kê theo biểu đồ", "Thống kê theo bảng")));
//    }
//
//    @Test
//    public void testFilterDateChart() throws SQLException {
//        LocalDate from = LocalDate.of(2024, 4, 1);
//        LocalDate to = LocalDate.of(2024, 4, 30);
//
//        controller.fromDateChart.setValue(from);
//        controller.toDateChart.setValue(to);
//        controller.cbTypeChart.setValue("Chi");
//
//        List<Expense> filtered = List.of(new Expense(from, to, 1, "Ăn uống", 100000));
//        when(mockStatsServices.getExpensesFilterByDate(from, to, "Chi")).thenReturn(filtered);
//
//        controller.btnFilterChart.fire(); // Gọi như đang click
//
//        // Không có TableView để kiểm tra dữ liệu, nên kiểm tra qua PieChart
//        assertFalse(controller.pieChart.getData().isEmpty());
//        assertEquals("Ăn uống", controller.pieChart.getData().get(0).getName());
//    }
//
//    @Test
//    public void testLoadDataByMonthChart() throws SQLException {
//        List<Expense> monthly = List.of(new Expense(1, "Tháng 4", 100000));
//        when(mockStatsServices.getMonthlyExpenses("Chi")).thenReturn(monthly);
//
//        controller.cbTypeChart.setValue("Chi");
//        controller.loadComboBoxType(); // Đảm bảo cbTypeChart có giá trị
//
//        // Gọi load dữ liệu theo tháng
//        controller.loadComboBoxType();
//        controller.loadComboBoxChart();
//        controller.loadDataByMonth();
//
//        ObservableList<BarChart.Series<String, Number>> series = controller.barChart.getData();
//        assertFalse(series.isEmpty());
//        assertEquals("Chi tiêu", series.get(0).getName());
//    }
//
//    @Test
//    public void testHandleComboBoxChartSwitch() {
//        ChartController spyController = Mockito.spy(controller);
//        doNothing().when(spyController).switchSceneChart(anyString());
//
//        spyController.cbChoseChart.setValue("Thống kê theo bảng");
//        spyController.handleComboBoxChart();
//        verify(spyController).switchSceneChart("baocaothongketheobang.fxml");
//
//        spyController.cbChoseChart.setValue("Thống kê theo biểu đồ");
//        spyController.handleComboBoxChart();
//        verify(spyController).switchSceneChart("baocaothongketheobieudo.fxml");
//    }
//}
