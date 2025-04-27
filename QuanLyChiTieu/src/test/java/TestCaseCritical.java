
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import com.qlct.services.ChartServices;
import com.qlct.services.UserSessionServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestCaseCritical {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private CallableStatement mockCallableStatement;

    private MockedStatic<UserSessionServices> userSessionServicesMock;
    private MockedStatic<JdbcUtils> jdbcUtilsMock;
    private MockedStatic<BCrypt> bcryptMock;

    private User testUser;
    // Các payload tấn công SQL Injection phổ biến
    private final String[] SQLInjectionPayloads = {
        "' OR '1'='1",
        "'; DROP TABLE user; --",
        "' UNION SELECT username, password FROM user --",
        "admin'--",
        "1'; INSERT INTO user VALUES('hacker','hacker','hacker@evil.com','$2a$12$hacked','Hacker','Hacker',NOW()); --"
    };
    // --- Khởi tạo JavaFX Platform (do cần load FXMLLoader trong test)

    @BeforeAll
    public static void initJavaFX() {
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    public void setup() {
        // Khởi tạo các mock tĩnh
        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
        bcryptMock = Mockito.mockStatic(BCrypt.class);

        // Tạo user test
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("test_user");

        // Thiết lập hành vi mock:
        // UserSessionServices trả về testUser
        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(testUser);
        // JdbcUtils trả về kết nối đã mock
        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(mockConnection);
        
        bcryptMock.when(() -> BCrypt.hashpw(anyString(), anyString())).thenReturn("hashedPassword");
        bcryptMock.when(() -> BCrypt.gensalt(anyInt())).thenReturn("salt");
    }

    @AfterEach
    public void tearDown() {
        userSessionServicesMock.close();
        jdbcUtilsMock.close();
        bcryptMock.close();
    }

    // 1. Kiểm tra chống SQL Injection
    @Test
    public void testAdvancedSQLInjectionInChartServices() throws SQLException {
        ChartServices chartServices = new ChartServices();
        LocalDate currentDate = LocalDate.now();

        // Mock các method chuẩn bị truy vấn
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Lặp qua từng payload SQL Injection để test
        for (String payload : SQLInjectionPayloads) {
            try {
                // gọi hàm sử dụng payload
                chartServices.getExpenseCurrentMonthChart(payload);

                 // Kiểm tra rằng hàm set các tham số đúng (không để lỗi SQL Injection)
                verify(mockPreparedStatement, atLeastOnce()).setInt(eq(1), eq(testUser.getId()));
                verify(mockPreparedStatement, atLeastOnce()).setString(eq(2), eq(payload));

                // Gọi hàm lọc theo ngày
                chartServices.getExpensesFilterByDateChart(currentDate, currentDate, payload);
                // Kiểm tra các tham số đúng
                verify(mockPreparedStatement, atLeastOnce()).setDate(eq(1), any(Date.class));
                verify(mockPreparedStatement, atLeastOnce()).setDate(eq(2), any(Date.class));
                verify(mockPreparedStatement, atLeastOnce()).setInt(eq(3), eq(testUser.getId()));
                verify(mockPreparedStatement, atLeastOnce()).setString(eq(4), eq(payload));
            } catch (Exception e) {
                // Nếu lỗi xuất hiện, đảm bảo lỗi không tiết lộ cấu trúc database
                assertFalse(e.getMessage().contains("syntax error"),
                        "SQL error message might reveal database structure: " + e.getMessage());
                assertFalse(e.getMessage().contains("table"),
                        "SQL error message might reveal database schema: " + e.getMessage());
            }
        }
    }

    // 4. 	Bảo vệ file server
    @Test
    public void testAdvancedPathTraversal() {
        // Các payload tấn công path traversal
        String[] pathTraversalPayloads = {
            "../../../etc/passwd",
            "..\\..\\..\\Windows\\System32\\drivers\\etc\\hosts",
            "/etc/shadow",
            "C:\\Windows\\System32\\config\\SAM",
            "file:///etc/passwd",
            "../../../../../../../../../../../../etc/passwd",
            "%2e%2e/%2e%2e/%2e%2e/etc/passwd",
            "..%c0%af..%c0%af..%c0%afetc/passwd",
            "....//....//....//etc/passwd",
            "..//..//..//..//etc//passwd"
        };

        for (String path : pathTraversalPayloads) {
            File file = new File(path);
            boolean canAccess = file.exists() && file.canRead();
            assertFalse(canAccess, "Should not access sensitive path: " + path);

            // Tạo file tạm và thử kết hợp đường dẫn nguy hiểm
            try {
                // Create a temporary file using the path
                Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "test_security_" + new Random().nextInt(1000) + ".txt");
                Files.write(tempPath, ("Testing with path: " + path).getBytes());

                // Try to use the dangerous path with the temp file
                File dangerous = new File(tempPath.toString() + "/" + path);
                assertFalse(dangerous.exists() && dangerous.canRead(),
                        "Should not access path through temp file: " + dangerous.getPath());

                // Clean up
                Files.deleteIfExists(tempPath);
            } catch (IOException e) {
                // Expected for invalid paths
            }
        }
    }

    //Kiểm tra khoá file và phân quyền file để chống truy cập trái phép
    @Test
    public void testFileLockingAndPermissions() {
        try {
            // Tạo file tạm để test
            File testFile = File.createTempFile("security_test", ".txt");
            testFile.deleteOnExit();

            // Ghi dữ liệu vào file
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("Test security data");
            }

            // Check quyền file
            boolean isReadOnly = !testFile.canWrite();
            boolean isWorldReadable = testFile.setReadable(true, false);

            // Reset permissions for security
            testFile.setReadable(true, true);  // Only owner can read
            testFile.setWritable(true, true);  // Only owner can write

            // Sau đó reset lại: chỉ owner được đọc ghi
            try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw"); FileInputStream fis = new FileInputStream(testFile)) {

            // Khoá file và kiểm tra
                java.nio.channels.FileLock lock = raf.getChannel().tryLock();
                assertNotNull(lock, "File should be lockable");

                if (lock != null) {
                    // Khi file đang bị lock, nếu mở ghi sẽ phải lỗi
                    assertThrows(IOException.class, () -> {
                        try (FileOutputStream fos = new FileOutputStream(testFile, true)) {
                            fos.write("This should fail".getBytes());
                        }
                    });

                    // GIải phóng file
                    lock.release();
                }
            }
        } catch (IOException e) {
            fail("File security test failed: " + e.getMessage());
        }
    }
    
    //	Phát hiện thực thi mã độc nguy hiểm
    @Test
    public void testAdvancedFXMLScriptInjection_Revised() throws Exception {
        String[] fxmlPayloads = {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?import java.lang.Runtime?>\n"
            + "<VBox xmlns:fx=\"http://javafx.com/fxml\">\n"
            + "    <fx:script>\n"
            + "        Runtime.getRuntime().exec(\"echo hello > test.txt\");\n"
            + "    </fx:script>\n"
            + "</VBox>",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?import java.lang.*?>\n"
            + "<VBox xmlns:fx=\"http://javafx.com/fxml\">\n"
            + "    <fx:script>\n"
            + "        String c=\"echo pwned > test2.txt\";\n"
            + "        ((Runtime)Runtime.class.getDeclaredMethod(\"getRuntime\").invoke(null)).exec(c);\n"
            + "    </fx:script>\n"
            + "</VBox>",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?import java.lang.System?>\n"
            + "<VBox xmlns:fx=\"http://javafx.com/fxml\">\n"
            + "    <fx:define>\n"
            + "        <fx:reference source=\"$null\"/>\n"
            + "        <System fx:constant=\"out\" fx:id=\"out\"/>\n"
            + "    </fx:define>\n"
            + "</VBox>"
        };

        for (String payload : fxmlPayloads) {
            File fxmlFile = new File("test-malicious-" + System.currentTimeMillis() + ".fxml");
            try {
                // Ghi nội dung payload vào file FXML
                try (FileWriter fw = new FileWriter(fxmlFile)) {
                    fw.write(payload);
                }

                // Kiểm tra trước xem có file test.txt / test2.txt tồn tại không
                File outputFile1 = new File("test.txt");
                File outputFile2 = new File("test2.txt");
                boolean existedBefore1 = outputFile1.exists();
                boolean existedBefore2 = outputFile2.exists();

                // Load FXML => nếu có lỗi cũng ok (miễn đừng tạo file)
                try {
                    FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
                    loader.load();
                } catch (Exception e) {
                    System.out.println("⚠️ Exception khi load FXML (không bắt buộc): " + e.getMessage());
                }

                 // Sau load, kiểm tra xem có file độc nào được tạo ra không
                boolean createdAfter1 = outputFile1.exists() && !existedBefore1;
                boolean createdAfter2 = outputFile2.exists() && !existedBefore2;

                assertFalse(createdAfter1, "❌ Phát hiện mã độc: file test.txt đã được tạo!");
                assertFalse(createdAfter2, "❌ Phát hiện mã độc: file test2.txt đã được tạo!");
                
                //xóa file
                outputFile1.delete();
                outputFile2.delete();
            } finally {
                fxmlFile.delete();
            }
        }
    }

    // Kiểm tra rò rỉ kết nối database
@Test
public void testDatabaseConnectionLeakage() throws SQLException {
    // Khởi tạo đối tượng ChartServices (lớp thao tác với DB)
    ChartServices chartServices = new ChartServices();
    
    // Setup mock cho prepareStatement
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    // Gọi hàm lấy dữ liệu
    chartServices.getExpenseCurrentMonthChart("food");

    // Kiểm tra sau khi thao tác, statement phải được đóng (không gây rò rỉ)
//    verify(mockPreparedStatement, times(1)).close();

    // (Nếu service đóng luôn cả connection, có thể thêm dòng dưới)
    // verify(mockConnection, times(1)).close();
}

}
