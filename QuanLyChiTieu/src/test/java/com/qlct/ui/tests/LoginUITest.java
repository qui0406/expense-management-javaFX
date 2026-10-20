package com.qlct.ui.tests;

import com.qlct.ui.pages.LoginPage;
import com.qlct.ui.pages.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginUITest - Bộ kịch bản kiểm thử tự động giao diện màn hình Đăng Nhập.
 * Áp dụng Page Object Model (POM) và AssertJ fluent assertions.
 */
@Tag("ui")
public class LoginUITest extends BaseUITest {

    private LoginPage loginPage;

    @BeforeEach
    public void initPage() {
        loginPage = new LoginPage(this);
    }

    @Test
    @DisplayName("TC-UI-01: Giao diện Đăng Nhập hiển thị đầy đủ các thành phần điều khiển")
    public void testLoginPageInitialRender() {
        assertThat(loginPage.isPageLoaded())
                .as("Màn hình đăng nhập cần nạp đầy đủ Username, Password và nút Đăng Nhập")
                .isTrue();

        assertThat(loginPage.isLoginButtonEnabled())
                .as("Nút Đăng Nhập phải ở trạng thái sẵn sàng (enabled)")
                .isTrue();

        assertThat(loginPage.getText(LoginPage.BTN_LOGIN))
                .as("Tiêu đề nút đăng nhập phải hiển thị đúng")
                .isEqualTo("Đăng nhập");

        assertThat(loginPage.getText(LoginPage.BTN_SWITCH_REGISTER))
                .as("Nút chuyển sang đăng ký phải hiển thị đúng")
                .isEqualTo("Đăng ký ngay");
    }

    @Test
    @DisplayName("TC-UI-02: Nhập liệu thành công vào ô Tên đăng nhập")
    public void testUsernameInputTyping() {
        String expectedUsername = "sdet_engineer";
        loginPage.enterUsername(expectedUsername);

        assertThat(loginPage.getUsernameValue())
                .as("Giá trị trong ô tên đăng nhập phải khớp với chuỗi đã nhập")
                .isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("TC-UI-03: Nhập liệu thành công vào ô Mật khẩu")
    public void testPasswordInput() {
        String expectedPassword = "SecurePassword@2026";
        loginPage.enterPassword(expectedPassword);

        assertThat(loginPage.getPasswordValue())
                .as("Giá trị trong ô mật khẩu phải được ghi nhận chính xác")
                .isEqualTo(expectedPassword);
    }

    @Test
    @DisplayName("TC-UI-04: Điều hướng từ màn hình Đăng Nhập sang màn hình Đăng Ký")
    public void testNavigationToRegister() {
        RegisterPage registerPage = loginPage.clickSwitchToRegister();

        assertThat(registerPage.isPageLoaded())
                .as("Sau khi click nút chuyển, màn hình Đăng Ký phải được hiển thị")
                .isTrue();
    }
}
