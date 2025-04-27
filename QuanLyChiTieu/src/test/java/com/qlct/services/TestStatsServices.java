package com.qlct.services;

import com.qlct.pojo.Expense;
import com.qlct.pojo.User;
import com.qlct.pojo.JdbcUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestStatsServices {

    private StatsServices statsServices = new StatsServices();
    private Connection mockConn = mock(Connection.class);
    private PreparedStatement mockStmt = mock(PreparedStatement.class);
    private ResultSet mockRs = mock(ResultSet.class);

    @Test
    public void testGetExpenseCurrentMonthSuccess() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {

            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("id")).thenReturn(1);
            when(mockRs.getString("name")).thenReturn("Ăn uống");
            when(mockRs.getDouble("amount")).thenReturn(500000.0);

            List<Expense> result = statsServices.getExpenseCurrentMonth("Chi");

            assertEquals(1, result.size());
            assertEquals("Ăn uống", result.get(0).getCategoryName());
            assertEquals(500000.0, result.get(0).getAmount());
        }
    }

    @Test
    public void testGetExpenseCurrentMonthEmpty() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(false); // Không có dữ liệu

            List<Expense> result = statsServices.getExpenseCurrentMonth("Chi");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Test
    public void testGetExpenseCurrentMonthFail() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            Exception exception = assertThrows(SQLException.class, () -> {
                statsServices.getExpenseCurrentMonth("Chi");
            });
            assertTrue(exception.getMessage().contains("Database error"));
        }
    }

    @Test
    public void testGetExpensesFilterByDateSuccess() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("id")).thenReturn(2);
            when(mockRs.getString("name")).thenReturn("Giải trí");
            when(mockRs.getDouble("amount")).thenReturn(300000.0);

            List<Expense> result = statsServices.getExpensesFilterByDate(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31),
                    "Chi");

            assertEquals(1, result.size());
            assertEquals("Giải trí", result.get(0).getCategoryName());
            assertEquals(300000.0, result.get(0).getAmount());
        }
    }

    @Test
    public void testGetExpensesFilterByDateEmpty() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(false); // Không có dữ liệu

            List<Expense> result = statsServices.getExpensesFilterByDate(
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31),
                    "Chi");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Test
    public void testGetExpensesFilterByDateFail() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            Exception exception = assertThrows(SQLException.class, () -> {
                statsServices.getExpensesFilterByDate(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 31),
                        "Chi");
            });
            assertTrue(exception.getMessage().contains("Database error"));
        }
    }

    @Test
    public void testGetMonthlyExpensesSuccess() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
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

    @Test
    public void testGetMonthlyExpensesEmpty() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockRs.next()).thenReturn(false); // Không có dòng dữ liệu

            List<Expense> result = statsServices.getMonthlyExpenses("EXP", 2025);

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Test
    public void testGetMonthlyExpensesFail() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (
                MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            Exception exception = assertThrows(SQLException.class, () -> {
                statsServices.getMonthlyExpenses("EXP", 2025);
            });

            assertTrue(exception.getMessage().contains("Database error"));
        }
    }

}
