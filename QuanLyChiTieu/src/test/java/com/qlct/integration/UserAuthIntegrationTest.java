package com.qlct.integration;

import com.qlct.services.LoginServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserAuthIntegrationTest - Kiểm thử tích hợp luồng Đăng ký và Xác thực người dùng
 * với cơ sở dữ liệu thật (thực thi câu lệnh SQL và mã hóa mật khẩu BCrypt).
 */
public class UserAuthIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("IT-AUTH-01: Đăng ký người dùng mới lưu thành công vào cơ sở dữ liệu và mã hóa BCrypt")
    public void testRegisterUser_Success() throws SQLException {
        String username = "sdet_tester";
        String rawPassword = "StrongPassword@2026";
        String email = "sdet_tester@example.com";

        boolean registered = LoginServices.registerUser("Anh", "Nguyen", username, rawPassword, email);
        assertThat(registered).as("Hàm registerUser phải trả về true khi thêm thành công").isTrue();

        // Truy vấn trực tiếp database để kiểm tra dữ liệu thực tế
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * FROM user WHERE username = ?")) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();

            assertThat(rs.next()).as("Bản ghi user phải tồn tại trong database").isTrue();
            assertThat(rs.getString("email")).isEqualTo(email);
            assertThat(rs.getString("first_name")).isEqualTo("Anh");
            assertThat(rs.getString("last_name")).isEqualTo("Nguyen");

            String dbPassword = rs.getString("password");
            assertThat(dbPassword)
                    .as("Mật khẩu trong database không được là plain text")
                    .isNotEqualTo(rawPassword);

            assertThat(BCrypt.checkpw(rawPassword, dbPassword))
                    .as("Hash mật khẩu trong database phải khớp với BCrypt")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("IT-AUTH-02: Đăng nhập thành công với tài khoản và mật khẩu chính xác từ DB")
    public void testAuthenticateUser_Success() throws SQLException {
        // Đăng ký trước 1 tài khoản
        String username = "auth_valid_user";
        String password = "MySecret@Pass1";
        LoginServices.registerUser("John", "Doe", username, password, "john@test.com");

        boolean isAuthenticated = LoginServices.checkUserAuth(username, password);
        assertThat(isAuthenticated).as("Đăng nhập phải thành công với mật khẩu đúng").isTrue();
    }

    @Test
    @DisplayName("IT-AUTH-03: Đăng nhập thất bại khi sai mật khẩu")
    public void testAuthenticateUser_WrongPassword() throws SQLException {
        String username = "auth_wrong_pw";
        String password = "CorrectPassword@1";
        LoginServices.registerUser("John", "Doe", username, password, "john_wrong@test.com");

        boolean isAuthenticated = LoginServices.checkUserAuth(username, "IncorrectPassword@99");
        assertThat(isAuthenticated).as("Đăng nhập phải trả về false khi nhập sai mật khẩu").isFalse();
    }

    @Test
    @DisplayName("IT-AUTH-04: Đăng nhập thất bại với tài khoản không tồn tại")
    public void testAuthenticateUser_NonExistentUser() throws SQLException {
        boolean isAuthenticated = LoginServices.checkUserAuth("completely_ghost_user", "AnyPassword123");
        assertThat(isAuthenticated).as("Đăng nhập người dùng không tồn tại phải thất bại").isFalse();
    }

    @Test
    @DisplayName("IT-AUTH-05: Kiểm tra sự tồn tại của Username trong cơ sở dữ liệu")
    public void testCheckUsernameExist() throws SQLException {
        assertThat(LoginServices.checkUsernameExist("admin"))
                .as("Username 'admin' đã được nạp từ test-data.sql phải tồn tại")
                .isTrue();

        assertThat(LoginServices.checkUsernameExist("non_existing_random_user"))
                .as("Username ngẫu nhiên chưa đăng ký phải trả về false")
                .isFalse();
    }

    @Test
    @DisplayName("IT-AUTH-06: Kháng tấn công SQL Injection khi đăng nhập qua Database thực")
    public void testSqlInjectionResilience_Auth() throws SQLException {
        String[] payloads = {
                "' OR '1'='1",
                "admin'--",
                "admin' #",
                "' OR 1=1 --",
                "\" OR \"\"=\""
        };

        for (String payload : payloads) {
            boolean result = LoginServices.checkUserAuth(payload, "any_pass");
            assertThat(result)
                    .as("Payload SQL Injection [" + payload + "] không được phép bypass đăng nhập")
                    .isFalse();
        }
    }
}
