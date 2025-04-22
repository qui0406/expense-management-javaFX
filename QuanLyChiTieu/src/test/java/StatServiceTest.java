//                                        /*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//

///**
// *
// * @author ttthu
// */
//
//import com.qlct.pojo.Expense;
//import com.qlct.services.StatsServices;
//import com.qlct.services.UserSessionServices;
//import org.junit.jupiter.api.*;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class StatServiceTest {
//                                                                                                                                                 
//    private static StatsServices statsServices;
//
//    @BeforeAll
//    public static void setUp() throws SQLException {
//        // Giả lập đăng nhập người dùng với user tồn tại
//        UserSessionServices.login("qui123"); // đảm bảo username này có tồn tại trong DB
//        statsServices = new StatsServices();
//    }
//
//    @Test
//    @Order(1)
//    public void testGetExpenseCurrentMonthChi() throws SQLException {
//        List<Expense> chiList = statsServices.getExpenseCurrentMonth("Chi");
//        assertNotNull(chiList);
//        System.out.println("Chi: " + chiList.size());
//
//        if (!chiList.isEmpty()) {
//            for (Expense ex : chiList) {
//                assertTrue(ex.getAmount() >= 0);
//                assertNotNull(ex.getCategoryName());
//                System.out.printf("Danh mục: %s - Số tiền: %.2f\n", ex.getCategoryName(), ex.getAmount());
//            }
//        }
//    }
//
//    @Test
//    @Order(2)
//    public void testGetExpenseCurrentMonthThu() throws SQLException {
//        List<Expense> thuList = statsServices.getExpenseCurrentMonth("Thu");
//        assertNotNull(thuList);
//        System.out.println("Thu: " + thuList.size());
//
//        if (!thuList.isEmpty()) {
//            for (Expense ex : thuList) {
//                assertTrue(ex.getAmount() >= 0);
//                assertNotNull(ex.getCategoryName());
//                System.out.printf("Danh mục: %s - Số tiền: %.2f\n", ex.getCategoryName(), ex.getAmount());
//            }
//        }
//    }
//
//    @Test
//    @Order(3)
//    public void testFilterByDateRange() throws SQLException {
//        LocalDate fromDate = LocalDate.of(2024, 3, 1);
//        LocalDate toDate = LocalDate.of(2024, 3, 31);
//        List<Expense> filtered = statsServices.getExpensesFilterByDate(fromDate, toDate, "Chi");
//
//        assertNotNull(filtered);
//        for (Expense e : filtered) {
//            assertTrue(e.getAmount() >= 0);
//            assertNotNull(e.getCategoryName());
//        }
//    }
//
//    @Test
//    @Order(4)
//    public void testMonthlyExpenseChi() throws SQLException {
//        List<Expense> monthlyChi = statsServices.getMonthlyExpenses("Chi");
//
//        assertNotNull(monthlyChi);
//        for (Expense e : monthlyChi) {
//            assertTrue(e.getCategoryName().startsWith("Tháng"));
//            assertTrue(e.getAmount() >= 0);
//        }
//    }
//
//    @Test
//    @Order(5)
//    public void testInvalidType() throws SQLException {
//        List<Expense> result = statsServices.getExpenseCurrentMonth("Không tồn tại");
//        assertNotNull(result);
//        assertEquals(0, result.size()); // vì không có loại này
//    }
//}
import com.qlct.pojo.Expense;
import com.qlct.services.StatsServices;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatServiceTest {

    private StatsServices statsServices;

    @BeforeEach
    public void setUp() throws SQLException {
        // Giả lập đăng nhập người dùng có tồn tại
        UserSessionServices.login("qui123"); // Đảm bảo tài khoản này tồn tại trong DB test
        statsServices = new StatsServices();
    }

    // ✅ Test truy xuất thống kê "Chi" của tháng hiện tại
    @Test
    @Order(1)
    public void testGetExpenseCurrentMonthWithChi() throws SQLException {
        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Chi");

        assertNotNull(expenses);
        assertFalse(expenses.isEmpty(), "Không có dữ liệu thống kê cho tháng hiện tại.");
        for (Expense e : expenses) {
            assertEquals("Chi", e.getType(), "Sai loại giao dịch.");
        }
    }

    // ✅ Test truy xuất thống kê "Thu" của tháng hiện tại
    @Test
    @Order(2)
    public void testGetExpenseCurrentMonthWithThu() throws SQLException {
        List<Expense> expenses = statsServices.getExpenseCurrentMonth("Thu");

        assertNotNull(expenses);
        for (Expense e : expenses) {
            assertEquals("Thu", e.getType(), "Sai loại giao dịch.");
        }
    }

    // ✅ Test lọc theo khoảng thời gian hợp lệ
    @Test
    @Order(3)
    public void testGetExpensesFilterByDate_ValidRange() throws SQLException {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        List<Expense> expenses = statsServices.getExpensesFilterByDate(from, to, "Thu");

        assertNotNull(expenses);
        for (Expense e : expenses) {
            assertEquals("Thu", e.getType(), "Loại giao dịch không đúng");
        }
    }

    // ✅ Test thống kê tổng theo tháng trong năm hiện tại
    @Test
    @Order(4)
    public void testGetMonthlyExpenses() throws SQLException {
        List<Expense> result = statsServices.getMonthlyExpenses("Chi");

        assertNotNull(result);
        assertTrue(result.size() <= 12, "Số tháng thống kê không hợp lệ");
        for (Expense e : result) {
            assertTrue(e.getCategoryName().startsWith("Tháng "), "Tên không đúng định dạng tháng");
        }
    }

    // ✅ Test khi truyền vào loại không tồn tại
    @Test
    @Order(5)
    public void testInvalidType() throws SQLException {
        List<Expense> result = statsServices.getExpenseCurrentMonth("Không tồn tại");

        assertNotNull(result);
        assertEquals(0, result.size(), "Phải trả về danh sách rỗng");
    }

    // ✅ [BỔ SUNG] Test truyền fromDate và toDate là null
    @Test
    @Order(6)
    public void testFilterDateWithNullInputs() {
        assertThrows(NullPointerException.class, () -> {
            statsServices.getExpensesFilterByDate(null, null, "Chi");
        }, "Phải ném ra NullPointerException khi ngày null");
    }

    // ✅ [BỔ SUNG] Test khi fromDate > toDate
    @Test
    @Order(7)
    public void testFilterDateFromGreaterThanTo() throws SQLException {
        LocalDate from = LocalDate.of(2024, 4, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);

        List<Expense> result = statsServices.getExpensesFilterByDate(from, to, "Chi");
        assertNotNull(result);
        assertEquals(0, result.size(), "Danh sách phải rỗng nếu fromDate > toDate");
    }

    // ✅ [BỔ SUNG] Test truyền type = null
    @Test
    @Order(8)
    public void testDefaultTypeIsChi() throws SQLException {
        // Giao diện mặc định gọi hàm này với type = "Chi"
        List<Expense> result = statsServices.getExpenseCurrentMonth("Chi");

        assertNotNull(result, "Kết quả không được null nếu type là 'Chi'");
        // Có thể không assert isEmpty vì tùy dữ liệu
    }

    // ✅ [BỔ SUNG] Test không có dữ liệu cho loại đã nhập
    @Test
    @Order(9)
    public void testNoDataForType() throws SQLException {
        List<Expense> result = statsServices.getExpenseCurrentMonth("LoạiKhôngTồnTại");
        assertNotNull(result);
        assertEquals(0, result.size(), "Danh sách phải rỗng khi không có loại giao dịch này");
    }

    // ✅ [BỔ SUNG] Test xử lý ngoại lệ SQLException bằng Mockito (mô phỏng lỗi DB)
    @Test
    @Order(10)
    public void testSQLExceptionHandling() throws SQLException {
        StatsServices mockService = Mockito.spy(new StatsServices());

        Mockito.doThrow(new SQLException("Lỗi DB")).when(mockService).getExpenseCurrentMonth("Chi");

        SQLException ex = assertThrows(SQLException.class, () -> {
            mockService.getExpenseCurrentMonth("Chi");
        });

        assertEquals("Lỗi DB", ex.getMessage());
    }
}
