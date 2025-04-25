package com.qlct.services;

import com.qlct.pojo.Expense;
import com.qlct.pojo.User;
import com.qlct.pojo.JdbcUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestStatsServices {
    private StatsServices statsServices= new StatsServices();
    private Connection mockConn= mock(Connection.class);
    private PreparedStatement mockStmt = mock(PreparedStatement.class);
    private ResultSet mockRs= mock(ResultSet.class);

    @Test
    public void testGetExpenseCurrentMonth() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
            MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)
        ) {
            
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("id")).thenReturn(1);
            when(mockRs.getString("name")).thenReturn("Ăn uống");
            when(mockRs.getDouble("amount")).thenReturn(500_000.0);

            List<Expense> result = statsServices.getExpenseCurrentMonth("EXP");

            assertEquals(1, result.size());
            assertEquals("Ăn uống", result.get(0).getCategoryName());
            assertEquals(500_000.0, result.get(0).getAmount());
        }
        
    }

    @Test
    public void testGetExpensesFilterByDate() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
            MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)
        ) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("id")).thenReturn(2);
            when(mockRs.getString("name")).thenReturn("Giải trí");
            when(mockRs.getDouble("amount")).thenReturn(300_000.0);

            List<Expense> result = statsServices.getExpensesFilterByDate(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31),
                    "EXP");

            assertEquals(1, result.size());
            assertEquals("Giải trí", result.get(0).getCategoryName());
            assertEquals(300_000.0, result.get(0).getAmount());
        }
    }

    @Test
    public void testGetMonthlyExpenses() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
            MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)
        ) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("month")).thenReturn(3);
            when(mockRs.getDouble("total_amount")).thenReturn(1200000.0);

            List<Expense> result = statsServices.getMonthlyExpenses("EXP", 2025);

            assertEquals(1, result.size());
            assertEquals("Tháng 3", result.get(0).getCategoryName());
            assertEquals(1200000.0, result.get(0).getAmount());
        }
    }
}
