package com.qlct.services;

import com.qlct.pojo.Budget;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.CallableStatement;
import java.time.LocalDate;
import java.util.List;

/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author DELL
 */
@ExtendWith(MockitoExtension.class)
public class TestBudgetServices {

    private BudgetServices budgetServices;
    private Connection mockConn;

    @BeforeEach
    public void setUp() throws Exception {
        budgetServices = new BudgetServices();
        mockConn = mock(Connection.class);
    }
    
    // Test input budget

    @Test
    public void testIsValidAmountInvalidString() {
        Assertions.assertFalse(budgetServices.isValidAmount("abc"));
    }

    @Test
    public void testIsValidAmountEmptyValue() {
        Assertions.assertFalse(budgetServices.isValidAmount(""));
    }

    @Test
    public void testIsValidAmountNegativeAmount() {
        Assertions.assertTrue(budgetServices.isValidAmount("-100000"));
    }

    @Test
    public void testIsValidAmountValidDouble() {
        Assertions.assertTrue(budgetServices.isValidAmount("123000.45"));
    }

    @Test
    public void testValidateAmountvalidInput() {
        double result = budgetServices.validateAmount("100000");
        Assertions.assertEquals(100000, result);
    }

    @Test
    public void testValidateAmountinputWithLeadingZeros() {
        double result = budgetServices.validateAmount("0000123");
        Assertions.assertEquals(123, result);
    }

    @Test
    public void testValidateAmount_invalidInputString() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("abc123");
        });
        Assertions.assertEquals("Giá trị nhập vào không hợp lệ", exception.getMessage());
    }

    @Test
    public void testValidateAmountEmptyInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("");
        });
        Assertions.assertEquals("Không được để trống ô này", exception.getMessage());
    }

    @Test
    public void testValidateAmountLongInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("99999999999999999999999");
        });
        Assertions.assertEquals("Giá trị nhập vào quá lớn", exception.getMessage());
    }

    @Test
    public void testValidateInputNegativeInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("-100");
        });
        Assertions.assertEquals("Không được nhập số âm.", exception.getMessage());
    }
    
    
    // Budget 80%
    @Test
    public void testIsBudgetWithinLimitUnder80Percent() {
        Assertions.assertTrue(budgetServices.isBudgetWithinLimit(60000.0, 100000.0));
    }

    @Test
    public void testIsBudgetWithinLimit_BudgetZero() {
        Assertions.assertFalse(budgetServices.isBudgetWithinLimit(800, 0));
    }

    @Test
    public void testIsBudgetWithinLimit_On80Percent() {
        Assertions.assertFalse(budgetServices.isBudgetWithinLimit(90000.0, 100000.0));
    }

    @Test
    public void testGetIdByNameFound() throws SQLException {
        User mockUser = new User(1, "Quí", "Trần", "qui123", "anhqui@gmail.com");
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);
                when(mockConn.prepareStatement("SELECT c.id "
                        + "FROM category c "
                        + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                        + "WHERE c.name = ? AND (c.is_default = 1 OR (c.is_default = 0 AND uc.user_id = ?)) "
                        + "LIMIT 1;")).thenReturn(mockStmt);
                when(mockStmt.executeQuery()).thenReturn(mockRs);
                when(mockRs.next()).thenReturn(true);
                when(mockRs.getInt("id")).thenReturn(1);
                
                BudgetServices budgetServices = new BudgetServices();
                int actualId = budgetServices.getIdByName("Ăn uống", mockUser.getId());

                assertEquals(1, actualId);

                verify(mockStmt).setString(1, "Ăn uống");
                verify(mockStmt).setInt(2, 1);
                verify(mockStmt).executeQuery();
            }
        }
    }

    @Test
    public void testGetIdByNameNotFound() throws SQLException {
        // Arrange
        User mockUser = new User(1, "Quí", "Trần", "qui123", "anhqui@gmail.com");
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                when(mockConn.prepareStatement("SELECT c.id "
                        + "FROM category c "
                        + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                        + "WHERE c.name = ? AND (c.is_default = 1 OR (c.is_default = 0 AND uc.user_id = ?)) "
                        + "LIMIT 1;")).thenReturn(mockStmt);

                when(mockStmt.executeQuery()).thenReturn(mockRs);
                when(mockRs.next()).thenReturn(false);

                BudgetServices budgetServices = new BudgetServices();
                int actualId = budgetServices.getIdByName("Nghỉ dưỡng", mockUser.getId());
                assertEquals(-1, actualId); 

                verify(mockStmt).setString(1, "Nghỉ dưỡng");
                verify(mockStmt).setInt(2, 1);
                verify(mockStmt).executeQuery();
            }
        }
    }

    @Test
    public void testGetTotalBudgetForCategoryFound() throws SQLException {
        // Arrange
        int categoryId = 2;
        int userId = 1;
        double expectedTotal = 5000.0;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockConn.prepareStatement("SELECT SUM(b.amount) "
                    + "FROM budget b "
                    + "WHERE MONTH(b.date) = ? AND YEAR(b.date) = ? "
                    + "AND b.user_id = ? AND b.category_id = ?"))
                    .thenReturn(mockStmt);

            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getDouble(1)).thenReturn(expectedTotal);

            BudgetServices budgetServices = new BudgetServices();

            double actualTotal = budgetServices.getTotalBudgetForCategory(categoryId, userId);

            assertEquals(expectedTotal, actualTotal, 0.001);

            verify(mockStmt).setInt(eq(1), anyInt()); 
            verify(mockStmt).setInt(eq(2), anyInt()); 
            verify(mockStmt).setInt(3, userId);
            verify(mockStmt).setInt(4, categoryId);
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetTotalBudgetForCategoryNotFound() throws SQLException {
        int categoryId = 2;
        int userId = 1;
        double expectedTotal = 0.0;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockConn.prepareStatement("SELECT SUM(b.amount) "
                    + "FROM budget b "
                    + "WHERE MONTH(b.date) = ? AND YEAR(b.date) = ? "
                    + "AND b.user_id = ? AND b.category_id = ?"))
                    .thenReturn(mockStmt);

            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false); 

            BudgetServices budgetServices = new BudgetServices();
            
            double actualTotal = budgetServices.getTotalBudgetForCategory(categoryId, userId);

            assertEquals(expectedTotal, actualTotal, 0.001);

            verify(mockStmt).setInt(eq(1), anyInt());
            verify(mockStmt).setInt(eq(2), anyInt());
            verify(mockStmt).setInt(3, userId);
            verify(mockStmt).setInt(4, categoryId);
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetTotalExpensesForCategoryFound() throws SQLException {
        int categoryId = 3;
        int userId = 1;
        double expectedTotal = 3000.0;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockConn.prepareStatement("SELECT SUM(t.amount) "
                    + "FROM transaction t "
                    + "WHERE MONTH(t.date) = ? AND YEAR(t.date) = ? "
                    + "AND t.user_id = ? AND t.category_id = ?"))
                    .thenReturn(mockStmt);

            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getDouble(1)).thenReturn(expectedTotal);

            BudgetServices budgetServices = new BudgetServices();

            double actualTotal = budgetServices.getTotalExpensesForCategory(categoryId, userId);

            assertEquals(expectedTotal, actualTotal, 0.001);

            verify(mockStmt).setInt(eq(1), anyInt()); 
            verify(mockStmt).setInt(eq(2), anyInt()); 
            verify(mockStmt).setInt(3, userId);
            verify(mockStmt).setInt(4, categoryId);
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetTotalExpensesForCategory_notFound() throws SQLException {
        // Arrange
        int categoryId = 3;
        int userId = 1;
        double expectedTotal = 0.0;

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

            when(mockConn.prepareStatement("SELECT SUM(t.amount) "
                    + "FROM transaction t "
                    + "WHERE MONTH(t.date) = ? AND YEAR(t.date) = ? "
                    + "AND t.user_id = ? AND t.category_id = ?"))
                    .thenReturn(mockStmt);

            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false); // Không có dữ liệu

            BudgetServices budgetServices = new BudgetServices();

            double actualTotal = budgetServices.getTotalExpensesForCategory(categoryId, userId);
            assertEquals(expectedTotal, actualTotal, 0.001);

            verify(mockStmt).setInt(eq(1), anyInt());
            verify(mockStmt).setInt(eq(2), anyInt());
            verify(mockStmt).setInt(3, userId);
            verify(mockStmt).setInt(4, categoryId);
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetBudgetsForCurrentUser_returnsBudgetList() throws SQLException {
        // Mock dữ liệu
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        when(mockRs.next()).thenReturn(true, false); 
        when(mockRs.getInt("id")).thenReturn(101);
        when(mockRs.getDouble("amount")).thenReturn(5000.0);
        when(mockRs.getInt("user_id")).thenReturn(1);
        when(mockRs.getString("type")).thenReturn("Chi");
        when(mockRs.getObject(eq("date"), eq(LocalDate.class))).thenReturn(LocalDate.of(2024, 4, 1));
        when(mockRs.getInt("category_id")).thenReturn(10);

        // Gắn mock với connection và statement
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Mock static cho UserSessionServices và JdbcUtils
        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                // Gọi phương thức và kiểm tra kết quả
                BudgetServices service = new BudgetServices();
                List<Budget> result = service.getBudgetsForCurrentUser();

                assertEquals(1, result.size());

                Budget budget = result.get(0);
                assertEquals(101, budget.getId());
                assertEquals(5000.0, budget.getAmountBudget());
                assertEquals(1, budget.getUserId());
                assertEquals("Chi", budget.getType());
                assertEquals(LocalDate.of(2024, 4, 1), budget.getCreatedDate());
                assertEquals(10, budget.getCategoryId());
            }
        }
    }

    @Test
    public void testGetBudgetsForCurrentUser_sqlException_returnsEmptyList() throws SQLException {
        // Mock dữ liệu
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        // Khi gọi prepareStatement thì ném SQLException
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Mock static cho UserSessionServices và JdbcUtils
        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                // Gọi phương thức
                BudgetServices service = new BudgetServices();
                List<Budget> result = service.getBudgetsForCurrentUser();

                // Kết quả trả về là danh sách rỗng
                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }
    }

    @Test
    public void testInsertBudgetTODB_Success() throws SQLException {
        // Mock input
        String input = "5000";
        String budgetType = "Expense";
        String categoryName = "Food";

        // Mock các dependency
        Connection mockConn = mock(Connection.class);
        CallableStatement mockStmt = mock(CallableStatement.class);

        // Mock kết nối trả về CallableStatement
        when(mockConn.prepareCall("INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)"))
                .thenReturn(mockStmt);

        // Giả lập hành vi JdbcUtils.getConn() để trả về mockConn
        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            // Giả lập UserSessionServices
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);

            try (MockedStatic<UserSessionServices> userSessionServicesMockedStatic = mockStatic(UserSessionServices.class)) {
                userSessionServicesMockedStatic.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

                // Giả lập hàm getIdByName
                BudgetServices budgetServices = spy(new BudgetServices());
                doReturn(10).when(budgetServices).getIdByName(categoryName, 1);

                // Gọi hàm cần test
                budgetServices.insertBudgetTODB(input, budgetType, categoryName);

                // Verify đã gọi đúng hành động
                verify(mockStmt).setDouble(1, 5000.0);
                verify(mockStmt).setInt(2, 1);
                verify(mockStmt).setString(3, budgetType);
                verify(mockStmt).setInt(4, 10);
                verify(mockStmt).executeUpdate();
            }
        }
    }

//    @Test
//    public void testInsertBudgetTODB_ThrowsSQLException() throws SQLException {
//        // Mock input
//        String input = "5000";
//        String budgetType = "Expense";
//        String categoryName = "Food";
//
//        // Mock các dependency
//        Connection mockConn = mock(Connection.class);
//
//        // Giả lập lỗi khi gọi prepareCall
//        when(mockConn.prepareCall(anyString())).thenThrow(new SQLException("Database error"));
//
//        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);
//
//            // Mock UserSessionServices
//            User mockUser = mock(User.class);
//            when(mockUser.getId()).thenReturn(1);
//
//            try (MockedStatic<UserSessionServices> userSessionServicesMockedStatic = mockStatic(UserSessionServices.class)) {
//                userSessionServicesMockedStatic.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//                // Giả lập hàm getIdByName
//                BudgetServices budgetServices = spy(new BudgetServices());
//                doReturn(10).when(budgetServices).getIdByName(categoryName, 1);
//
//                // Gọi hàm và expect SQLException
//                SQLException thrown = assertThrows(SQLException.class, () -> {
//                    budgetServices.insertBudgetTODB(input, budgetType, categoryName);
//                });
//
//                // Kiểm tra nội dung exception
//                assertTrue(thrown.getMessage().contains("Không thể thêm ngân sách vào cơ sở dữ liệu!"));
//                assertTrue(thrown.getCause().getMessage().contains("Database error"));
//            }
//        }
//    }

    @Test
    public void testTryInsertBudget_success() throws SQLException {
        BudgetServices service = spy(new BudgetServices());

        // Mock insertBudgetTODB không ném exception
        doNothing().when(service).insertBudgetTODB(anyString(), anyString(), anyString());

        // Act
        boolean result = service.tryInsertBudget("5000", "Chi", "Ăn uống");

        // Assert
        assertTrue(result);
        verify(service).insertBudgetTODB("5000", "Chi", "Ăn uống");
    }

//    @Test
//    public void testTryInsertBudget_withSQLException() throws SQLException {
//        BudgetServices service = spy(new BudgetServices());
//
//        // Mock insertBudgetTODB ném SQLException
//        doThrow(new SQLException("Fake error")).when(service).insertBudgetTODB(anyString(), anyString(), anyString());
//
//        // Act
//        boolean result = service.tryInsertBudget("5000", "Thu", "Ăn uống");
//
//        // Assert
//        assertFalse(result);
//        verify(service).insertBudgetTODB("5000", "Thu", "Ăn uống");
//    }

    @Test
    public void testSumBudgetInMonth_success() throws SQLException {
        // Mock các đối tượng cần thiết
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        // Mock UserSessionServices
        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            // Mock JdbcUtils.getConn()
            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                // Mock prepareStatement và ResultSet
                when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
                when(mockStmt.executeQuery()).thenReturn(mockRs);

                // Giả lập kết quả trả về từ ResultSet
                when(mockRs.next()).thenReturn(true);
                when(mockRs.getDouble(1)).thenReturn(12345.67); // Tổng chi tiêu giả lập

                BudgetServices service = new BudgetServices();
                double result = service.sumBudgetInMonth();

                assertEquals(12345.67, result, 0.001); // So sánh kết quả mong đợi
            }
        }
    }

    @Test
    public void testSumBudgetInMonthNoData() throws SQLException {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
                when(mockStmt.executeQuery()).thenReturn(mockRs);
                when(mockRs.next()).thenReturn(false); // Không có dòng nào

                BudgetServices service = new BudgetServices();
                double result = service.sumBudgetInMonth();

                assertEquals(0.0, result, 0.001); // Mong đợi trả về 0
            }
        }
    }

    @Test
    public void testSumMoneyUsedInMonth_success() throws SQLException {
        // Mock các đối tượng cần thiết
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        // Mock UserSessionServices
        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            // Mock JdbcUtils.getConn()
            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                // Mock prepareStatement và ResultSet
                when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
                when(mockStmt.executeQuery()).thenReturn(mockRs);

                // Giả lập kết quả trả về từ ResultSet
                when(mockRs.next()).thenReturn(true);
                when(mockRs.getDouble(1)).thenReturn(12345.67); // Tổng chi tiêu giả lập

                // Khởi tạo service và gọi phương thức
                BudgetServices service = new BudgetServices();
                double result = service.sumMoneyUsedInMonth();

                // Kiểm tra kết quả trả về
                assertEquals(12345.67, result, 0.001); // So sánh kết quả mong đợi
            }
        }
    }

    @Test
    public void testSumMoneyUsedInMonth_noData() throws SQLException {
        // Mock các đối tượng cần thiết
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        // Mock UserSessionServices
        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            // Mock JdbcUtils.getConn()
            try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                // Mock prepareStatement và ResultSet
                when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
                when(mockStmt.executeQuery()).thenReturn(mockRs);

                // Giả lập không có dữ liệu trả về từ ResultSet
                when(mockRs.next()).thenReturn(false); // Không có dòng nào

                // Khởi tạo service và gọi phương thức
                BudgetServices service = new BudgetServices();
                double result = service.sumMoneyUsedInMonth();

                // Kiểm tra kết quả trả về khi không có dữ liệu
                assertEquals(0.0, result, 0.001); // Mong đợi trả về 0
            }
        }
    }

    @Test
    public void testUpdateBudgetTODB_Success() throws SQLException {
        // Mock input
        String input = "5000";
        int budgetId = 1;

        // Mock các dependency
        Connection mockConn = mock(Connection.class);
        CallableStatement mockUpdateStmt = mock(CallableStatement.class);

        // Mock conn.prepareStatement
        when(mockConn.prepareCall("UPDATE budget SET amount =  ? WHERE id = ? AND user_id = ?")).thenReturn(mockUpdateStmt);

        // Mock JdbcUtils.getConn
        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            // Mock UserSessionServices
            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);

            try (MockedStatic<UserSessionServices> userSessionServicesMockedStatic = mockStatic(UserSessionServices.class)) {
                userSessionServicesMockedStatic.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

                // Gọi hàm cần test
                BudgetServices budgetServices = spy(new BudgetServices());
                budgetServices.updateBudgetTODB(input, budgetId);

                // Verify update
                verify(mockUpdateStmt).setDouble(1, 5000.0);
                verify(mockUpdateStmt).setInt(2, budgetId);
                verify(mockUpdateStmt).setInt(3, 1);
                verify(mockUpdateStmt).executeUpdate();
            }
        }
    }

//    @Test
//    public void testUpdateBudgetTODB_ThrowsSQLException() throws SQLException {
//        // Mock input
//        String input = "5000";
//        int budgetId = 1;
//
//        // Mock các dependency
//        Connection mockConn = mock(Connection.class);
//
//        // Giả lập lỗi khi gọi prepareCall
//        when(mockConn.prepareCall(anyString())).thenThrow(new SQLException("Database update error"));
//
//        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);
//
//            // Mock UserSessionServices
//            User mockUser = mock(User.class);
//            when(mockUser.getId()).thenReturn(1);
//
//            try (MockedStatic<UserSessionServices> userSessionServicesMockedStatic = mockStatic(UserSessionServices.class)) {
//                userSessionServicesMockedStatic.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//                // Gọi hàm và expect SQLException
//                BudgetServices budgetServices = spy(new BudgetServices());
//                SQLException thrown = assertThrows(SQLException.class, () -> {
//                    budgetServices.updateBudgetTODB(input, budgetId);
//                });
//
//                // Kiểm tra nội dung exception
//                assertTrue(thrown.getMessage().contains("Không thể cập nhật ngân sách vào cơ sở dữ liệu!"));
//                assertTrue(thrown.getCause().getMessage().contains("Database update error"));
//            }
//        }
//    }

    @Test
    public void testTryUpdateBudget_success() throws SQLException {
        BudgetServices service = spy(new BudgetServices());

        // Mock updateBudgetTODB không ném exception
        doNothing().when(service).updateBudgetTODB(anyString(), anyInt());

        // Act
        boolean result = service.tryUpdateBudget(1, "5000", "Chi", 10);

        // Assert
        assertTrue(result);
        verify(service).updateBudgetTODB("5000", 1);
    }

    @Test
    public void testTryUpdateBudget_withSQLException() throws SQLException {
        BudgetServices service = spy(new BudgetServices());

        // Mock updateBudgetTODB ném SQLException
        doThrow(new SQLException("Fake error")).when(service).updateBudgetTODB(anyString(), anyInt());

        // Act
        boolean result = service.tryUpdateBudget(1, "5000", "Thu", 10);

        // Assert
        assertFalse(result);
        verify(service).updateBudgetTODB("5000", 1);
    }

    @Test
    public void testIsBudgetExistForCurrentMonth_existTrue() throws SQLException {
        // Mock các đối tượng cần thiết
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        // Mock giá trị trả về của ResultSet
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(10); // ID giả định

        // Mock JDBC
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
                jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                BudgetServices service = new BudgetServices();
                boolean result = service.isBudgetExistForCurrentMonth(5, true);

                assertTrue(result); // Mong đợi trả về true
            }
        }
    }

    @Test
    public void testIsBudgetExistForCurrentMonth_notExistFalse() throws SQLException {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        when(mockRs.next()).thenReturn(false); // Không có kết quả
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
                jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

                BudgetServices service = new BudgetServices();
                boolean result = service.isBudgetExistForCurrentMonth(5, false);

                assertFalse(result); // Mong đợi trả về false
            }
        }
    }

}
