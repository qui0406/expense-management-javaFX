package com.expensemanagement.services;

import com.expensemanagement.pojo.Expense;
import com.expensemanagement.pojo.JdbcUtils;
import com.expensemanagement.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
import javafx.scene.chart.LineChart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChartServicesTest {

    @Mock
    private PieChart pieChart;

    @Mock
    private BarChart barChart;
    
    @Mock
    private LineChart lineChart;

    private Connection conn = mock(Connection.class);
    private PreparedStatement mockStatement = mock(PreparedStatement.class);
    private ResultSet mockResultSet = mock(ResultSet.class);

    @InjectMocks
    private ChartServices chartServices;

    private List<Expense> expenses;
    ;

    @BeforeEach
    public void setUp() {
        expenses = Arrays.asList(
            new Expense(1, "Ăn uống", 100.0),
            new Expense(2, "Xăng xe", 50.0),
            new Expense(3, "Sinh hoạt", 75.0)
        );
    }
    
    
    @Test
    public void testPieChartCorrect() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        when(pieChart.getData()).thenReturn(pieChartData);
        doAnswer(invocation -> {
            pieChartData.setAll((ObservableList<PieChart.Data>) invocation.getArgument(0));
            return null;
        }).when(pieChart).setData(any(ObservableList.class));
        chartServices.loadPieChart(pieChart, expenses);
        assertEquals(3, pieChartData.size());
        double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
        assertEquals("Ăn uống - " + String.format("%.1f", 100.0 / totalAmount * 100) + "%", pieChartData.get(0).getName());
        assertEquals(100.0, pieChartData.get(0).getPieValue(), 0.001);
        assertEquals("Xăng xe - " + String.format("%.1f", 50.0 / totalAmount * 100) + "%", pieChartData.get(1).getName());
        assertEquals(50.0, pieChartData.get(1).getPieValue(), 0.001);
        assertEquals("Sinh hoạt - " + String.format("%.1f", 75.0 / totalAmount * 100) + "%", pieChartData.get(2).getName());
        assertEquals(75.0, pieChartData.get(2).getPieValue(), 0.001);
        verify(pieChart).setData(any(ObservableList.class));
        verify(pieChart).setLabelsVisible(false);
        verify(pieChart).setLegendVisible(true);
        verify(pieChart).setMinSize(350, 320);
        verify(pieChart).setPrefSize(100, 100);
    }
    @Test
    public void testPieChartEmpty() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        when(pieChart.getData()).thenReturn(pieChartData);
        chartServices.loadPieChart(pieChart, Arrays.asList());
        assertEquals(0, pieChartData.size());
        verify(pieChart).setData(pieChartData);
        verify(pieChart).setLabelsVisible(false);
        verify(pieChart).setLegendVisible(true);
        verify(pieChart).setMinSize(350, 320);
        verify(pieChart).setPrefSize(100, 100);
    }
    
    
    @Test
    public void testLoadBarChartCorrect() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(barChart.getData()).thenReturn(mockData);
        chartServices.loadBarChart(barChart, expenses);
        assertEquals(1, mockData.size());
        XYChart.Series<String, Number> series = mockData.get(0);
        assertEquals(3, series.getData().size());
        assertEquals("Ăn uống", series.getData().get(0).getXValue());
        assertEquals(100.0, series.getData().get(0).getYValue());
        verify(barChart).setMinSize(600, 300);
        verify(barChart).setPrefSize(600, 300);
    }
    @Test
    public void testBarChartNull() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(barChart.getData()).thenReturn(mockData);
        assertThrows(NullPointerException.class, () -> chartServices.loadBarChart(barChart, null));
    }
    @Test
    public void testBarChartEmpty() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(barChart.getData()).thenReturn(mockData);
        chartServices.loadBarChart(barChart, Arrays.asList());
        assertEquals(1, mockData.size());
        assertEquals(0, mockData.get(0).getData().size());
    }
    
    @Test
    public void testLineChartCorrect() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(lineChart.getData()).thenReturn(mockData);
        chartServices.loadLineChart(lineChart, expenses);
        verify(lineChart).setTitle("Chi tiêu theo danh mục");
        verify(lineChart).setPrefSize(850, 350);
        verify(lineChart).setMinSize(850, 350);
        assertEquals(1, mockData.size());
        XYChart.Series<String, Number> series = mockData.get(0);
        assertEquals(3, series.getData().size());
        assertEquals("Ăn uống", series.getData().get(0).getXValue());
        assertEquals(100.0, series.getData().get(0).getYValue());
    }
    @Test
    public void testLineChartNull() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(lineChart.getData()).thenReturn(mockData);
        assertThrows(NullPointerException.class, () -> chartServices.loadLineChart(lineChart, null));
    }
    @Test
    public void testLineChartEmpty() {
        ObservableList<XYChart.Series<String, Number>> mockData = FXCollections.observableArrayList();
        when(lineChart.getData()).thenReturn(mockData);
        chartServices.loadLineChart(lineChart, Arrays.asList());
        assertEquals(1, mockData.size());
        assertEquals(0, mockData.get(0).getData().size());
    }
    
    
    @Test
    public void testGetExpenseCurrentMonthChart() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        try (
            MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)
        ) {
            
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(conn);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("name")).thenReturn("Ăn uống");
            when(mockResultSet.getDouble("amount")).thenReturn(500000.0);
            List<Expense> result = chartServices.getExpenseCurrentMonthChart("Chi");
            assertEquals(1, result.size());
            assertEquals("Ăn uống", result.get(0).getCategoryName());
            assertEquals(500000.0, result.get(0).getAmount());
        }
    }

    @Test
    public void testGetExpensesFilterByDate() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        try (
            MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)
        ) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(conn);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getInt("id")).thenReturn(2);
            when(mockResultSet.getString("name")).thenReturn("Giải trí");
            when(mockResultSet.getDouble("amount")).thenReturn(300000.0);

            List<Expense> result = chartServices.getExpensesFilterByDateChart(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31),
                    "Chi");
            assertEquals(1, result.size());
            assertEquals("Giải trí", result.get(0).getCategoryName());
            assertEquals(300_000.0, result.get(0).getAmount());
        }
    }
}
