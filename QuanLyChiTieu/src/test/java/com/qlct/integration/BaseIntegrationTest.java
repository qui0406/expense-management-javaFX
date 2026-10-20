package com.qlct.integration;

import com.qlct.pojo.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * BaseIntegrationTest - Lớp cơ sở cho toàn bộ tầng Integration Test.
 * Tự động thiết lập cơ sở dữ liệu In-Memory H2 chạy ở chế độ tương thích MySQL (MySQL Mode),
 * tự động khởi tạo Schema và nạp Test Data trước mỗi test case.
 */
@Tag("integration")
public abstract class BaseIntegrationTest {

    protected static final String H2_URL = "jdbc:h2:mem:qlct_it_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER,TRANSACTION";
    protected static final String H2_USER = "sa";
    protected static final String H2_PASSWORD = "";

    @BeforeAll
    public static void initDatabase() throws Exception {
        // Trỏ JdbcUtils sang database kiểm thử
        JdbcUtils.setTestDataSource(H2_URL, H2_USER, H2_PASSWORD);

        // Khởi tạo bảng dữ liệu từ schema.sql
        executeSqlScript("/schema.sql");
    }

    @BeforeEach
    public void resetTestData() throws Exception {
        // Nạp lại dữ liệu sạch trước mỗi test case (bảo đảm tính cô lập Idempotency)
        executeSqlScript("/test-data.sql");
    }

    @AfterAll
    public static void tearDownDatabase() {
        JdbcUtils.resetDefaultDataSource();
    }

    protected static Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
    }

    /**
     * Đọc và thực thi toàn bộ câu lệnh trong file SQL từ classpath (loại bỏ comment an toàn)
     */
    protected static void executeSqlScript(String resourcePath) throws Exception {
        try (InputStream is = BaseIntegrationTest.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Không tìm thấy file SQL: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String cleanSql = reader.lines()
                        .map(line -> {
                            int commentIdx = line.indexOf("--");
                            return commentIdx >= 0 ? line.substring(0, commentIdx) : line;
                        })
                        .collect(Collectors.joining("\n"));

                String[] statements = cleanSql.split(";");

                try (Connection conn = getTestConnection();
                     Statement stmt = conn.createStatement()) {
                    for (String sql : statements) {
                        String trimmed = sql.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                }
            }
        }
    }
}
