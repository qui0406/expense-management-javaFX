//
//import com.qlct.pojo.Expense;
//import com.qlct.pojo.JdbcUtils;
//import com.qlct.pojo.User;
//import com.qlct.services.StatsServices;
//import com.qlct.services.UserSessionServices;
//import org.junit.jupiter.api.*;
//
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//public class StatServiceTest {
//
//    private StatsServices statsServices;
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
//        // Tạo user giả lập
//        mockUser = new User(1, "Quí", "Trần", "qui123", "qui123@gmail.com");
//        mockUser.setId(1);
//
//        // Mock static UserSessionServices.getCurrentUser()
//        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
//        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//        // Mock JDBC
//        connection = mock(Connection.class);
//        preparedStatement = mock(PreparedStatement.class);
//        resultSet = mock(ResultSet.class);
//
//        // Mock static JdbcUtils.getConn()
//        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
//        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(connection);
//
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//
//        statsServices = new StatsServices();
//    }
//
//    @AfterEach
//    void tearDown() {
//        userSessionServicesMock.close();
//        jdbcUtilsMock.close();
//    }
//
//    /*trả về danh sách đúng số lượng, đúng dữ liệu.*/
//    @Test
//    void testGetExpenseCurrentMonth_Success() throws SQLException {
//        when(resultSet.next()).thenReturn(true, true, false);
//        when(resultSet.getInt("id")).thenReturn(1, 2);
//        when(resultSet.getString("name")).thenReturn("Food", "Transport");
//        when(resultSet.getDouble("amount")).thenReturn(500.0, 300.0);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
//
//        assertEquals(2, expenses.size());
//        assertEquals(1, expenses.get(0).getCategoryId());
//        assertEquals("Food", expenses.get(0).getCategoryName());
//        assertEquals(500.0, expenses.get(0).getAmount(), 0.01);
//        assertEquals(2, expenses.get(1).getCategoryId());
//        assertEquals("Transport", expenses.get(1).getCategoryName());
//        assertEquals(300.0, expenses.get(1).getAmount(), 0.01);
//
//        // Verify the parameters are set correctly with updated positions
//        verify(preparedStatement).setInt(1, 1); // userId
//        verify(preparedStatement).setString(2, "Chi"); // type
//    }
//
//    /*mô phỏng truy vấn không có kết quả.*/
//    @Test
//    void testGetExpenseCurrentMonth_EmptyResult() throws SQLException {
//        when(resultSet.next()).thenReturn(false);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");
//
//        assertTrue(expenses.isEmpty());
//
//        // Verify the parameters are set correctly
//        verify(preparedStatement).setInt(1, 1); // userId
//        verify(preparedStatement).setString(2, "Thu"); // type
//    }
//
//    /*kiểm tra xem hệ thống có xử lý đúng không khi không truyền loại thu/chi.*/
//    @Test
//    void testGetExpenseCurrentMonth_NullType() throws SQLException {
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Food");
//        when(resultSet.getDouble("amount")).thenReturn(500.0);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth(null);
//
//        assertEquals(1, expenses.size());
//        assertEquals(1, expenses.get(0).getCategoryId());
//        assertEquals("Food", expenses.get(0).getCategoryName());
//        assertEquals(500.0, expenses.get(0).getAmount(), 0.01);
//
//        // Verify null parameter is handled
//        verify(preparedStatement).setInt(1, 1); // userId
//        verify(preparedStatement).setString(2, null); // type
//    }
//
//    /*kiểm tra xem hệ thống có xử lý đúng không khi có lỗi SQL.*/
//    @Test
//    void testGetExpenseCurrentMonth_SQLException() throws SQLException {
//        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));
//
//        SQLException exception = assertThrows(SQLException.class, () -> {
//            statsServices.getExpenseCurrentMonth("Chi");
//        });
//
//        assertEquals("Database error", exception.getMessage());
//    }
//
//    /*có lọc đúng dữ liệu dựa trên ngày và loại thu/chi không.*/
//    @Test
//    void testGetExpensesFilterByDate_Success() throws SQLException {
//        LocalDate fromDate = LocalDate.of(2023, 1, 1);
//        LocalDate toDate = LocalDate.of(2023, 12, 31);
//
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Food");
//        when(resultSet.getDouble("amount")).thenReturn(1000.0);
//
//        List<Expense> expenses = statsServices.getExpensesFilterByDate(fromDate, toDate, "Chi");
//
//        assertEquals(1, expenses.size());
//        assertEquals(1, expenses.get(0).getCategoryId());
//        assertEquals("Food", expenses.get(0).getCategoryName());
//        assertEquals(1000.0, expenses.get(0).getAmount(), 0.01);
//
//        // Verify the parameters are set correctly with updated positions
//        verify(preparedStatement).setDate(1, Date.valueOf(fromDate));
//        verify(preparedStatement).setDate(2, Date.valueOf(toDate));
//        verify(preparedStatement).setInt(3, 1); // userId
//        verify(preparedStatement).setString(4, "Chi"); // type
//    }
//
//    /*Xác minh hệ thống chuyển đổi dữ liệu tháng thành định dạng "Tháng X" và trả về đúng giá trị.*/
//    @Test
//    void testGetMonthlyExpenses_Success() throws SQLException {
//        when(resultSet.next()).thenReturn(true, true, false);
//        when(resultSet.getInt("month")).thenReturn(1, 2);
//        when(resultSet.getDouble("total_amount")).thenReturn(500.0, 700.0);
//
//        List<Expense> expenses = statsServices.getMonthlyExpenses("Chi", 2023);
//
//        assertEquals(2, expenses.size());
//        assertEquals(1, expenses.get(0).getCategoryId());
//        assertEquals("Tháng 1", expenses.get(0).getCategoryName());
//        assertEquals(500.0, expenses.get(0).getAmount(), 0.01);
//        assertEquals(2, expenses.get(1).getCategoryId());
//        assertEquals("Tháng 2", expenses.get(1).getCategoryName());
//        assertEquals(700.0, expenses.get(1).getAmount(), 0.01);
//
//        // Xác minh các tham số
//        verify(preparedStatement).setInt(1, 2023);    // year
//        verify(preparedStatement).setInt(2, 1);       // userId
//        verify(preparedStatement).setString(3, "Chi"); // type
//    }
//
//    // Kiểm tra trường hợp null connection (lỗi truy cập tài nguyên)
//    @Test
//    void testGetExpenseCurrentMonth_NullConnection() {
//        // Mock trả về null thay vì connection thật
//        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(null);
//
//        // Kiểm tra ngoại lệ khi connection là null
//        assertThrows(NullPointerException.class, () -> {
//            statsServices.getExpenseCurrentMonth("Chi");
//        });
//    }
//
//    @Test
//    void testGetMonthlyExpenses_InvalidMonth() throws SQLException {
//        // Mock ResultSet trả về tháng không hợp lệ
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("month")).thenReturn(13); // Tháng không hợp lệ
//        when(resultSet.getDouble("total_amount")).thenReturn(1000.0);
//
//        List<Expense> expenses = statsServices.getMonthlyExpenses("Chi", 2023);
//
//        assertEquals(1, expenses.size());
//        assertEquals("Tháng 13", expenses.get(0).getCategoryName()); // Lỗi: tháng không hợp lệ
//    }
//
//    // Kiểm tra trường hợp type = "Thu" (lỗi bao phủ không đủ)
//    @Test
//    void testGetExpenseCurrentMonth_ThuType() throws SQLException {
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Lương");
//        when(resultSet.getDouble("amount")).thenReturn(10000.0);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");
//
//        assertEquals(1, expenses.size());
//        assertEquals("Lương", expenses.get(0).getCategoryName());
//        assertEquals(10000.0, expenses.get(0).getAmount(), 0.01);
//
//        // Kiểm tra tham số đầu vào với thứ tự mới
//        verify(preparedStatement).setInt(1, 1); // userId = 1
//        verify(preparedStatement).setString(2, "Thu"); // type = "Thu"
//    }
//
//    // Kiểm tra tính toán chính xác với số thực (lỗi tính toán)
//    @Test
//    void testGetExpenseCurrentMonth_Calculation() throws SQLException {
//        when(resultSet.next()).thenReturn(true, true, false);
//        when(resultSet.getInt("id")).thenReturn(1, 2);
//        when(resultSet.getString("name")).thenReturn("Food", "Transport");
//        when(resultSet.getDouble("amount")).thenReturn(0.1, 0.2);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
//
//        // Kiểm tra tính chính xác của phép tính với số thực
//        assertEquals(2, expenses.size());
//        assertEquals(0.1, expenses.get(0).getAmount(), 0.00001);
//        assertEquals(0.2, expenses.get(1).getAmount(), 0.00001);
//
//        // Kiểm tra tổng số tiền (0.1 + 0.2 có thể không chính xác là 0.3 với double)
//        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
//        assertEquals(0.3, total, 0.00001);
//    }
//
//    // Kiểm tra giá trị ngày tháng hợp lệ (lỗi ràng buộc dữ liệu)
//    @Test
//    void testGetExpensesFilterByDate_ValidDates() throws SQLException {
//        LocalDate fromDate = LocalDate.of(2023, 1, 1);
//        LocalDate toDate = LocalDate.of(2023, 12, 31);
//
//        when(resultSet.next()).thenReturn(false);
//
//        statsServices.getExpensesFilterByDate(fromDate, toDate, "Chi");
//
//        // Kiểm tra tham số ngày tháng được truyền đúng với thứ tự mới
//        verify(preparedStatement).setDate(1, Date.valueOf(fromDate));
//        verify(preparedStatement).setDate(2, Date.valueOf(toDate));
//        verify(preparedStatement).setInt(3, 1); // userId = 1
//        verify(preparedStatement).setString(4, "Chi"); // type = "Chi"
//    }
//
//    // Kiểm tra khi ngày bắt đầu lớn hơn ngày kết thúc (lỗi ràng buộc dữ liệu)
//    @Test
//    void testGetExpensesFilterByDate_InvalidDateRange() throws SQLException {
//        LocalDate fromDate = LocalDate.of(2023, 12, 31);
//        LocalDate toDate = LocalDate.of(2023, 1, 1); // Ngày kết thúc < ngày bắt đầu
//
//        when(resultSet.next()).thenReturn(false);
//
//        // Phương thức hiện tại không kiểm tra trường hợp này nên vẫn thực hiện bình thường
//        // Đây là một lỗi logic cần được sửa
//        List<Expense> expenses = statsServices.getExpensesFilterByDate(fromDate, toDate, "Chi");
//        assertTrue(expenses.isEmpty());
//
//        // Kiểm tra tham số truyền vào có đúng thứ tự với trường hợp ngày không hợp lệ
//        verify(preparedStatement).setDate(1, Date.valueOf(fromDate));
//        verify(preparedStatement).setDate(2, Date.valueOf(toDate));
//        verify(preparedStatement).setInt(3, 1);
//        verify(preparedStatement).setString(4, "Chi");
//    }
//
//    // Kiểm tra trường hợp ngày tương lai (lỗi ràng buộc dữ liệu)
//    @Test
//    void testGetExpensesFilterByDate_FutureDate() throws SQLException {
//        LocalDate today = LocalDate.now();
//        LocalDate futureDate = today.plusMonths(1);
//
//        when(resultSet.next()).thenReturn(false);
//
//        // Phương thức hiện tại không kiểm tra ngày tương lai
//        // Đây là một lỗi logic cần được sửa
//        statsServices.getExpensesFilterByDate(today, futureDate, "Chi");
//
//        verify(preparedStatement).setDate(1, Date.valueOf(today));
//        verify(preparedStatement).setDate(2, Date.valueOf(futureDate));
//        verify(preparedStatement).setInt(3, 1);
//        verify(preparedStatement).setString(4, "Chi");
//    }
//
//    // Kiểm tra xử lý giá trị âm (lỗi ràng buộc dữ liệu)
//    @Test
//    void testGetExpenseCurrentMonth_NegativeAmount() throws SQLException {
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Food");
//        when(resultSet.getDouble("amount")).thenReturn(-500.0); // Giá trị âm
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
//
//        assertEquals(1, expenses.size());
//        assertEquals(-500.0, expenses.get(0).getAmount(), 0.01); // Chấp nhận giá trị âm
//        // Điều này có thể là một lỗi logic cần được xử lý
//    }
//
//    /*Phát hiện lỗi khi amount là NaN, có thể gây ra vấn đề khi hiển thị hoặc tính toán*/
//    @Test
//    void testGetExpenseCurrentMonth_NanAmount() throws SQLException {
//        // Mock ResultSet trả về amount là NaN
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Food");
//        when(resultSet.getDouble("amount")).thenReturn(Double.NaN);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
//
//        assertEquals(1, expenses.size());
//        assertTrue(Double.isNaN(expenses.get(0).getAmount())); // Lỗi: chấp nhận NaN
//    }
//
//    // Kiểm tra đóng tài nguyên (lỗi truy cập tài nguyên)
//    @Test
//    void testGetExpenseCurrentMonth_ResourceClosure() throws SQLException {
//        when(resultSet.next()).thenReturn(false);
//
//        statsServices.getExpenseCurrentMonth("Chi");
//
//        // Kiểm tra resultSet và preparedStatement có được đóng
//        // Lưu ý: trong try-with-resources, connection sẽ tự đóng
//        verify(resultSet, never()).close(); // StatsServices không tự đóng resultSet
//        verify(preparedStatement, never()).close(); // StatsServices không tự đóng preparedStatement
//        // Đây có thể là một lỗi cần xử lý nếu không sử dụng try-with-resources cho resultSet và preparedStatement
//    }
//
//    // Kiểm tra xử lý ngoại lệ (lỗi ngoại lệ không bắt)
//    @Test
//    void testGetMonthlyExpenses_SQLExceptionHandling() throws SQLException {
//        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Connection lost"));
//
//        assertThrows(SQLException.class, () -> {
//            statsServices.getMonthlyExpenses("Chi", 2023);
//        });
//        // Không có catch SQLException trong phương thức, nên ngoại lệ được truyền ra ngoài
//    }
//
//    // Kiểm tra các thảo tác không đồng bộ (lỗi xử lý đồng thời)
////    @Test
////    void testMultipleSimultaneousCalls() throws SQLException, InterruptedException {
////        when(resultSet.next()).thenReturn(false);
////
////        // Tạo và thực thi 5 luồng đồng thời
////        Thread[] threads = new Thread[5];
////        for (int i = 0; i < 5; i++) {
////            final int index = i;
////            threads[i] = new Thread(() -> {
////                try {
////                    statsServices.getExpenseCurrentMonth("Chi");
////                } catch (SQLException e) {
////                    fail("Thread " + index + " failed: " + e.getMessage());
////                }
////            });
////            threads[i].start();
////        }
////
////        // Đợi tất cả luồng hoàn thành
////        for (Thread thread : threads) {
////            thread.join();
////        }
////
////        // Kiểm tra số lần gọi executeQuery() phải là 5
////        verify(preparedStatement, times(5)).executeQuery();
////    }
//    // Kiểm tra vòng lặp với kết quả trả về là null (lỗi vòng lặp)
//    @Test
//    void testGetExpenseCurrentMonth_NullResultSet() throws SQLException {
//        when(preparedStatement.executeQuery()).thenReturn(null);
//
//        // Trường hợp resultSet là null sẽ gây ra NullPointerException khi gọi next()
//        assertThrows(NullPointerException.class, () -> {
//            statsServices.getExpenseCurrentMonth("Chi");
//        });
//    }
//
//    // Kiểm tra chuyển đổi dữ liệu cho phương thức getMonthlyExpenses (lỗi tính toán)
//    @Test
//    void testGetMonthlyExpenses_DataConversion() throws SQLException {
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("month")).thenReturn(10); // Tháng 10
//        when(resultSet.getDouble("total_amount")).thenReturn(1500.25);
//
//        List<Expense> expenses = statsServices.getMonthlyExpenses("Chi", 2023);
//
//        assertEquals(1, expenses.size());
//        assertEquals(10, expenses.get(0).getCategoryId()); // categoryId lưu tháng
//        assertEquals("Tháng 10", expenses.get(0).getCategoryName()); // Kiểm tra chuyển đổi tháng đúng
//        assertEquals(1500.25, expenses.get(0).getAmount(), 0.001);
//    }
//}
