package com.qlct.integration;

import com.qlct.pojo.JdbcUtils;
import com.qlct.services.LoginServices;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.junit.jupiter.api.condition.EnabledIf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MySqlContainerIntegrationTest - Kiểm thử tích hợp cấp độ cao nhất của SDET:
 * Tự động khởi động Docker container chứa MySQL 8.4 thực tế bằng Testcontainers.
 * Tự động bỏ qua an toàn (Graceful Skip) nếu môi trường hiện tại chưa bật Docker daemon.
 */
@Tag("integration")
@Tag("docker")
@EnabledIf("isDockerAvailable")
public class MySqlContainerIntegrationTest {

    private static MySQLContainer<?> mysqlContainer;

    static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable t) {
            return false;
        }
    }

    @BeforeAll
    public static void setUpContainer() {
        mysqlContainer = new MySQLContainer<>("mysql:8.4.0")
                .withDatabaseName("qlct_docker_test")
                .withUsername("testuser")
                .withPassword("testpass")
                .withInitScript("schema.sql");

        mysqlContainer.start();
        JdbcUtils.setTestDataSource(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
    }

    @AfterAll
    public static void tearDownContainer() {
        if (mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
        JdbcUtils.resetDefaultDataSource();
    }

    @Test
    @DisplayName("IT-DOCKER-01: Chạy truy vấn trên container MySQL thực tế được cấp phát động")
    public void testDatabaseConnectionOnRealMySqlContainer() throws SQLException {
        assumeTrue(mysqlContainer.isRunning(), "Yêu cầu MySQL Docker Container đang chạy");

        String username = "docker_admin";
        boolean created = LoginServices.registerUser("Docker", "Admin", username, "DockerPass@123", "docker@mysql.com");
        assertThat(created).as("Người dùng phải đăng ký thành công trên MySQL container").isTrue();

        try (Connection conn = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
             PreparedStatement stm = conn.prepareStatement("SELECT * FROM user WHERE username = ?")) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("email")).isEqualTo("docker@mysql.com");
        }
    }
}
