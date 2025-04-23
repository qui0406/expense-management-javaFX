//
//=======
//////                                        /*
////// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
////// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
////// */
//////
////
///////**
////// *
////// * @author ttthu
////// */
//////
//////import com.qlct.pojo.Expense;
//////import com.qlct.services.StatsServices;
//////import com.qlct.services.UserSessionServices;
//////import org.junit.jupiter.api.*;
//////
//////import java.sql.SQLException;
//////import java.time.LocalDate;
//////import java.util.List;
//////
//////import static org.junit.jupiter.api.Assertions.*;
//////
//////@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//////public class StatServiceTest {
//////                                                                                                                                                 
//////    private static StatsServices statsServices;
//////
//////    @BeforeAll
//////    public static void setUp() throws SQLException {
//////        // Giả lập đăng nhập người dùng với user tồn tại
//////        UserSessionServices.login("qui123"); // đảm bảo username này có tồn tại trong DB
//////        statsServices = new StatsServices();
//////    }
//////
//////    @Test
//////    @Order(1)
//////    public void testGetExpenseCurrentMonthChi() throws SQLException {
//////        List<Expense> chiList = statsServices.getExpenseCurrentMonth("Chi");
//////        assertNotNull(chiList);
//////        System.out.println("Chi: " + chiList.size());
//////
//////        if (!chiList.isEmpty()) {
//////            for (Expense ex : chiList) {
//////                assertTrue(ex.getAmount() >= 0);
//////                assertNotNull(ex.getCategoryName());
//////                System.out.printf("Danh mục: %s - Số tiền: %.2f\n", ex.getCategoryName(), ex.getAmount());
//////            }
//////        }
//////    }
//////
//////    @Test
//////    @Order(2)
//////    public void testGetExpenseCurrentMonthThu() throws SQLException {
//////        List<Expense> thuList = statsServices.getExpenseCurrentMonth("Thu");
//////        assertNotNull(thuList);
//////        System.out.println("Thu: " + thuList.size());
//////
//////        if (!thuList.isEmpty()) {
//////            for (Expense ex : thuList) {
//////                assertTrue(ex.getAmount() >= 0);
//////                assertNotNull(ex.getCategoryName());
//////                System.out.printf("Danh mục: %s - Số tiền: %.2f\n", ex.getCategoryName(), ex.getAmount());
//////            }
//////        }
//////    }
//////
//////    @Test
//////    @Order(3)
//////    public void testFilterByDateRange() throws SQLException {
//////        LocalDate fromDate = LocalDate.of(2024, 3, 1);
//////        LocalDate toDate = LocalDate.of(2024, 3, 31);
//////        List<Expense> filtered = statsServices.getExpensesFilterByDate(fromDate, toDate, "Chi");
//////
//////        assertNotNull(filtered);
//////        for (Expense e : filtered) {
//////            assertTrue(e.getAmount() >= 0);
//////            assertNotNull(e.getCategoryName());
//////        }
//////    }
//////
//////    @Test
//////    @Order(4)
//////    public void testMonthlyExpenseChi() throws SQLException {
//////        List<Expense> monthlyChi = statsServices.getMonthlyExpenses("Chi");
//////
//////        assertNotNull(monthlyChi);
//////        for (Expense e : monthlyChi) {
//////            assertTrue(e.getCategoryName().startsWith("Tháng"));
//////            assertTrue(e.getAmount() >= 0);
//////        }
//////    }
//////
//////    @Test
//////    @Order(5)
//////    public void testInvalidType() throws SQLException {
//////        List<Expense> result = statsServices.getExpenseCurrentMonth("Không tồn tại");
//////        assertNotNull(result);
//////        assertEquals(0, result.size()); // vì không có loại này
//////    }
//////}
////import com.qlct.pojo.Expense;
////import com.qlct.services.StatsServices;
////import com.qlct.services.UserSessionServices;
////import org.junit.jupiter.api.*;
////import org.mockito.Mockito;
////
////import java.sql.SQLException;
////import java.time.LocalDate;
////import java.util.List;
////
////import static org.junit.jupiter.api.Assertions.*;
////
////@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
////public class StatServiceTest {
////
////    private StatsServices statsServices;
////
////    @BeforeEach
////    public void setUp() throws SQLException {
////        // Giả lập đăng nhập người dùng có tồn tại
////        UserSessionServices.login("qui123"); // Đảm bảo tài khoản này tồn tại trong DB test
////        statsServices = new StatsServices();
////    }
////
////    // ✅ Test truy xuất thống kê "Chi" của tháng hiện tại
////    @Test
////    @Order(1)
////    public void testGetExpenseCurrentMonthWithChi() throws SQLException {
////        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
////        assertNotNull(expenses);
////        assertFalse(expenses.isEmpty(), "Không có dữ liệu thống kê cho tháng hiện tại.");
////        for (Expense e : expenses) {
////            assertEquals("Chi", e.getType(), "Sai loại giao dịch.");
////        }
////    }
////
////    // ✅ Test truy xuất thống kê "Thu" của tháng hiện tại
////    @Test
////    @Order(2)
////    public void testGetExpenseCurrentMonthWithThu() throws SQLException {
////        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");
////
////        assertNotNull(expenses);
////        for (Expense e : expenses) {
////            assertEquals("Thu", e.getType(), "Sai loại giao dịch.");
////        }
////    }
////
////    // ✅ Test lọc theo khoảng thời gian hợp lệ
////    @Test
////    @Order(3)
////    public void testGetExpensesFilterByDate_ValidRange() throws SQLException {
////        LocalDate from = LocalDate.of(2024, 1, 1);
////        LocalDate to = LocalDate.of(2024, 1, 31);
////
////        List<Expense> expenses = statsServices.getExpensesFilterByDate(from, to, "Thu");
////
////        assertNotNull(expenses);
////        for (Expense e : expenses) {
////            assertEquals("Thu", e.getType(), "Loại giao dịch không đúng");
////        }
////    }
////
////    // ✅ Test thống kê tổng theo tháng trong năm hiện tại
////    @Test
////    @Order(4)
////    public void testGetMonthlyExpenses() throws SQLException {
////        List<Expense> result = statsServices.getMonthlyExpenses("Chi");
////
////        assertNotNull(result);
////        assertTrue(result.size() <= 12, "Số tháng thống kê không hợp lệ");
////        for (Expense e : result) {
////            assertTrue(e.getCategoryName().startsWith("Tháng "), "Tên không đúng định dạng tháng");
////        }
////    }
////
////    // ✅ Test khi truyền vào loại không tồn tại
////    @Test
////    @Order(5)
////    public void testInvalidType() throws SQLException {
////        List<Expense> result = statsServices.getExpenseCurrentMonth("Không tồn tại");
////
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Phải trả về danh sách rỗng");
////    }
////
////    // ✅ [BỔ SUNG] Test truyền fromDate và toDate là null
////    @Test
////    @Order(6)
////    public void testFilterDateWithNullInputs() {
////        assertThrows(NullPointerException.class, () -> {
////            statsServices.getExpensesFilterByDate(null, null, "Chi");
////        }, "Phải ném ra NullPointerException khi ngày null");
////    }
////
////    // ✅ [BỔ SUNG] Test khi fromDate > toDate
////    @Test
////    @Order(7)
////    public void testFilterDateFromGreaterThanTo() throws SQLException {
////        LocalDate from = LocalDate.of(2024, 4, 1);
////        LocalDate to = LocalDate.of(2024, 1, 1);
////
////        List<Expense> result = statsServices.getExpensesFilterByDate(from, to, "Chi");
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Danh sách phải rỗng nếu fromDate > toDate");
////    }
////
////    // ✅ [BỔ SUNG] Test truyền type = null
////    @Test
////    @Order(8)
////    public void testDefaultTypeIsChi() throws SQLException {
////        // Giao diện mặc định gọi hàm này với type = "Chi"
////        List<Expense> result = statsServices.getExpenseCurrentMonth("Chi");
////
////        assertNotNull(result, "Kết quả không được null nếu type là 'Chi'");
////        // Có thể không assert isEmpty vì tùy dữ liệu
////    }
////
////    // ✅ [BỔ SUNG] Test không có dữ liệu cho loại đã nhập
////    @Test
////    @Order(9)
////    public void testNoDataForType() throws SQLException {
////        List<Expense> result = statsServices.getExpenseCurrentMonth("LoạiKhôngTồnTại");
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Danh sách phải rỗng khi không có loại giao dịch này");
////    }
////
////    // ✅ [BỔ SUNG] Test xử lý ngoại lệ SQLException bằng Mockito (mô phỏng lỗi DB)
////    @Test
////    @Order(10)
////    public void testSQLExceptionHandling() throws SQLException {
////        StatsServices mockService = Mockito.spy(new StatsServices());
////
////        Mockito.doThrow(new SQLException("Lỗi DB")).when(mockService).getExpenseCurrentMonth("Chi");
////
////        SQLException ex = assertThrows(SQLException.class, () -> {
////            mockService.getExpenseCurrentMonth("Chi");
////        });
////
////        assertEquals("Lỗi DB", ex.getMessage());
////    }
////}
//
//=======
//
////
////import com.qlct.pojo.Expense;
////import com.qlct.services.StatsServices;
////import com.qlct.services.UserSessionServices;
////import org.junit.jupiter.api.*;
////import org.mockito.Mockito;
////
////import java.sql.SQLException;
////import java.time.LocalDate;
////import java.util.List;
////
////import static org.junit.jupiter.api.Assertions.*;
////
////@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
////public class StatServiceTest {
////
////    private StatsServices statsServices;
////
////    @BeforeEach
////    public void setUp() throws SQLException {
////        // Giả lập đăng nhập người dùng có tồn tại
////        UserSessionServices.login("qui123"); // Đảm bảo tài khoản này tồn tại trong DB test
////        statsServices = new StatsServices();
////    }
////
////    // ✅ Test truy xuất thống kê "Chi" của tháng hiện tại
////    @Test
////    @Order(1)
////    public void testGetExpenseCurrentMonthWithChi() throws SQLException {
////        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");
////
////        assertNotNull(expenses);
////        assertFalse(expenses.isEmpty(), "Không có dữ liệu thống kê cho tháng hiện tại.");
////        for (Expense e : expenses) {
////            assertEquals("Chi", e.getType(), "Sai loại giao dịch.");
////        }
////    }
////
////    // ✅ Test truy xuất thống kê "Thu" của tháng hiện tại
////    @Test
////    @Order(2)
////    public void testGetExpenseCurrentMonthWithThu() throws SQLException {
////        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");
////
////        assertNotNull(expenses);
////        for (Expense e : expenses) {
////            assertEquals("Thu", e.getType(), "Sai loại giao dịch.");
////        }
////    }
////
////    // ✅ Test lọc theo khoảng thời gian hợp lệ
////    @Test
////    @Order(3)
////    public void testGetExpensesFilterByDate_ValidRange() throws SQLException {
////        LocalDate from = LocalDate.of(2024, 1, 1);
////        LocalDate to = LocalDate.of(2024, 1, 31);
////
////        List<Expense> expenses = statsServices.getExpensesFilterByDate(from, to, "Thu");
////
////        assertNotNull(expenses);
////        for (Expense e : expenses) {
////            assertEquals("Thu", e.getType(), "Loại giao dịch không đúng");
////        }
////    }
////
////    // ✅ Test thống kê tổng theo tháng trong năm hiện tại
////    @Test
////    @Order(4)
////    public void testGetMonthlyExpenses() throws SQLException {
////        List<Expense> result = statsServices.getMonthlyExpenses("Chi");
////
////        assertNotNull(result);
////        assertTrue(result.size() <= 12, "Số tháng thống kê không hợp lệ");
////        for (Expense e : result) {
////            assertTrue(e.getCategoryName().startsWith("Tháng "), "Tên không đúng định dạng tháng");
////        }
////    }
////
////    // ✅ Test khi truyền vào loại không tồn tại
////    @Test
////    @Order(5)
////    public void testInvalidType() throws SQLException {
////        List<Expense> result = statsServices.getExpenseCurrentMonth("Không tồn tại");
////
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Phải trả về danh sách rỗng");
////    }
////
////    // ✅ [BỔ SUNG] Test truyền fromDate và toDate là null
////    @Test
////    @Order(6)
////    public void testFilterDateWithNullInputs() {
////        assertThrows(NullPointerException.class, () -> {
////            statsServices.getExpensesFilterByDate(null, null, "Chi");
////        }, "Phải ném ra NullPointerException khi ngày null");
////    }
////
////    // ✅ [BỔ SUNG] Test khi fromDate > toDate
////    @Test
////    @Order(7)
////    public void testFilterDateFromGreaterThanTo() throws SQLException {
////        LocalDate from = LocalDate.of(2024, 4, 1);
////        LocalDate to = LocalDate.of(2024, 1, 1);
////
////        List<Expense> result = statsServices.getExpensesFilterByDate(from, to, "Chi");
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Danh sách phải rỗng nếu fromDate > toDate");
////    }
////
////    // ✅ [BỔ SUNG] Test truyền type = null
////    @Test
////    @Order(8)
////    public void testDefaultTypeIsChi() throws SQLException {
////        // Giao diện mặc định gọi hàm này với type = "Chi"
////        List<Expense> result = statsServices.getExpenseCurrentMonth("Chi");
////
////        assertNotNull(result, "Kết quả không được null nếu type là 'Chi'");
////        // Có thể không assert isEmpty vì tùy dữ liệu
////    }
////
////    // ✅ [BỔ SUNG] Test không có dữ liệu cho loại đã nhập
////    @Test
////    @Order(9)
////    public void testNoDataForType() throws SQLException {
////        List<Expense> result = statsServices.getExpenseCurrentMonth("LoạiKhôngTồnTại");
////        assertNotNull(result);
////        assertEquals(0, result.size(), "Danh sách phải rỗng khi không có loại giao dịch này");
////    }
////
////    // ✅ [BỔ SUNG] Test xử lý ngoại lệ SQLException bằng Mockito (mô phỏng lỗi DB)
////    @Test
////    @Order(10)
////    public void testSQLExceptionHandling() throws SQLException {
////        StatsServices mockService = Mockito.spy(new StatsServices());
////
////        Mockito.doThrow(new SQLException("Lỗi DB")).when(mockService).getExpenseCurrentMonth("Chi");
////
////        SQLException ex = assertThrows(SQLException.class, () -> {
////            mockService.getExpenseCurrentMonth("Chi");
////        });
////
////        assertEquals("Lỗi DB", ex.getMessage());
////    }
////    }
//
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
//    }
///*mô phỏng truy vấn không có kết quả.*/
//    @Test
//    void testGetExpenseCurrentMonth_EmptyResult() throws SQLException {
//        when(resultSet.next()).thenReturn(false);
//
//        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");
//
//        assertTrue(expenses.isEmpty());
//    }
///*kiểm tra xem hệ thống có xử lý đúng không khi không truyền loại thu/chi.*/
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
//    }
///*kiểm tra xem hệ thống có xử lý đúng không khi không truyền loại thu/chi.*/
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
///*có lọc đúng dữ liệu dựa trên ngày và loại thu/chi không.*/
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
//    }
///*Xác minh hệ thống chuyển đổi dữ liệu tháng thành định dạng "Tháng X" và trả về đúng giá trị.*/
//    @Test
//    void testGetMonthlyExpenses_Success() throws SQLException {
//        when(resultSet.next()).thenReturn(true, true, false);
//        when(resultSet.getInt("month")).thenReturn(1, 2);
//        when(resultSet.getDouble("total_amount")).thenReturn(500.0, 700.0);
//
//        List<Expense> expenses = statsServices.getMonthlyExpenses("Chi");
//
//        assertEquals(2, expenses.size());
//        assertEquals(1, expenses.get(0).getCategoryId());
//        assertEquals("Tháng 1", expenses.get(0).getCategoryName());
//        assertEquals(500.0, expenses.get(0).getAmount(), 0.01);
//        assertEquals(2, expenses.get(1).getCategoryId());
//        assertEquals("Tháng 2", expenses.get(1).getCategoryName());
//        assertEquals(700.0, expenses.get(1).getAmount(), 0.01);
//    }
//
//// Kiểm tra trường hợp null connection (lỗi truy cập tài nguyên)
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
//        // Kiểm tra tham số đầu vào
//        verify(preparedStatement).setInt(eq(1), eq(1)); // userId = 1
//        verify(preparedStatement).setString(eq(2), eq("Thu")); // type = "Thu"
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
//        // Kiểm tra tham số ngày tháng được truyền đúng
//        verify(preparedStatement).setInt(eq(1), eq(1)); // userId = 1
//        verify(preparedStatement).setString(eq(2), eq("Chi")); // type = "Chi"
//        verify(preparedStatement).setDate(eq(3), eq(Date.valueOf(fromDate)));
//        verify(preparedStatement).setDate(eq(4), eq(Date.valueOf(toDate)));
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
//        // Kiểm tra tham số truyền vào có ngay đối với trường hợp ngày không hợp lệ
//        verify(preparedStatement).setDate(eq(3), eq(Date.valueOf(fromDate)));
//        verify(preparedStatement).setDate(eq(4), eq(Date.valueOf(toDate)));
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
//        verify(preparedStatement).setDate(eq(3), eq(Date.valueOf(today)));
//        verify(preparedStatement).setDate(eq(4), eq(Date.valueOf(futureDate)));
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
//            statsServices.getMonthlyExpenses("Chi");
//        });
//        // Không có catch SQLException trong phương thức, nên ngoại lệ được truyền ra ngoài
//    }
//
//    // Kiểm tra các thảo tác không đồng bộ (lỗi xử lý đồng thời)
//    @Test
//    void testMultipleSimultaneousCalls() throws SQLException, InterruptedException {
//        when(resultSet.next()).thenReturn(false);
//
//        // Tạo và thực thi 5 luồng đồng thời
//        Thread[] threads = new Thread[5];
//        for (int i = 0; i < 5; i++) {
//            final int index = i;
//            threads[i] = new Thread(() -> {
//                try {
//                    statsServices.getExpenseCurrentMonth("Chi");
//                } catch (SQLException e) {
//                    fail("Thread " + index + " failed: " + e.getMessage());
//                }
//            });
//            threads[i].start();
//        }
//
//        // Đợi tất cả luồng hoàn thành
//        for (Thread thread : threads) {
//            thread.join();
//        }
//
//        // Kiểm tra số lần gọi executeQuery() phải là 5
//        verify(preparedStatement, times(5)).executeQuery();
//    }
//
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
//        List<Expense> expenses = statsServices.getMonthlyExpenses("Chi");
//
//        assertEquals(1, expenses.size());
//        assertEquals(10, expenses.get(0).getCategoryId()); // categoryId lưu tháng
//        assertEquals("Tháng 10", expenses.get(0).getCategoryName()); // Kiểm tra chuyển đổi tháng đúng
//        assertEquals(1500.25, expenses.get(0).getAmount(), 0.001);
//    }
//}
//}
//
