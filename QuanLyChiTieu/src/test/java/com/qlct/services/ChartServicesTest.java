package com.qlct.services;

import com.qlct.pojo.Expense;
import com.qlct.pojo.JdbcUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChartServicesTest {

    @InjectMocks
    private ChartServices chartServices;

    @Mock
    private JdbcUtils jdbcUtilsMock;

    @Mock
    private Connection connectionMock;

    @Mock
    private PreparedStatement preparedStatementMock;

    @Mock
    private ResultSet resultSetMock;
    
   

    @Test
    public void testGetExpenseCurrentMonthChart() throws SQLException {
        // Mocking ResultSet data
        MockitoAnnotations.openMocks(this);
        when(JdbcUtils.getConn()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("name")).thenReturn("Food");
        when(resultSetMock.getDouble("amount")).thenReturn(100.0);
        
        String type = "income";
        List<Expense> expenses = chartServices.getExpenseCurrentMonthChart(type);

        // Verify that the correct SQL statement is executed
        verify(preparedStatementMock).setString(1, type);

        assertNotNull(expenses);
        assertEquals(1, expenses.size());
        assertEquals("Food", expenses.get(0).getCategoryName());
        assertEquals(100.0, expenses.get(0).getAmount(), 0.01);
    }

    @Test
    public void testGetExpensesFilterByDateChart() throws SQLException {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 3, 31);
        String type = "expense";
        
        // Mocking ResultSet data
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getInt("id")).thenReturn(1);
        when(resultSetMock.getString("name")).thenReturn("Entertainment");
        when(resultSetMock.getDouble("amount")).thenReturn(150.0);
        
        List<Expense> expenses = chartServices.getExpensesFilterByDateChart(fromDate, toDate, type);

        // Verify that the prepared statement is correctly set
        verify(preparedStatementMock).setDate(1, Date.valueOf(fromDate));
        verify(preparedStatementMock).setDate(2, Date.valueOf(toDate));
        verify(preparedStatementMock).setString(4, type);

        assertNotNull(expenses);
        assertEquals(1, expenses.size());
        assertEquals("Entertainment", expenses.get(0).getCategoryName());
        assertEquals(150.0, expenses.get(0).getAmount(), 0.01);
    }

    @Test
    public void testLoadPieChart() {
        PieChart chart = new PieChart();
        List<Expense> expenses = List.of(
                new Expense(1, "Food", 100.0),
                new Expense(2, "Entertainment", 50.0)
        );

        chartServices.loadPieChart(chart, expenses);

        // Verify that the pie chart data is loaded correctly
        assertEquals(2, chart.getData().size());
        assertEquals("Food", chart.getData().get(0).getName());
        assertEquals("Entertainment", chart.getData().get(1).getName());
    }

    @Test
    public void testLoadBarChart() {
        BarChart<String, Number> chart = new BarChart<>(null, null);
        List<Expense> expenses = List.of(
                new Expense(1, "Food", 100.0),
                new Expense(2, "Entertainment", 50.0)
        );

        chartServices.loadBarChart(chart, expenses);

        // Verify that the bar chart data is loaded correctly
        assertEquals(1, chart.getData().size());
        assertEquals("Food", chart.getData().get(0).getData().get(0).getXValue());
        assertEquals(100.0, chart.getData().get(0).getData().get(0).getYValue());
    }

    @Test
    public void testLoadLineChart() {
        LineChart<String, Number> chart = new LineChart<>(null, null);
        List<Expense> expenses = List.of(
                new Expense(1, "Food", 100.0),
                new Expense(2, "Entertainment", 50.0)
        );

        chartServices.loadLineChart(chart, expenses);

        // Verify that the line chart data is loaded correctly
        assertEquals(1, chart.getData().size());
        assertEquals("Food", chart.getData().get(0).getData().get(0).getXValue());
        assertEquals(100.0, chart.getData().get(0).getData().get(0).getYValue());
    }
}
