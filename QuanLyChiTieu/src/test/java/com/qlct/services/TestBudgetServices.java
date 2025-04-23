// package com.qlct.services;

// import com.qlct.pojo.Budget;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import com.qlct.services.UserSessionServices;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.Assertions;
// import org.mockito.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import com.qlct.pojo.JdbcUtils;
// import com.qlct.pojo.User;
// import java.sql.CallableStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;
// import java.sql.CallableStatement;
// import java.time.LocalDate;
// import java.util.List;

// /*
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
//  */
// /**
//  *
//  * @author DELL
//  */
// @ExtendWith(MockitoExtension.class)
// public class TestBudgetServices {

//     private BudgetServices budgetServices;
//     private Connection mockConn;

//     @BeforeEach
//     public void setUp() throws Exception {
//         budgetServices = new BudgetServices();
//         mockConn = mock(Connection.class);
//     }

//     @Test
//     public void testIsValidAmount_invalidString() {
//         Assertions.assertFalse(budgetServices.isValidAmount("abc"));
//     }

//     @Test
//     public void testIsValidAmount_emptyValue() {
//         Assertions.assertFalse(budgetServices.isValidAmount(""));
//     }

//     @Test
//     public void testIsValidAmount_negativeAmount() {
//         Assertions.assertTrue(budgetServices.isValidAmount("-100000"));
//     }

//     @Test
//     public void testIsValidAmount_validDouble() {
//         Assertions.assertTrue(budgetServices.isValidAmount("123000.45"));
//     }

//     @Test
//     public void testValidateAmount_validInput() {
//         double result = budgetServices.validateAmount("100000");
//         Assertions.assertEquals(100000, result);
//     }

//     @Test
//     public void testValidateAmount_validDouble() {
//         double result = budgetServices.validateAmount("123000.500");
//         Assertions.assertEquals(123001.0, result);
//     }

//     @Test
//     public void testValidateAmount_inputWithLeadingZeros() {
//         double result = budgetServices.validateAmount("0000123");
//         Assertions.assertEquals(123, result);
//     }

//     @Test
//     public void testValidateAmount_invalidInputString() {
//         Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//             budgetServices.validateAmount("abc123");
//         });
//         Assertions.assertEquals("Giá trị nhập vào không hợp lệ", exception.getMessage());
//     }

//     @Test
//     public void testValidateAmount_emptyInput() {
//         Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//             budgetServices.validateAmount("");
//         });
//         Assertions.assertEquals("Không được để trống ô này", exception.getMessage());
//     }

//     @Test
//     public void testValidateAmount_longInput() {
//         Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//             budgetServices.validateAmount("99999999999999999999999");
//         });
//         Assertions.assertEquals("Giá trị nhập vào quá lớn", exception.getMessage());
//     }

//     @Test
//     public void testValidateInput_negativeInput() {
//         Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
//             budgetServices.validateAmount("-100");
//         });
//         Assertions.assertEquals("Không được nhập số âm.", exception.getMessage());
//     }

//     @Test
//     public void testIsBudgetValid_newBudgetMoreThanOldBudget() {
//         Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 500.0));
//     }

//     @Test
//     public void testIsBudgetValid_newBudgetLessThanOldBudget() {
//         Assertions.assertFalse(budgetServices.isBudgetValid(300.0, 500.0));
//     }

//     @Test
//     public void testIsBudgetValid_trueCase() {
//         Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 999.9));
//     }

//     @Test
//     public void testIsBudgetValid_newBudgetEqualOldBudget() {
//         Assertions.assertTrue(budgetServices.isBudgetValid(1000.0, 1000.0));
//     }

//     @Test
//     public void testIsBudgetWithinLimit_Under80Percent() {
//         Assertions.assertTrue(budgetServices.isBudgetWithinLimit(60000.0, 100000.0));
//     }

//     @Test
//     public void testIsBudgetWithinLimit_BudgetZero() {
//         Assertions.assertFalse(budgetServices.isBudgetWithinLimit(800, 0));
//     }

//     @Test
//     public void testIsBudgetWithinLimit_On80Percent() {
//         Assertions.assertFalse(budgetServices.isBudgetWithinLimit(90000.0, 100000.0));
//     }

//     @Test
//     public void testGetIdByName_found() throws SQLException {
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);

//         when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenReturn(mockStmt);
//         when(mockStmt.executeQuery()).thenReturn(mockRs);
//         when(mockRs.next()).thenReturn(true);
//         when(mockRs.getInt("id")).thenReturn(1);

//         try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//             jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

//             int result = budgetServices.getIdByName("Ăn uống");

//             assertEquals(1, result);
//             verify(mockStmt).setString(1, "Ăn uống");
//             verify(mockStmt).executeQuery();
//         }
//     }

//     @Test
//     public void testGetIdByName_notFound() throws SQLException {
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);

//         when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenReturn(mockStmt);
//         when(mockStmt.executeQuery()).thenReturn(mockRs);
//         when(mockRs.next()).thenReturn(false);

//         try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//             jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

//             int result = budgetServices.getIdByName("Nghỉ ngơi");

//             assertEquals(-1, result);
//             verify(mockStmt).setString(1, "Nghỉ ngơi");
//             verify(mockStmt).executeQuery();
//         }
//     }

//     @Test
//     public void testGetIdByName_sqlException() throws SQLException {
//         // Giả lập trường hợp SQLException xảy ra khi tạo PreparedStatement
//         when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenThrow(new SQLException("Database error"));

//         try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//             jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

//             // Kiểm tra nếu SQLException được ném ra khi gọi phương thức
//             Assertions.assertThrows(SQLException.class, () -> {
//                 budgetServices.getIdByName("Nghỉ ngơi");
//             });
//         }
//     }

//     @Test
//     public void testInsertBudgetTODB_successfulInsert() throws SQLException {
//         // Arrange
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");
//         Connection mockConn = mock(Connection.class);
//         CallableStatement mockStmtInsert = mock(CallableStatement.class);
//         PreparedStatement mockStmtSelect = mock(PreparedStatement.class);
//         ResultSet mockResultSet = mock(ResultSet.class);

//         // Mock static UserSessionServices.getCurrentUser()
//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             // Mock static JdbcUtils.getConn()
//             try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 // Giả lập truy vấn SELECT category_id
//                 when(mockConn.prepareStatement("SELECT id FROM category WHERE name = ?")).thenReturn(mockStmtSelect);
//                 when(mockStmtSelect.executeQuery()).thenReturn(mockResultSet);
//                 when(mockResultSet.next()).thenReturn(true);
//                 when(mockResultSet.getInt("id")).thenReturn(2);

//                 // Giả lập prepareStatement INSERT
//                 when(mockConn.prepareCall("INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)")).thenReturn(mockStmtInsert);

//                 BudgetServices budgetServices = new BudgetServices();

//                 // Act
//                 Assertions.assertDoesNotThrow(()
//                         -> budgetServices.insertBudgetTODB("5000", "Chi", "Ăn uống")
//                 );

//                 // Assert
//                 verify(mockStmtInsert).setDouble(1, 5000.0);
//                 verify(mockStmtInsert).setInt(2, 1);
//                 verify(mockStmtInsert).setString(3, "Chi");
//                 verify(mockStmtInsert).setInt(4, 2);
//                 verify(mockStmtInsert).executeUpdate();
//             }
//         }
//     }

//     @Test
//     public void testInsertBudgetTODB_withSQLException() throws SQLException {
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmtSelect = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);

//         CallableStatement mockStmtInsert = mock(CallableStatement.class);

//         // Mock cho truy vấn SELECT
//         when(mockConn.prepareStatement(eq("SELECT id FROM category WHERE name = ?"))).thenReturn(mockStmtSelect);
//         when(mockStmtSelect.executeQuery()).thenReturn(mockRs);
//         when(mockRs.next()).thenReturn(true); // Giả lập có kết quả
//         when(mockRs.getInt("id")).thenReturn(1); // ID giả

//         // Mock cho INSERT: ném SQLException
//         when(mockConn.prepareCall("INSERT INTO budget (amount, user_id ,type, category_id) VALUES (?,?,?,?)"))
//                 .thenThrow(new SQLException("Database error"));

//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcUtilsMockedStatic = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMockedStatic.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 Assertions.assertThrows(SQLException.class, () -> {
//                     BudgetServices budgetServices = new BudgetServices();
//                     budgetServices.insertBudgetTODB("50000", "Thu", "Ăn uống");
//                 });
//             }
//         }
//     }

//     @Test
//     public void testTryInsertBudget_success() throws SQLException {
//         BudgetServices service = spy(new BudgetServices());

//         // Giả lập insertBudgetTODB không ném exception
//         doNothing().when(service).insertBudgetTODB("5000", "Chi", "Ăn uống");

//         boolean result = service.tryInsertBudget("5000", "Chi", "Ăn uống");

//         assertTrue(result);
//         verify(service).insertBudgetTODB("5000", "Chi", "Ăn uống");
//     }

//     @Test
//     public void testTryInsertBudget_withSQLException() throws SQLException {
//         BudgetServices service = spy(new BudgetServices());

//         // Giả lập insertBudgetTODB ném ra SQLException
//         doThrow(new SQLException("Fake error")).when(service).insertBudgetTODB("5000", "Thu", "Ăn uống");

//         boolean result = service.tryInsertBudget("5000", "Thu", "Ăn uống");

//         assertFalse(result);
//         verify(service).insertBudgetTODB("5000", "Thu", "Ăn uống");
//     }

//     @Test
//     public void testSumBudgetInMonth_success() throws SQLException {
//         // Mock các đối tượng cần thiết
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         // Mock UserSessionServices
//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             // Mock JdbcUtils.getConn()
//             try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 // Mock prepareStatement và ResultSet
//                 when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//                 when(mockStmt.executeQuery()).thenReturn(mockRs);

//                 // Giả lập kết quả trả về từ ResultSet
//                 when(mockRs.next()).thenReturn(true);
//                 when(mockRs.getDouble(1)).thenReturn(12345.67); // Tổng chi tiêu giả lập

//                 BudgetServices service = new BudgetServices();
//                 double result = service.sumBudgetInMonth();

//                 assertEquals(12345.67, result, 0.001); // So sánh kết quả mong đợi
//             }
//         }
//     }

//     @Test
//     public void testSumBudgetInMonth_noData() throws SQLException {
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//                 when(mockStmt.executeQuery()).thenReturn(mockRs);
//                 when(mockRs.next()).thenReturn(false); // Không có dòng nào

//                 BudgetServices service = new BudgetServices();
//                 double result = service.sumBudgetInMonth();

//                 assertEquals(0.0, result, 0.001); // Mong đợi trả về 0
//             }
//         }
//     }

//     @Test
//     public void testIsBudgetExistForCurrentMonth_existTrue() throws SQLException {
//         // Mock các đối tượng cần thiết
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         // Mock giá trị trả về của ResultSet
//         when(mockRs.next()).thenReturn(true);
//         when(mockRs.getInt(1)).thenReturn(10); // ID giả định

//         // Mock JDBC
//         when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//         when(mockStmt.executeQuery()).thenReturn(mockRs);

//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
//                 jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 BudgetServices service = new BudgetServices();
//                 boolean result = service.isBudgetExistForCurrentMonth(5, true);

//                 assertTrue(result); // Mong đợi trả về true
//             }
//         }
//     }

//     @Test
//     public void testIsBudgetExistForCurrentMonth_notExistFalse() throws SQLException {
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         when(mockRs.next()).thenReturn(false); // Không có kết quả
//         when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//         when(mockStmt.executeQuery()).thenReturn(mockRs);

//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
//                 jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 BudgetServices service = new BudgetServices();
//                 boolean result = service.isBudgetExistForCurrentMonth(5, false);

//                 assertFalse(result); // Mong đợi trả về false
//             }
//         }
//     }

//     @Test
//     public void testGetBudgetsForCurrentUser_returnsBudgetList() throws SQLException {
//         // Mock dữ liệu
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         ResultSet mockRs = mock(ResultSet.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         // Giả lập dữ liệu trả về từ ResultSet
//         when(mockRs.next()).thenReturn(true, false); // chỉ có 1 dòng dữ liệu
//         when(mockRs.getInt("id")).thenReturn(101);
//         when(mockRs.getDouble("amount")).thenReturn(5000.0);
//         when(mockRs.getInt("user_id")).thenReturn(1);
//         when(mockRs.getString("type")).thenReturn("Chi");
//         when(mockRs.getObject(eq("date"), eq(LocalDate.class))).thenReturn(LocalDate.of(2024, 4, 1));
//         when(mockRs.getInt("category_id")).thenReturn(10);

//         // Gắn mock với connection và statement
//         when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
//         when(mockStmt.executeQuery()).thenReturn(mockRs);

//         // Mock static cho UserSessionServices và JdbcUtils
//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 // Gọi phương thức và kiểm tra kết quả
//                 BudgetServices service = new BudgetServices();
//                 List<Budget> result = service.getBudgetsForCurrentUser();

//                 assertEquals(1, result.size());

//                 Budget budget = result.get(0);
//                 assertEquals(101, budget.getId());
//                 assertEquals(5000.0, budget.getAmountBudget());
//                 assertEquals(1, budget.getUserId());
//                 assertEquals("Chi", budget.getType());
//                 assertEquals(LocalDate.of(2024, 4, 1), budget.getCreatedDate());
//                 assertEquals(10, budget.getCategoryId());
//             }
//         }
//     }

//     @Test
//     public void testGetBudgetsForCurrentUser_sqlException_returnsEmptyList() throws SQLException {
//         // Mock dữ liệu
//         Connection mockConn = mock(Connection.class);
//         PreparedStatement mockStmt = mock(PreparedStatement.class);
//         User mockUser = new User(1, "John", "Doe", "johndoe", "johndoe@example.com");

//         // Khi gọi prepareStatement thì ném SQLException
//         when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

//         // Mock static cho UserSessionServices và JdbcUtils
//         try (MockedStatic<UserSessionServices> userSessionMock = mockStatic(UserSessionServices.class)) {
//             userSessionMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

//             try (MockedStatic<JdbcUtils> jdbcUtilsMock = mockStatic(JdbcUtils.class)) {
//                 jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConn);

//                 // Gọi phương thức
//                 BudgetServices service = new BudgetServices();
//                 List<Budget> result = service.getBudgetsForCurrentUser();

//                 // Kết quả trả về là danh sách rỗng
//                 assertNotNull(result);
//                 assertTrue(result.isEmpty());
//             }
//         }
//     }

// }
