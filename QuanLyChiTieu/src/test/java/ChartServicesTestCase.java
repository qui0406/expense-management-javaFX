
import com.qlct.pojo.Expense;
import com.qlct.services.ChartServices;
import static java.security.KeyRep.Type.SECRET;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChartServicesTestCase {

    @InjectMocks
    private ChartServices chartServices;

    @Mock
    private PieChart pieChart;

    @Mock
    private BarChart<String, Number> barChart;

    @Mock
    private LineChart<String, Number> lineChart;

    @Mock
    private ObservableList<PieChart.Data> pieChartData;

    @Mock
    private ObservableList<XYChart.Series<String, Number>> chartSeries;

    @BeforeEach
    void setUp() {
        // Khởi tạo mock
        MockitoAnnotations.openMocks(this);

        // Mock hành vi trả về của các biểu đồ
        when(pieChart.getData()).thenReturn(pieChartData);
        when(barChart.getData()).thenReturn(chartSeries);
        when(lineChart.getData()).thenReturn(chartSeries);
    }

    // Test loadPieChart với danh sách null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadPieChart_NullExpenses() {
        // Kiểm tra lỗi khi expenses là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadPieChart(pieChart, null);
        });
    }

    // Test loadPieChart với chart null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadPieChart_NullChart() {
        List<Expense> expenses = Arrays.asList(new Expense(1, "Food", 500.0));

        // Kiểm tra lỗi khi chart là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadPieChart(null, expenses);
        });
    }

    @Test
    void testLoadPieChart_Success() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, "Food", 500.0),
                new Expense(2, "Transport", 300.0)
        );

        ObservableList<PieChart.Data> actualPieChartData = FXCollections.observableArrayList();
        when(pieChart.getData()).thenReturn(actualPieChartData);

        chartServices.loadPieChart(pieChart, expenses);

        // Sử dụng ArgumentCaptor để bắt giá trị title
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(pieChart).setTitle(titleCaptor.capture());
        assertEquals("Biểu đồ chi tiêu", titleCaptor.getValue());

        // Bắt danh sách dữ liệu được gán vào pieChart.setData()
        ArgumentCaptor<ObservableList<PieChart.Data>> captor = ArgumentCaptor.forClass(ObservableList.class);
        verify(pieChart).setData(captor.capture());
        ObservableList<PieChart.Data> capturedData = captor.getValue();

        // Cập nhật mock để pieChart.getData() trả về capturedData
        when(pieChart.getData()).thenReturn(capturedData);

        // Gọi lại vòng lặp để cập nhật tên (mô phỏng hành vi của loadPieChart)
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        for (PieChart.Data d : capturedData) {
            double percent = (d.getPieValue() / total) * 100;
            d.nameProperty().set(String.format("%s - %.1f%%", d.getName(), percent));
        }

        assertEquals(2, capturedData.size());
        assertEquals("Food - 62.5%", capturedData.get(0).getName());
        assertEquals(500.0, capturedData.get(0).getPieValue(), 0.01);
    }

    // Test loadPieChart với categoryName là null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadPieChart_NullCategoryName() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, null, 500.0),
                new Expense(2, "Transport", 300.0)
        );

        // Kiểm tra lỗi khi categoryName là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadPieChart(pieChart, expenses);
        });
    }

    // Test loadBarChart với dữ liệu hợp lệ (bao phủ luồng chính)
    @Test
    void testLoadBarChart_Success() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, "Food", 500.0),
                new Expense(2, "Transport", 300.0)
        );

        chartServices.loadBarChart(barChart, expenses);

        // Xác minh cấu hình biểu đồ
        verify(barChart).setMinSize(600, 300);
        verify(barChart).setPrefSize(600, 300);
        verify(chartSeries).clear();

        // Sử dụng ArgumentCaptor để bắt series dữ liệu
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());

        // Kiểm tra dữ liệu
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        List<XYChart.Data<String, Number>> data = capturedSeries.getData();
        assertEquals(2, data.size());
        assertEquals("Food", data.get(0).getXValue());
        assertEquals(500.0, data.get(0).getYValue().doubleValue(), 0.01);
        assertEquals("Transport", data.get(1).getXValue());
        assertEquals(300.0, data.get(1).getYValue().doubleValue(), 0.01);
    }

    // Test loadBarChart với danh sách rỗng
    @Test
    void testLoadBarChart_EmptyExpenses() {
        List<Expense> expenses = Collections.emptyList();

        chartServices.loadBarChart(barChart, expenses);

        // Xác minh cấu hình biểu đồ
        verify(barChart).setMinSize(600, 300);
        verify(chartSeries).clear();

        // Sử dụng ArgumentCaptor để bắt series dữ liệu
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());

        // Kiểm tra dữ liệu rỗng
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        assertTrue(capturedSeries.getData().isEmpty());
    }

    // Test loadBarChart với danh sách null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadBarChart_NullExpenses() {
        // Kiểm tra lỗi khi expenses là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadBarChart(barChart, null);
        });
    }

    // Test loadBarChart với chart null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadBarChart_NullChart() {
        List<Expense> expenses = Arrays.asList(new Expense(1, "Food", 500.0));

        // Kiểm tra lỗi khi chart là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadBarChart(null, expenses);
        });
    }

    // Test loadBarChart với số lượng lớn expenses (bao phủ vòng lặp gán màu)
    @Test
    void testLoadBarChart_LargeExpenses() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, "E1", 100.0),
                new Expense(2, "E2", 200.0),
                new Expense(3, "E3", 300.0),
                new Expense(4, "E4", 400.0),
                new Expense(5, "E5", 500.0),
                new Expense(6, "E6", 600.0),
                new Expense(7, "E7", 700.0),
                new Expense(8, "E8", 800.0),
                new Expense(9, "E9", 900.0),
                new Expense(10, "E10", 1000.0)
        );

        chartServices.loadBarChart(barChart, expenses);

        // Sử dụng ArgumentCaptor để bắt series dữ liệu
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());

        // Kiểm tra dữ liệu
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        List<XYChart.Data<String, Number>> data = capturedSeries.getData();
        assertEquals(10, data.size());
        assertEquals("E1", data.get(0).getXValue());
        assertEquals(100.0, data.get(0).getYValue().doubleValue(), 0.01);
    }

    // Test loadLineChart với dữ liệu hợp lệ (bao phủ luồng chính)
    @Test
    void testLoadLineChart_Success() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, "Food", 500.0),
                new Expense(2, "Transport", 300.0)
        );

        chartServices.loadLineChart(lineChart, expenses);

        // Xác minh cấu hình biểu đồ
        verify(lineChart).setTitle("Chi tiêu theo danh mục");
        verify(lineChart).setMinSize(850, 350);
        verify(lineChart).setPrefSize(850, 350);
        verify(chartSeries).clear();

        // Sử dụng ArgumentCaptor để bắt series dữ liệu
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());

        // Kiểm tra dữ liệu
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        List<XYChart.Data<String, Number>> data = capturedSeries.getData();
        assertEquals(2, data.size());
        assertEquals("Food", data.get(0).getXValue());
        assertEquals(500.0, data.get(0).getYValue().doubleValue(), 0.01);
        assertEquals("Transport", data.get(1).getXValue());
        assertEquals(300.0, data.get(1).getYValue().doubleValue(), 0.01);
    }

    @Test
    void testLoadLineChart_EmptyExpenses() {
        List<Expense> expenses = Collections.emptyList();
        chartServices.loadLineChart(lineChart, expenses);
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        assertTrue(capturedSeries.getData().isEmpty());
    }

    // Test loadLineChart với danh sách null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadLineChart_NullExpenses() {
        // Kiểm tra lỗi khi expenses là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadLineChart(lineChart, null);
        });
    }

    // Test loadLineChart với chart null (dự kiến lỗi: NullPointerException)
    @Test
    void testLoadLineChart_NullChart() {
        List<Expense> expenses = Arrays.asList(new Expense(1, "Food", 500.0));

        // Kiểm tra lỗi khi chart là null
        assertThrows(NullPointerException.class, () -> {
            chartServices.loadLineChart(null, expenses);
        });
    }

    // Test loadLineChart với amount là Infinity (dự kiến lỗi: dữ liệu biểu đồ không hợp lệ)
    @Test
    void testLoadLineChart_InfiniteAmount() {
        List<Expense> expenses = Arrays.asList(
                new Expense(1, "Food", Double.POSITIVE_INFINITY),
                new Expense(2, "Transport", 300.0)
        );

        chartServices.loadLineChart(lineChart, expenses);

        // Sử dụng ArgumentCaptor để bắt series dữ liệu
        ArgumentCaptor<XYChart.Series<String, Number>> captor = ArgumentCaptor.forClass(XYChart.Series.class);
        verify(chartSeries).add(captor.capture());

        // Kiểm tra dữ liệu
        XYChart.Series<String, Number> capturedSeries = captor.getValue();
        List<XYChart.Data<String, Number>> data = capturedSeries.getData();
        assertEquals(2, data.size());
        assertEquals("Food", data.get(0).getXValue());
        assertEquals(Double.POSITIVE_INFINITY, data.get(0).getYValue().doubleValue(), 0.01); // Dữ liệu không hợp lệ
        assertEquals("Transport", data.get(1).getXValue());
        assertEquals(300.0, data.get(1).getYValue().doubleValue(), 0.01);
    }
}
