package com.qlct.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    public void testIsValidAmount_invalidString() {
        Assertions.assertFalse(budgetServices.isValidAmount("abc"));
    }

    @Test
    public void testIsValidAmount_emptyValue() {
        Assertions.assertFalse(budgetServices.isValidAmount(""));
    }

    @Test
    public void testIsValidAmount_negativeAmount() {
        Assertions.assertTrue(budgetServices.isValidAmount("-100000"));
    }

    @Test
    public void testIsValidAmount_validDouble() {
        Assertions.assertTrue(budgetServices.isValidAmount("123000.45"));
    }

    @Test
    public void testValidateAmount_validInput() {
        double result = budgetServices.validateAmount("100000");
        Assertions.assertEquals(100000, result);
    }

    @Test
    public void testValidateAmount_validDouble() {
        double result = budgetServices.validateAmount("123000.500");
        Assertions.assertEquals(123001.0, result);
    }

    @Test
    public void testValidateAmount_inputWithLeadingZeros() {
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
    public void testValidateAmount_emptyInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("");
        });
        Assertions.assertEquals("Không được để trống ô này", exception.getMessage());
    }

    @Test
    public void testValidateAmount_longInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("99999999999999999999999");
        });
        Assertions.assertEquals("Giá trị nhập vào quá lớn", exception.getMessage());
    }

    @Test
    public void testValidateInput_negativeInput() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            budgetServices.validateAmount("-100");
        });
        Assertions.assertEquals("Không được nhập số âm.", exception.getMessage());
    }

    @Test
    public void testIsBudgetValid_newBudgetMoreThanOldBudget() {
        Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 500.0));
    }

    @Test
    public void testIsBudgetValid_newBudgetLessThanOldBudget() {
        Assertions.assertFalse(budgetServices.isBudgetValid(300.0, 500.0));
    }

    @Test
    public void testIsBudgetValid_trueCase() {
        Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 999.9));
    }

    @Test
    public void testIsBudgetValid_newBudgetEqualOldBudget() {
        Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 1000.0));
    }

    @Test
    public void testIsBudgetWithinLimit_Under80Percent() {
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
    public void testGetIdByName_found() throws SQLException {
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("id")).thenReturn(1);

        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            int result = budgetServices.getIdByName("Ăn uống");

            assertEquals(1, result);
            verify(mockStmt).setString(1, "Ăn uống");
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetIdByName_notFound() throws SQLException {
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            int result = budgetServices.getIdByName("Nghỉ ngơi");

            assertEquals(-1, result);
            verify(mockStmt).setString(1, "Nghỉ ngơi");
            verify(mockStmt).executeQuery();
        }
    }

    @Test
    public void testGetIdByName_sqlException() throws SQLException {
        // Giả lập trường hợp SQLException xảy ra khi tạo PreparedStatement
        when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenThrow(new SQLException("Database error"));

        try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
            jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

            // Kiểm tra nếu SQLException được ném ra khi gọi phương thức
            Assertions.assertThrows(SQLException.class, () -> {
                budgetServices.getIdByName("Nghỉ ngơi");
            });
        }
    }

    @Test
    public void testInsertBudgetTODB_successfulInsert() throws SQLException {
        // Mock các đối tượng cần thiết
        Connection mockConn1 = mock(Connection.class);
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");
        PreparedStatement mockStmtSelect = mock(PreparedStatement.class);
        PreparedStatement mockStmtInsert = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        // Mock câu SELECT id từ category
        when(mockConn1.prepareStatement(eq("SELECT id FROM category WHERE name = ?"))).thenReturn(mockStmtSelect);
        when(mockStmtSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("id")).thenReturn(2); // ID giả cho "Ăn uống"

        // Mock câu INSERT (không ném exception)
        when(mockConn1.prepareStatement(eq("INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)")))
                .thenReturn(mockStmtInsert);

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn1);

                BudgetServices budgetServices = new BudgetServices();
                // Gọi hàm cần test, không mong đợi exception
                Assertions.assertDoesNotThrow(() -> {
                    budgetServices.insertBudgetTODB("50000", "Chi", "Ăn uống");
                });

                // Kiểm tra xem lệnh insert có được gọi đúng không
                verify(mockStmtInsert).setDouble(1, 50000.0);
                verify(mockStmtInsert).setInt(2, 1); // ID user
                verify(mockStmtInsert).setString(3, "Thu");
                verify(mockStmtInsert).setInt(4, 2); // ID category
                verify(mockStmtInsert).executeUpdate();
            }
        }
    }

    @Test
    public void testInsertBudgetTODB_withSQLException() throws SQLException {
        User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmtSelect = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        PreparedStatement mockStmtInsert = mock(PreparedStatement.class);

        // Mock cho truy vấn SELECT
        when(mockConn.prepareStatement(eq("SELECT id FROM category WHERE name = ?"))).thenReturn(mockStmtSelect);
        when(mockStmtSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true); // Giả lập có kết quả
        when(mockRs.getInt("id")).thenReturn(1); // ID giả

        // Mock cho INSERT: ném SQLException
        when(mockConn.prepareStatement(eq("INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)")))
                .thenThrow(new SQLException("Database error"));

        try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
            userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

            try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
                jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

                Assertions.assertThrows(SQLException.class, () -> {
                    BudgetServices budgetServices = new BudgetServices();
                    budgetServices.insertBudgetTODB("abc", "Chi", "Ăn uống");
                });
            }
        }
    }
}
