package com.expensemanagement.ui.tests;

import com.expensemanagement.ui.pages.LoginPage;
import com.expensemanagement.ui.pages.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RegisterUITest - Bộ kịch bản kiểm thử tự động giao diện màn hình Đăng Ký.
 */
@Tag("ui")
public class RegisterUITest extends BaseUITest {

    private LoginPage loginPage;
    private RegisterPage registerPage;

    @BeforeEach
    public void navigateToRegister() {
        loginPage = new LoginPage(this);
        registerPage = loginPage.clickSwitchToRegister();
    }

    @Test
    @DisplayName("TC-UI-05: Màn hình Đăng Ký nạp đầy đủ các trường nhập liệu")
    public void testRegisterPageRender() {
        assertThat(registerPage.isPageLoaded())
                .as("Màn hình đăng ký phải hiển thị đầy đủ các trường yêu cầu")
                .isTrue();

        assertThat(registerPage.isDisplayed(RegisterPage.TXT_LAST_NAME))
                .as("Trường Họ và tên lót phải hiển thị")
                .isTrue();

        assertThat(registerPage.isDisplayed(RegisterPage.TXT_FIRST_NAME))
                .as("Trường Tên phải hiển thị")
                .isTrue();

        assertThat(registerPage.isDisplayed(RegisterPage.TXT_CONFIRM_PASSWORD))
                .as("Trường Xác nhận mật khẩu phải hiển thị")
                .isTrue();
    }

    @Test
    @DisplayName("TC-UI-06: Điền dữ liệu vào toàn bộ form đăng ký")
    public void testFillRegisterForm() {
        registerPage.fillRegistrationForm(
                "Nguyen",
                "Van A",
                "sdet_candidate",
                "sdet_candidate@example.com",
                "P@ssword123",
                "P@ssword123"
        );

        assertThat(registerPage.getUsernameValue())
                .as("Tên đăng nhập đã nhập phải hiển thị trong ô")
                .isEqualTo("sdet_candidate");

        assertThat(registerPage.getEmailValue())
                .as("Email đã nhập phải hiển thị trong ô")
                .isEqualTo("sdet_candidate@example.com");
    }

    @Test
    @DisplayName("TC-UI-07: Điều hướng quay lại màn hình Đăng Nhập từ Đăng Ký")
    public void testNavigateBackToLogin() {
        LoginPage returnedLoginPage = registerPage.clickSwitchToLogin();

        assertThat(returnedLoginPage.isPageLoaded())
                .as("Sau khi bấm quay lại, màn hình Đăng Nhập phải được hiển thị")
                .isTrue();
    }
}
