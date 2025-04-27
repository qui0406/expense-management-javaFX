
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.Transaction;
import com.qlct.pojo.User;
import com.qlct.services.TransactionServices;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TransactionServicesTest {

    private TransactionServices transactionServices;
    private User mockUser;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private MockedStatic<UserSessionServices> userSessionServicesMock;
    private MockedStatic<JdbcUtils> jdbcUtilsMock;

    @BeforeEach
    void setUp() throws SQLException {
        // Tạo mock user
        mockUser = new User(1, "testuser", "password", "Test User", "test@example.com");

        // Mock UserSessionServices.getCurrentUser()
        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

        // Mock các thành phần JDBC
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        // Mock JdbcUtils.getConn()
        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(connection);

        // Mock các phương thức kết nối
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(connection.getAutoCommit()).thenReturn(true);

        transactionServices = new TransactionServices();
    }

    @AfterEach
    void tearDown() {
        userSessionServicesMock.close();
        jdbcUtilsMock.close();
    }

    // Test phương thức getTransactionsByDateRange()
    @Test
    @DisplayName("Test getTransactionsByDateRange() trả về danh sách giao dịch")
    void testGetTransactionsByDateRange() throws SQLException {
        // Mock ResultSet
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(1000.0);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(startDate));
        when(resultSet.getString("note")).thenReturn("Test note");
        when(resultSet.getInt("user_id")).thenReturn(mockUser.getId());

        // Gọi phương thức cần test
        List<Transaction> transactions = transactionServices.getTransactionsByDateRange(startDate, endDate);

        // Xác nhận kết quả
        assertEquals(1, transactions.size());
        assertEquals(1000.0, transactions.get(0).getAmount());
        assertEquals(1, transactions.get(0).getCategoryId());
        assertEquals(startDate, transactions.get(0).getDate());
        assertEquals("Test note", transactions.get(0).getNote());
        assertEquals(mockUser.getId(), transactions.get(0).getUserId());

        // Xác nhận tương tác
        verify(preparedStatement).setDate(1, Date.valueOf(startDate));
        verify(preparedStatement).setDate(2, Date.valueOf(endDate));
        verify(preparedStatement).setInt(3, mockUser.getId());
    }

    // Test phương thức addTransaction() với dữ liệu hợp lệ
    @Test
    @DisplayName("Test addTransaction() với giao dịch hợp lệ")
    void testAddTransactionSuccess() throws SQLException {
        // Mock executeUpdate
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Tạo giao dịch hợp lệ
        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now(), "Test note", mockUser.getId());

        // Gọi phương thức cần test
        boolean result = transactionServices.addTransaction(transaction);

        // Xác nhận kết quả
        assertTrue(result);

        // Xác nhận tương tác
        verify(preparedStatement).setDouble(1, 1000.0);
        verify(preparedStatement).setString(2, "Test note");
        verify(preparedStatement).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatement).setInt(4, 1);
        verify(preparedStatement).setInt(5, mockUser.getId());
    }

    // Test phương thức deleteTransaction()
    @Test
    @DisplayName("Test deleteTransaction() với ID hợp lệ")
    void testDeleteTransactionSuccess() throws SQLException {
        // Mock executeUpdate
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Gọi phương thức cần test
        transactionServices.deleteTransaction(1);

        // Xác nhận tương tác
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeUpdate();
    }

    // Test phương thức deleteTransaction() với ID không tồn tại
    @Test
    @DisplayName("Test deleteTransaction() với ID không tồn tại")
    void testDeleteTransactionNotFound() throws SQLException {
        // Mock executeUpdate để trả về 0 (không có hàng nào bị ảnh hưởng)
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // Gọi phương thức cần test và assertThrows SQLException
        assertThrows(SQLException.class, () -> {
            transactionServices.deleteTransaction(999);
        });

        // Xác nhận tương tác
        verify(preparedStatement).setInt(1, 999);
        verify(preparedStatement).executeUpdate();
    }

    // Test phương thức updateTransaction() với dữ liệu hợp lệ
    @Test
    @DisplayName("Test updateTransaction() với dữ liệu hợp lệ")
    void testUpdateTransactionSuccess() throws SQLException {
        // Mock executeUpdate
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Gọi phương thức cần test
        transactionServices.updateTransaction(1, 2000.0, 2, LocalDate.now(), "Updated note", mockUser.getId());

        // Xác nhận tương tác
        verify(preparedStatement).setDouble(1, 2000.0);
        verify(preparedStatement).setInt(2, 2);
        verify(preparedStatement).setDate(3, Date.valueOf(LocalDate.now()));
        verify(preparedStatement).setString(4, "Updated note");
        verify(preparedStatement).setInt(5, mockUser.getId());
        verify(preparedStatement).setInt(6, 1);
    }

    // Test phương thức getAllTran()
    @Test
    @DisplayName("Test getAllTran() trả về danh sách giao dịch")
    void testGetAllTran() throws SQLException {
        // Mock ResultSet
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(1000.0);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("note")).thenReturn("Test note");
        when(resultSet.getInt("user_id")).thenReturn(mockUser.getId());

        // Gọi phương thức cần test
        List<Transaction> transactions = transactionServices.getAllTran();

        // Xác nhận kết quả
        assertEquals(1, transactions.size());
        assertEquals(1000.0, transactions.get(0).getAmount());
        assertEquals(1, transactions.get(0).getCategoryId());
        assertEquals(LocalDate.now(), transactions.get(0).getDate());
        assertEquals("Test note", transactions.get(0).getNote());
        assertEquals(mockUser.getId(), transactions.get(0).getUserId());

        // Xác nhận tương tác
        verify(preparedStatement).setInt(1, mockUser.getId());
    }

    // Các test về constraints validate của Transaction
    @Test
    @DisplayName("Test validate() - số tiền <= 0")
    void testValidateAmountLessThanOrEqualToZero() {
        Transaction transaction = new Transaction(0, 1, LocalDate.now(), "Test note", mockUser.getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate() - số tiền > 1 nghìn tỷ")
    void testValidateAmountGreaterThanOneTrillion() {
        Transaction transaction = new Transaction(1_000_000_000_001.0, 1, LocalDate.now(), "Test note", mockUser.getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate() - ngày là null")
    void testValidateDateIsNull() {
        Transaction transaction = new Transaction(1000.0, 1, null, "Test note", mockUser.getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
        assertEquals("Ngày giao dịch không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate() - ngày trong tương lai")
    void testValidateDateInFuture() {
        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now().plusDays(1), "Test note", mockUser.getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
        assertEquals("Ngày giao dịch không được vượt quá ngày hiện tại!", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate() - mã danh mục <= 0")
    void testValidateCategoryInvalid() {
        Transaction transaction = new Transaction(1000.0, 0, LocalDate.now(), "Test note", mockUser.getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, transaction::validate);
        assertEquals("Danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate() - giao dịch hợp lệ")
    void testValidTransaction() {
        // Tạo giao dịch hợp lệ
        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now(), "Test note", mockUser.getId());

        // Kiểm tra không ném ngoại lệ
        assertDoesNotThrow(transaction::validate);
    }

    @Test
    @DisplayName("Test validate() - giao dịch với số tiền nhỏ nhất hợp lệ")
    void testValidTransactionWithMinimumAmount() {
        // Tạo giao dịch với số tiền nhỏ nhất hợp lệ
        Transaction transaction = new Transaction(0.01, 1, LocalDate.now(), "Test note", mockUser.getId());

        // Kiểm tra không ném ngoại lệ
        assertDoesNotThrow(transaction::validate);
    }

    @Test
    @DisplayName("Test validate() - giao dịch với số tiền lớn nhất hợp lệ")
    void testValidTransactionWithMaximumAmount() {
        // Tạo giao dịch với số tiền lớn nhất hợp lệ
        Transaction transaction = new Transaction(1_000_000_000_000.0, 1, LocalDate.now(), "Test note", mockUser.getId());

        // Kiểm tra không ném ngoại lệ
        assertDoesNotThrow(transaction::validate);
    }

    @Test
    @DisplayName("Test validate() - giao dịch với ngày trong quá khứ")
    void testValidTransactionWithPastDate() {
        // Tạo giao dịch với ngày trong quá khứ
        Transaction transaction = new Transaction(1000.0, 1, LocalDate.now().minusDays(1), "Test note", mockUser.getId());

        // Kiểm tra không ném ngoại lệ
        assertDoesNotThrow(transaction::validate);
    }

    @Test
    @DisplayName("Test addTransaction() với giao dịch không hợp lệ")
    void testAddTransactionInvalid() {
        // Tạo giao dịch không hợp lệ (số tiền <= 0)
        Transaction invalidTransaction = new Transaction(0, 1, LocalDate.now(), "Test note", mockUser.getId());

        // Kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionServices.addTransaction(invalidTransaction);
        });
        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
    }
}
