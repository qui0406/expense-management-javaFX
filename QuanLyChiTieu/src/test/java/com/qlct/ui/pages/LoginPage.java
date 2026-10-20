package com.qlct.ui.pages;

import org.testfx.api.FxRobot;

/**
 * LoginPage - Page Object đại diện cho màn hình Đăng Nhập (login.fxml).
 */
public class LoginPage extends BasePage {

    // Locators
    public static final String TXT_USERNAME = "#txtUsername";
    public static final String TXT_PASSWORD = "#txtPassword";
    public static final String BTN_LOGIN = "#btnLogin";
    public static final String BTN_SWITCH_REGISTER = "#btnSwitchRegister";

    public LoginPage(FxRobot robot) {
        super(robot);
    }

    /**
     * Kiểm tra xem màn hình Đăng Nhập đã nạp đầy đủ các thành phần chính chưa
     */
    public boolean isPageLoaded() {
        try {
            waitFor(TXT_USERNAME, 3);
            return isDisplayed(TXT_USERNAME) && isDisplayed(TXT_PASSWORD) && isDisplayed(BTN_LOGIN);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Nhập tên đăng nhập
     */
    public LoginPage enterUsername(String username) {
        type(TXT_USERNAME, username);
        return this;
    }

    /**
     * Nhập mật khẩu
     */
    public LoginPage enterPassword(String password) {
        type(TXT_PASSWORD, password);
        return this;
    }

    /**
     * Thực hiện click nút Đăng Nhập
     */
    public void clickLogin() {
        click(BTN_LOGIN);
    }

    /**
     * Điền thông tin và thực hiện đăng nhập
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /**
     * Chuyển sang màn hình Đăng Ký
     */
    public RegisterPage clickSwitchToRegister() {
        click(BTN_SWITCH_REGISTER);
        return new RegisterPage(robot);
    }

    /**
     * Lấy giá trị hiện tại trong ô Username
     */
    public String getUsernameValue() {
        return getText(TXT_USERNAME);
    }

    /**
     * Lấy giá trị hiện tại trong ô Password
     */
    public String getPasswordValue() {
        return getText(TXT_PASSWORD);
    }

    /**
     * Kiểm tra nút đăng nhập có đang bật hay không
     */
    public boolean isLoginButtonEnabled() {
        return isEnabled(BTN_LOGIN);
    }
}
