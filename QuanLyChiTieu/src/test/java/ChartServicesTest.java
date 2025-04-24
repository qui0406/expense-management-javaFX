package com.qlct.services;

import com.qlct.pojo.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ChartServicesTest {

    private ChartServices chartServices;

    @Mock private PieChart pieChart;
    @Mock private BarChart<String, Number> barChart;
    @Mock private LineChart<String, Number> lineChart;

    private List<Expense> expenses;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chartServices = new ChartServices();
        expenses = Arrays.asList(
                new Expense("Food", 100),
                new Expense("Transport", 50),
                new Expense("Entertainment", 30)
        );
    }

    @Test
    public void testLoadPieChart() {
        chartServices.loadPieChart(pieChart, expenses);

        // Kiểm tra xem pieChart có dữ liệu không
        verify(pieChart).setData(any(ObservableList.class));
        verify(pieChart).setTitle("Biểu đồ chi tiêu");
        verify(pieChart).setLabelsVisible(false);
        verify(pieChart).setLegendVisible(true);

        // Kiểm tra số lượng phần tử trong pie chart
        ObservableList<PieChart.Data> data = pieChart.getData();
        assert data.size() == 3 : "Expected 3 data entries in PieChart";
    }

    @Test
    public void testLoadBarChart() {
        chartServices.loadBarChart(barChart, expenses);

        // Kiểm tra xem barChart có dữ liệu không
        verify(barChart).getData();
        verify(barChart).setMinSize(600, 300);
        verify(barChart).setPrefSize(600, 300);

        // Kiểm tra số lượng series trong bar chart
        assert barChart.getData().size() == 1 : "Expected 1 series in BarChart";
        XYChart.Series<String, Number> series = (XYChart.Series<String, Number>) barChart.getData().get(0);
        assert series.getData().size() == 3 : "Expected 3 data entries in BarChart";
    }

    @Test
    public void testLoadLineChart() {
        chartServices.loadLineChart(lineChart, expenses);

        // Kiểm tra xem lineChart có dữ liệu không
        verify(lineChart).getData();
        verify(lineChart).setTitle("Chi tiêu theo danh mục");
        verify(lineChart).setPrefSize(850, 350);
        verify(lineChart).setMinSize(850, 350);

        // Kiểm tra số lượng series trong line chart
        assert lineChart.getData().size() == 1 : "Expected 1 series in LineChart";
        XYChart.Series<String, Number> series = (XYChart.Series<String, Number>) lineChart.getData().get(0);
        assert series.getData().size() == 3 : "Expected 3 data entries in LineChart";
    }
}
