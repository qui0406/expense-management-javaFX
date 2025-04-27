import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import com.qlct.services.ChartServices;
import com.qlct.services.LoginServices;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

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
    private final String[] SQLInjectionPayloads = {
        "' OR '1'='1",
        "'; DROP TABLE user; --",
        "' UNION SELECT username, password FROM user --",
        "admin'--",
        "1'; INSERT INTO user VALUES('hacker','hacker','hacker@evil.com','$2a$12$hacked','Hacker','Hacker',NOW()); --"
    };

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

        // Thiết lập mock
        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(testUser);
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

    // 1. Advanced SQL Injection Tests
    @Test
    public void testAdvancedSQLInjectionInChartServices() throws SQLException {
        ChartServices chartServices = new ChartServices();
        LocalDate currentDate = LocalDate.now();

        // Setup mocks
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Test multiple SQL injection payloads
        for (String payload : SQLInjectionPayloads) {
            try {
                // Test with harmful type parameter
                chartServices.getExpenseCurrentMonthChart(payload);

                // Verify that injection doesn't change the SQL behavior
                verify(mockPreparedStatement, atLeastOnce()).setInt(eq(1), eq(testUser.getId()));
                verify(mockPreparedStatement, atLeastOnce()).setString(eq(2), eq(payload));

                // Test with harmful date range
                chartServices.getExpensesFilterByDateChart(currentDate, currentDate, payload);
                verify(mockPreparedStatement, atLeastOnce()).setDate(eq(1), any(Date.class));
                verify(mockPreparedStatement, atLeastOnce()).setDate(eq(2), any(Date.class));
                verify(mockPreparedStatement, atLeastOnce()).setInt(eq(3), eq(testUser.getId()));
                verify(mockPreparedStatement, atLeastOnce()).setString(eq(4), eq(payload));
            } catch (Exception e) {
                // Should not throw dangerous exceptions that reveal SQL structure
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
        // Extended path traversal payloads
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

            // Try more operations that could be dangerous
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

    //Bảo vệ quyền file, ngăn truy cập trái phép
    @Test
    public void testFileLockingAndPermissions() {
        try {
            // Create a test file
            File testFile = File.createTempFile("security_test", ".txt");
            testFile.deleteOnExit();

            // Write some data
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("Test security data");
            }

            // Check file permissions
            boolean isReadOnly = !testFile.canWrite();
            boolean isWorldReadable = testFile.setReadable(true, false);

            // Reset permissions for security
            testFile.setReadable(true, true);  // Only owner can read
            testFile.setWritable(true, true);  // Only owner can write

            // Try file locking mechanism
            try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw"); FileInputStream fis = new FileInputStream(testFile)) {

                // Lock the file
                java.nio.channels.FileLock lock = raf.getChannel().tryLock();
                assertNotNull(lock, "File should be lockable");

                if (lock != null) {
                    // Verify that the file is locked by trying another stream
                    assertThrows(IOException.class, () -> {
                        try (FileOutputStream fos = new FileOutputStream(testFile, true)) {
                            fos.write("This should fail".getBytes());
                        }
                    });

                    // Release lock
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
                try (FileWriter fw = new FileWriter(fxmlFile)) {
                    fw.write(payload);
                }

                // Check trạng thái file trước khi test
                File outputFile1 = new File("test.txt");
                File outputFile2 = new File("test2.txt");
                boolean existedBefore1 = outputFile1.exists();
                boolean existedBefore2 = outputFile2.exists();

                // Load FXML
                try {
                    FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
                    loader.load();
                } catch (Exception e) {
                    System.out.println("⚠️ Exception khi load FXML (không bắt buộc): " + e.getMessage());
                }

                // Kiểm tra có tạo file độc hại không
                boolean createdAfter1 = outputFile1.exists() && !existedBefore1;
                boolean createdAfter2 = outputFile2.exists() && !existedBefore2;

                assertFalse(createdAfter1, "❌ Phát hiện mã độc: file test.txt đã được tạo!");
                assertFalse(createdAfter2, "❌ Phát hiện mã độc: file test2.txt đã được tạo!");

                outputFile1.delete();
                outputFile2.delete();
            } finally {
                fxmlFile.delete();
            }
        }
    }

    // 6. 	Ngăn rò rỉ dữ liệu DB khi lỗi
    @Test
    public void testDatabaseConnectionLeakage() throws SQLException {
        // Simulate a database connection issue
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Connection error"));

        ChartServices chartServices = new ChartServices();

        Exception exception = assertThrows(SQLException.class, () -> {
            chartServices.getExpenseCurrentMonthChart("Chi");
        });

        // Check if the error message reveals sensitive information
        String exceptionMessage = exception.getMessage();
        assertFalse(exceptionMessage.contains("password"),
                "Error should not expose database credentials");
        assertFalse(exceptionMessage.contains("root"),
                "Error should not expose database user");
        assertFalse(exceptionMessage.contains("jdbc:"),
                "Error should not expose connection string");
    }
}
