//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.qlct.pojo.Category;
//import com.qlct.pojo.Transaction;
//import java.time.LocalDate;
//
//public class ValidationTest {
//
//    private int userId;
//
//    @BeforeEach
//    public void setUp() {
//        userId = 1; // Sample user ID for testing
//    }
//
//    // ----- CATEGORY TESTS -----
//    @Test
//    public void testValidCategory() {
//        Category category = new Category(1, "Ăn uống", "Chi", userId);
//        // Should not throw exception
//        assertDoesNotThrow(() -> category.validate());
//    }
//
//    @Test
//    public void testCategoryEmptyName() {
//        Category category = new Category(1, "", "Chi", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> category.validate());
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    public void testCategoryNullName() {
//        Category category = new Category(1, null, "Chi", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> category.validate());
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    public void testCategoryWhitespaceName() {
//        Category category = new Category(1, "   ", "Chi", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> category.validate());
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    public void testCategoryEmptyType() {
//        // Test with empty type
//        Category category1 = new Category(1, "Ăn uống", "", userId);
//        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, 
//            () -> category1.validate());
//        assertEquals("Loại danh mục không được để trống!", exception1.getMessage());
//        
//        // Test with null type
//        Category category2 = new Category(1, "Ăn uống", null, userId);
//        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, 
//            () -> category2.validate());
//        assertEquals("Loại danh mục không được để trống!", exception2.getMessage());
//        
//        // Test with whitespace-only type
//        Category category3 = new Category(1, "Ăn uống", "   ", userId);
//        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, 
//            () -> category3.validate());
//        assertEquals("Loại danh mục không được để trống!", exception3.getMessage());
//    }
//
//    @Test
//    public void testCategoryNameWithSpecialChars() {
//        // Test with numbers
//        Category category1 = new Category(1, "Ăn uống123", "Chi", userId);
//        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, 
//            () -> category1.validate());
//        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", 
//                exception1.getMessage());
//        
//        // Test with special characters
//        Category category2 = new Category(1, "Ăn uống!", "Chi", userId);
//        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, 
//            () -> category2.validate());
//        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", 
//                exception2.getMessage());
//    }
//
//    @Test
//    public void testCategoryInvalidType() {
//        Category category = new Category(1, "Ăn uống", "Khác", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
//            () -> category.validate());
//        assertEquals("Loại danh mục phải là 'Chi' hoặc 'Thu'!", exception.getMessage());
//    }
//
//    @Test
//    public void testCategoryValidTypeChi() {
//        Category category = new Category(1, "Ăn uống", "Chi", userId);
//        assertDoesNotThrow(() -> category.validate());
//    }
//
//    @Test
//    public void testCategoryValidTypeThu() {
//        Category category = new Category(1, "Lương", "Thu", userId);
//        assertDoesNotThrow(() -> category.validate());
//    }
//
//    // ----- TRANSACTION TESTS -----
//    @Test
//    public void testValidTransaction() {
//        Transaction transaction = new Transaction(1000000, 1, LocalDate.now(), "Mua đồ ăn", userId);
//        assertDoesNotThrow(() -> transaction.validate());
//    }
//
//    @Test
//    public void testTransactionNegativeAmount() {
//        Transaction transaction = new Transaction(-1000, 1, LocalDate.now(), "Mua đồ ăn", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> transaction.validate());
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    @Test
//    public void testTransactionZeroAmount() {
//        Transaction transaction = new Transaction(0, 1, LocalDate.now(), "Mua đồ ăn", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> transaction.validate());
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    @Test
//    public void testTransactionExcessiveAmount() {
//        Transaction transaction = new Transaction(1_000_000_000_001.0, 1, LocalDate.now(), "Mua đồ ăn", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> transaction.validate());
//        assertEquals("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!", exception.getMessage());
//    }
//
//    @Test
//    public void testTransactionNullDate() {
//        Transaction transaction = new Transaction(1000000, 1, null, "Mua đồ ăn", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> transaction.validate());
//        assertEquals("Ngày giao dịch không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    public void testTransactionFutureDate() {
//        LocalDate futureDate = LocalDate.now().plusDays(1);
//        Transaction transaction = new Transaction(1000000, 1, futureDate, "Mua đồ ăn", userId);
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> transaction.validate());
//        assertEquals("Ngày giao dịch không được vượt quá ngày hiện tại!", exception.getMessage());
//    }
//
//    @Test
//    public void testTransactionInvalidCategory() {
//        // Test with zero category id
//        Transaction transaction1 = new Transaction(1000000, 0, LocalDate.now(), "Mua đồ ăn", userId);
//        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, 
//            () -> transaction1.validate());
//        assertEquals("Danh mục không được để trống!", exception1.getMessage());
//        
//        // Test with negative category id
//        Transaction transaction2 = new Transaction(1000000, -1, LocalDate.now(), "Mua đồ ăn", userId);
//        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, 
//            () -> transaction2.validate());
//        assertEquals("Danh mục không được để trống!", exception2.getMessage());
//    }
//
//    @Test
//    public void testTransactionPastDate() {
//        LocalDate pastDate = LocalDate.now().minusDays(30);
//        Transaction transaction = new Transaction(1000000, 1, pastDate, "Mua đồ ăn", userId);
//        // Should not throw exception
//        assertDoesNotThrow(() -> transaction.validate());
//    }
//
//    @Test
//    public void testTransactionMaxAmount() {
//        Transaction transaction = new Transaction(1_000_000_000_000.0, 1, LocalDate.now(), "Mua bất động sản", userId);
//        // Should not throw exception
//        assertDoesNotThrow(() -> transaction.validate());
//    }
//}
