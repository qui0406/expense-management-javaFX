package com.qlct.ui.pages;

import org.testfx.api.FxRobot;

/**
 * RegisterPage - Page Object đại diện cho màn hình Đăng Ký (register.fxml).
 */
public class RegisterPage extends BasePage {

    // Locators
    public static final String TXT_LAST_NAME = "#txtLastName";
    public static final String TXT_FIRST_NAME = "#txtFirstName";
    public static final String TXT_USERNAME = "#txtUsernameRegister";
    public static final String TXT_EMAIL = "#txtEmail";
    public static final String TXT_PASSWORD = "#txtPasswordRegister";
    public static final String TXT_CONFIRM_PASSWORD = "#txtCofirmPassword";
    public static final String BTN_REGISTER = "#btnRegister";
    public static final String BTN_SWITCH_LOGIN = "#btnSwitchLogin";

    public RegisterPage(FxRobot robot) {
        super(robot);
    }

    /**
     * Kiểm tra xem màn hình Đăng Ký đã nạp đầy đủ các trường nhập liệu chưa
     */
    public boolean isPageLoaded() {
        try {
            waitFor(TXT_USERNAME, 3);
            return isDisplayed(TXT_USERNAME) && isDisplayed(TXT_EMAIL)
                    && isDisplayed(TXT_PASSWORD) && isDisplayed(BTN_REGISTER);
        } catch (Exception e) {
            return false;
        }
    }

    public RegisterPage enterLastName(String lastName) {
        type(TXT_LAST_NAME, lastName);
        return this;
    }

    public RegisterPage enterFirstName(String firstName) {
        type(TXT_FIRST_NAME, firstName);
        return this;
    }

    public RegisterPage enterUsername(String username) {
        type(TXT_USERNAME, username);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        type(TXT_EMAIL, email);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        type(TXT_PASSWORD, password);
        return this;
    }

    public RegisterPage enterConfirmPassword(String confirmPassword) {
        type(TXT_CONFIRM_PASSWORD, confirmPassword);
        return this;
    }

    /**
     * Điền toàn bộ thông tin đăng ký trong một phương thức tiện ích
     */
    public RegisterPage fillRegistrationForm(String lastName, String firstName, String username,
                                            String email, String password, String confirmPassword) {
        enterLastName(lastName);
        enterFirstName(firstName);
        enterUsername(username);
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);
        return this;
    }

    public void clickRegister() {
        click(BTN_REGISTER);
    }

    /**
     * Nhấn quay lại màn hình Đăng Nhập
     */
    public LoginPage clickSwitchToLogin() {
        click(BTN_SWITCH_LOGIN);
        return new LoginPage(robot);
    }

    public String getUsernameValue() {
        return getText(TXT_USERNAME);
    }

    public String getEmailValue() {
        return getText(TXT_EMAIL);
    }
}
