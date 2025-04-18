package com.qlct.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import java.sql.ResultSet;
import static org.mockito.Mockito.*;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private BudgetServices budgetServices = new BudgetServices();

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

//    @Test
//    void testInsertBudgetToDB_withNullUser() throws Exception {
//        // Arrange
//        String input = "12345.67";
//        String budgetType = "income";
//        String categoryName = "Food";
//
//        try (
//                MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> mockedSession = mockStatic(UserSessionServices.class)) {
//            BudgetServices service = spy(new BudgetServices());
//
//            Connection mockConn = mock(Connection.class);
//            PreparedStatement mockStm = mock(PreparedStatement.class);
//
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(mockConn);
//            when(mockConn.prepareStatement(anyString())).thenReturn(mockStm);
//
//            // Case where getCurrentUser returns null
//            mockedSession.when(UserSessionServices::getCurrentUser).thenReturn(null);
//
//            // mock getIdByName
//            doReturn(88).when(service).getIdByName(categoryName);
//
//            // Act & Assert: Check if exception or appropriate behavior happens when user is null
//            assertThrows(NullPointerException.class, () -> service.insertBudgetTODB(input, budgetType, categoryName));
//        }
//    }
//
//    @Test
//    void testInsertBudgetToDB_withMockedUser() throws Exception {
//        // Arrange
//        String input = "12345.67";
//        String budgetType = "income";
//        String categoryName = "Food";
//
//        try (
//                MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class); MockedStatic<UserSessionServices> mockedSession = mockStatic(UserSessionServices.class)) {
//            BudgetServices service = spy(new BudgetServices());
//
//            Connection mockConn = mock(Connection.class);
//            PreparedStatement mockStm = mock(PreparedStatement.class);
//
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(mockConn);
//            when(mockConn.prepareStatement(anyString())).thenReturn(mockStm);
//
//            // Case where getCurrentUser returns a mocked user object
//            User mockUser = mock(User.class);
//            when(mockUser.getId()).thenReturn(999);
//            mockedSession.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//            // mock getIdByName
//            doReturn(88).when(service).getIdByName(categoryName);
//
//            // Act
//            service.insertBudgetTODB(input, budgetType, categoryName);
//
//            // Assert
//            verify(mockStm).setDouble(1, 12345.67);
//            verify(mockStm).setInt(2, 999);
//            verify(mockStm).setString(3, budgetType);
//            verify(mockStm).setInt(4, 88);
//            verify(mockStm).executeUpdate();
//            verify(mockStm).close(); // auto-close
//        }
//    }

//    private MockedStatic<UserSessionServices> userSessionMock;
//    private MockedStatic<JdbcUtils> jdbcUtilsMock;
//    @Mock
//    private Connection connection;
//
//    @Mock
//    private PreparedStatement preparedStatement;
//
//    @Mock
//    private ResultSet resultSet;
//
//    @Mock
//    private User mockUser;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        userSessionMock = mockStatic(UserSessionServices.class);
//        jdbcUtilsMock = mockStatic(JdbcUtils.class);
//
//        when(UserSessionServices.getCurrentUser()).thenReturn(mockUser);
//        when(mockUser.getId()).thenReturn(1);
//        when(JdbcUtils.getConn()).thenReturn(connection);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        userSessionMock.close();
//        jdbcUtilsMock.close();
//    }
//
//    
//    @Test
//    public void testGetIdByName_ReturnsCorrectId() throws Exception {
//        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt("id")).thenReturn(99);
//
//        int result = budgetServices.getIdByName("Ăn uống");
//        assertEquals(99, result);
//    }
//
//    @Mock
//    private Connection mockConnection;
//
//    @Mock
//    private PreparedStatement mockStatement;
//
//    @Mock
//    private UserSessionServices userSessionServices;
}
