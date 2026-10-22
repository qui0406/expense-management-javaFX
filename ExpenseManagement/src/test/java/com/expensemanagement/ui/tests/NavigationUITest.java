package com.expensemanagement.ui.tests;

import com.expensemanagement.ui.pages.LoginPage;
import com.expensemanagement.ui.pages.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NavigationUITest - Kiểm thử chuyển trang và luồng điều hướng giữa các màn hình.
 */
@Tag("ui")
public class NavigationUITest extends BaseUITest {

    @Test
    @DisplayName("TC-UI-08: Chu trình điều hướng qua lại giữa Đăng Nhập và Đăng Ký nhiều lần liên tiếp")
    public void testRoundTripNavigation() {
        LoginPage loginPage = new LoginPage(this);
        assertThat(loginPage.isPageLoaded()).isTrue();

        // 1. Chuyển sang Đăng ký
        RegisterPage registerPage = loginPage.clickSwitchToRegister();
        assertThat(registerPage.isPageLoaded()).isTrue();

        // 2. Chuyển lại Đăng nhập
        loginPage = registerPage.clickSwitchToLogin();
        assertThat(loginPage.isPageLoaded()).isTrue();

        // 3. Chuyển sang Đăng ký lần 2 để kiểm tra trạng thái stage ổn định
        registerPage = loginPage.clickSwitchToRegister();
        assertThat(registerPage.isPageLoaded()).isTrue();

        // 4. Chuyển lại Đăng nhập
        loginPage = registerPage.clickSwitchToLogin();
        assertThat(loginPage.isPageLoaded()).isTrue();
    }
}
