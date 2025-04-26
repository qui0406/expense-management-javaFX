////import com.qlct.pojo.Transaction;
////import com.qlct.services.TransactionServices;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.DisplayName;
////
////import java.time.LocalDate;
////
////import static org.junit.jupiter.api.Assertions.*;
////
////public class TransactionServicesTest {
////    
////    private TransactionServices transactionServices;
////    private int validCategoryId;
////    private int validUserId;
////    
////    @BeforeEach
////    public void setUp() {
////        transactionServices = new TransactionServices();
////        validCategoryId = 1; // Giả định ID hợp lệ cho test
////        validUserId = 1; // Giả định ID hợp lệ cho test
////    }
////    
////    @Test
////    @DisplayName("Test amount validation - amount <= 0")
////    public void testValidateAmountLessThanOrEqualToZero() {
////        // Arrange
////        double invalidAmount = 0;
////        LocalDate validDate = LocalDate.now();
////        Transaction transaction = new Transaction(invalidAmount, validCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            transaction.validate();
////        });
////        
////        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test amount validation - amount > 1 trillion")
////    public void testValidateAmountGreaterThanOneTrillion() {
////        // Arrange
////        double invalidAmount = 1_000_000_000_001.0;
////        LocalDate validDate = LocalDate.now();
////        Transaction transaction = new Transaction(invalidAmount, validCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            transaction.validate();
////        });
////        
////        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test date validation - date is null")
////    public void testValidateDateIsNull() {
////        // Arrange
////        double validAmount = 1000;
////        LocalDate nullDate = null;
////        Transaction transaction = new Transaction(validAmount, validCategoryId, nullDate, "Test note", validUserId);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            transaction.validate();
////        });
////        
////        assertEquals("Ngày giao dịch không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test date validation - date in future")
////    public void testValidateDateInFuture() {
////        // Arrange
////        double validAmount = 1000;
////        LocalDate futureDate = LocalDate.now().plusDays(1);
////        Transaction transaction = new Transaction(validAmount, validCategoryId, futureDate, "Test note", validUserId);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            transaction.validate();
////        });
////        
////        assertEquals("Ngày giao dịch không được vượt quá ngày hiện tại!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test category validation - category <= 0")
////    public void testValidateCategoryIsInvalid() {
////        // Arrange
////        double validAmount = 1000;
////        LocalDate validDate = LocalDate.now();
////        int invalidCategoryId = 0;
////        Transaction transaction = new Transaction(validAmount, invalidCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            transaction.validate();
////        });
////        
////        assertEquals("Danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test valid transaction")
////    public void testValidTransaction() {
////        // Arrange
////        double validAmount = 1000;
////        LocalDate validDate = LocalDate.now();
////        Transaction transaction = new Transaction(validAmount, validCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            transaction.validate();
////        });
////    }
////    
////    @Test
////    @DisplayName("Test valid transaction with minimum amount")
////    public void testValidTransactionWithMinimumAmount() {
////        // Arrange
////        double validAmount = 0.01; // Số tiền nhỏ nhất hợp lệ
////        LocalDate validDate = LocalDate.now();
////        Transaction transaction = new Transaction(validAmount, validCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            transaction.validate();
////        });
////    }
////    
////    @Test
////    @DisplayName("Test valid transaction with maximum amount")
////    public void testValidTransactionWithMaximumAmount() {
////        // Arrange
////        double validAmount = 1_000_000_000_000.0; // Số tiền lớn nhất hợp lệ
////        LocalDate validDate = LocalDate.now();
////        Transaction transaction = new Transaction(validAmount, validCategoryId, validDate, "Test note", validUserId);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            transaction.validate();
////        });
////    }
////    
////    @Test
////    @DisplayName("Test valid transaction with past date")
////    public void testValidTransactionWithPastDate() {
////        // Arrange
////        double validAmount = 1000;
////        LocalDate pastDate = LocalDate.now().minusDays(1);
////        Transaction transaction = new Transaction(validAmount, validCategoryId, pastDate, "Test note", validUserId);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            transaction.validate();
////        });
////    }
////}
//
//import com.qlct.pojo.JdbcUtils;
//import com.qlct.pojo.Transaction;
//import com.qlct.pojo.User;
//import com.qlct.services.TransactionServices;
//import com.qlct.services.UserSessionServices;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//public class TransactionServicesTest {
//
//    private TransactionServices transactionServices;
//    private User mockUser;
//
//    private Connection connection;
//    private PreparedStatement preparedStatement;
//    private ResultSet resultSet;
//
//    private MockedStatic<UserSessionServices> userSessionServicesMock;
//    private MockedStatic<JdbcUtils> jdbcUtilsMock;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // Create a mock user
//        mockUser = new User(1, "testuser", "password", "Test User", "test@example.com");
//
//        // Mock UserSessionServices.getCurrentUser()
//        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
//        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//        // Mock JDBC components
//        connection = mock(Connection.class);
//        preparedStatement = mock(PreparedStatement.class);
//        resultSet = mock(ResultSet.class);
//
//        // Mock JdbcUtils.getConn()
//        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
//        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(connection);
//
//        // Mock connection methods
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(connection.getAutoCommit()).thenReturn(true);
//
//        transactionServices = new TransactionServices();
//    }
//
//    @AfterEach
//    void tearDown() {
//        userSessionServicesMock.close();
//        jdbcUtilsMock.close();
//    }
//
//    // Test getTransactionsByDateRange()
//    @Test
//    @DisplayName("Test getTransactionsByDateRange() returns transactions")
//    void testGetTransactionsByDateRange() throws SQLException {
//        // Mock ResultSet
//        LocalDate startDate = LocalDate.of(2023, 1, 1);
//        LocalDate endDate = LocalDate.of(2023, 12, 31);
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getDouble("amount")).thenReturn(1000.0);
//        when(resultSet.getInt("category_id")).thenReturn(1);
//        when(resultSet.getDate("date")).thenReturn(Date.valueOf(startDate));
//        when(resultSet.getString("note")).thenReturn("Test note");
//        when(resultSet.getInt("user_id")).thenReturn(mockUser.getId());
//
//        // Call the method
//        List<Transaction> transactions = transactionServices.getTransactionsByDateRange(startDate, endDate);
//
//        // Verify results
//        assertEquals(1, transactions.size());
//        assertEquals(1000.0, transactions.get(0).getAmount());
//        assertEquals(1, transactions.get(0).getCategoryId());
//        assertEquals(startDate, transactions.get(0).getDate());
//        assertEquals("Test note", transactions.get(0).getNote());
//        assertEquals(mockUser.getId(), transactions.get(0).getUserId());
//
//        // Verify interactions
//        verify(preparedStatement).setDate(1, Date.valueOf(startDate));
//        verify(preparedStatement).setDate(2, Date.valueOf(endDate));
//        verify(preparedStatement).setInt(3, mockUser.getId());
//    }
//
//    // Test addTransaction() with valid data
//    @Test
//    @DisplayName("Test addTransaction() with valid transaction")
//    void testAddTransactionSuccess() throws SQLException {
//        // Mock executeUpdate
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        // Create a valid transaction
//        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now(), "Test note", mockUser.getId());
//
//        // Call the method
//        boolean result = transactionServices.addTransaction(transaction);
//
//        // Verify results
//        assertTrue(result);
//
//        // Verify interactions
//        verify(preparedStatement).setDouble(1, 1000.0);
//        verify(preparedStatement).setString(2, "Test note");
//        verify(preparedStatement).setDate(3, Date.valueOf(LocalDate.now()));
//        verify(preparedStatement).setInt(4, 1);
//        verify(preparedStatement).setInt(5, mockUser.getId());
//    }
//
//    // Test addTransaction() with invalid transaction (validation fails)
//    @Test
//    @DisplayName("Test addTransaction() with invalid transaction")
//    void testAddTransactionInvalid() {
//        // Create an invalid transaction (amount <= 0)
//        Transaction invalidTransaction = new Transaction(0, 1, LocalDate.now(), "Test note", mockUser.getId());
//
//        // Verify exception
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            transactionServices.addTransaction(invalidTransaction);
//        });
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    // Test deleteTransaction()
//    @Test
//    @DisplayName("Test deleteTransaction() with valid ID")
//    void testDeleteTransactionSuccess() throws SQLException {
//        // Mock executeUpdate
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        // Call the method
//        transactionServices.deleteTransaction(1);
//
//        // Verify interactions
//        verify(preparedStatement).setInt(1, 1);
//        verify(preparedStatement).executeUpdate();
//    }
//
//    // Test updateTransaction() with valid data
//    @Test
//    @DisplayName("Test updateTransaction() with valid data")
//    void testUpdateTransactionSuccess() throws SQLException {
//        // Mock executeUpdate
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//
//        // Call the method
//        transactionServices.updateTransaction(1, 2000.0, 2, LocalDate.now(), "Updated note", mockUser.getId());
//
//        // Verify interactions
//        verify(preparedStatement).setDouble(1, 2000.0);
//        verify(preparedStatement).setInt(2, 2);
//        verify(preparedStatement).setDate(3, Date.valueOf(LocalDate.now()));
//        verify(preparedStatement).setString(4, "Updated note");
//        verify(preparedStatement).setInt(5, mockUser.getId());
//        verify(preparedStatement).setInt(6, 1);
//    }
//
//    // Test validation constraints for Transaction
//    @Test
//    @DisplayName("Test validation - amount <= 0")
//    void testValidateAmountLessThanOrEqualToZero() {
//        Transaction transaction = new Transaction(0, 1, LocalDate.now(), "Test note", mockUser.getId());
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - amount > 1 trillion")
//    void testValidateAmountGreaterThanOneTrillion() {
//        Transaction transaction = new Transaction(1_000_000_000_001.0, 1, LocalDate.now(), "Test note", mockUser.getId());
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - date is null")
//    void testValidateDateIsNull() {
//        Transaction transaction = new Transaction(1000.0, 1, null, "Test note", mockUser.getId());
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
//        assertEquals("Ngày giao dịch không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - date in future")
//    void testValidateDateInFuture() {
//        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now().plusDays(1), "Test note", mockUser.getId());
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
//        assertEquals("Ngày giao dịch không được vượt quá ngày hiện tại!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - category <= 0")
//    void testValidateCategoryInvalid() {
//        Transaction transaction = new Transaction(1000.0, 0, LocalDate.now(), "Test note", mockUser.getId());
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
//        assertEquals("Danh mục không được để trống!", exception.getMessage());
//    }
//}