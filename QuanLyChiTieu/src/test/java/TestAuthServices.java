
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.qlct.quanlychitieu.AuthController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
/**
 *
 * @author QUI
 */
public class TestAuthServices {
    private static AuthController authController = new AuthController();
    
    @Test
    public void testCheckIsEmptyFirstName() {
        Assertions.assertTrue(authController.checkIsEmptyFirstName(""));
        Assertions.assertFalse(authController.checkIsEmptyFirstName("John"));
    }

    @Test
    public void testCheckIsEmptyLastName() {
        Assertions.assertTrue(authController.checkIsEmptyLastName(""));
        Assertions.assertFalse(authController.checkIsEmptyLastName("Doe"));
    }

    @Test
    public void testCheckUsernameValid() {
        Assertions.assertTrue(authController.checkUsernameValid(""));
        Assertions.assertFalse(authController.checkUsernameValid("user123"));
    }

    @Test
    public void testCheckUsernameLengthValid() {
        Assertions.assertTrue(authController.checkUsernameLengthValid("ab"));
        Assertions.assertFalse(authController.checkUsernameLengthValid("abc"));
    }

    @Test
    public void testCheckPasswordIsEmpty() {
        Assertions.assertTrue(authController.checkPasswordIsEmpty(""));
        Assertions.assertTrue(authController.checkPasswordIsEmpty(null));
        Assertions.assertFalse(authController.checkPasswordIsEmpty("password123"));
    }

    @Test
    void testCheckConfirmPasswordIsEmpty() {
        Assertions.assertTrue(authController.checkConfirmPasswordIsEmpty(""));
        Assertions.assertTrue(authController.checkConfirmPasswordIsEmpty(null));
        Assertions.assertFalse(authController.checkConfirmPasswordIsEmpty("password123"));
    }

    @Test
    void testCheckPasswordLengthValid() {
        Assertions.assertTrue(authController.checkPasswordLengthValid("1234567"));
        Assertions.assertFalse(authController.checkPasswordLengthValid("12345678"));
    }

    @Test
    void testCheckPasswordSpecialCharacter() {
        Assertions.assertTrue(authController.checkPasswordSpecialCharacter("password"));
        Assertions.assertTrue(authController.checkPasswordSpecialCharacter("Password1"));
        Assertions.assertFalse(authController.checkPasswordSpecialCharacter("Password1!"));
    }

    @Test
    void testCheckPasswordEqualConfirm() {
        Assertions.assertTrue(authController.checkPasswordEqualConfirm("password123", "password321"));
        Assertions.assertFalse(authController.checkPasswordEqualConfirm("password123", "password123"));
    }
    
     @Test
    void testLastNameEmpty() {
        String result = authController.isValidPassword("", "John", "user123", "Password@123", "Password@123");
        Assertions.assertEquals("Họ không được để trống!", result);
    }
    
    @Test
    void testFirstNameEmpty() {
        String result = authController.isValidPassword("Doe", "", "user123", "Password@123", "Password@123");
        Assertions.assertEquals("Tên không được để trống!", result);
    }
    
    @Test
    void testUsernameEmpty() {
        String result = authController.isValidPassword("Doe", "John", "", "Password@123", "Password@123");
        Assertions.assertEquals("Username không được để trống!", result);
    }
    
    @Test
    void testUsernameTooShort() {
        String result = authController.isValidPassword("Doe", "John", "us", "Password@123", "Password@123");
        Assertions.assertEquals("Username phải từ 3 kí tự trở lên", result);
    }
    
    @Test
    void testPasswordEmpty() {
        String result = authController.isValidPassword("Doe", "John", "user123", "", "Password@123");
        Assertions.assertEquals("Mật khẩu không được để trống!", result);
    }
    
    @Test
    void testConfirmPasswordEmpty() {
        String result = authController.isValidPassword("Doe", "John", "user123", "Password@123", "");
        Assertions.assertEquals("Xác nhận mật khẩu không được để trống!", result);
    }
    
    @Test
    void testPasswordTooShort() {
        String result = authController.isValidPassword("Doe", "John", "user123", "Pass@1", "Pass@1");
        Assertions.assertEquals("Mật khẩu phải có ít nhất 8 ký tự!", result);
    }
    
    @Test
    void testPasswordMissingCriteria() {
        String result = authController.isValidPassword("Doe", "John", "user123", "password123", "password123");
        Assertions.assertEquals("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt!", result);
    }
    
    @Test
    void testPasswordMismatch() {
        String result = authController.isValidPassword("Doe", "John", "user123", "Password@123", "Password@124");
        Assertions.assertEquals("Mật khẩu xác nhận không khớp!", result);
    }
    
    @Test
    void testValidPassword() {
        String result = authController.isValidPassword("Doe", "John", "user123", "Password@123", "Password@123");
        Assertions.assertEquals("", result);    
    }

    @Test
    void testIsValidPassword() {
        Assertions.assertEquals("Họ không được để trống!", authController.isValidPassword("", "Doe", "user", "Password1!", "Password1!"));
        Assertions.assertEquals("Username phải từ 3 kí tự trở lên", authController.isValidPassword("John", "Doe", "ab", "Password1!", "Password1!"));
        Assertions.assertEquals("Mật khẩu xác nhận không khớp!", authController.isValidPassword("John", "Doe", "user", "Password1!", "Password2!"));
        Assertions.assertEquals("", authController.isValidPassword("John", "Doe", "user", "Password1!", "Password1!"));
    }
    
    
    
    
}

