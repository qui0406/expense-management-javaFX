package com.qlct.services;




/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author QUI
 */
import com.qlct.services.LoginServices;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;


public class TestAuthServices {
    
    private Connection mockConnection =mock(Connection.class);
    private PreparedStatement mockStatement= mock(PreparedStatement.class);
    private ResultSet mockResultSet= mock(ResultSet.class);

    
    @Test
    public void testUsernameIsValid(){
       String name ="Qui@";
       assertTrue(LoginServices.hasSymbols(name)); //co chua ki tu dac biet
    }
    
    @Test
    public void testUsernameValid(){
       String name ="Qui";
       assertFalse(LoginServices.hasSymbols(name)); // khong chua ki tu dac biet
    }
    
    @Test
    public void testIsValidEmail_Valid() {
        String email = "test@example.com";
        assertFalse(LoginServices.isValidEmail(email)); //Email hop le
    }

    @Test
    public void testIsValidEmail_Invalid() {
        String email = "invalid-email";
        assertTrue(LoginServices.isValidEmail(email));
    }
    
    @Test
    public void testCheckUsernameExistWhenExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        boolean result = LoginServices.checkUsernameExist("anhquiii", mockConnection);
        assertTrue(result);
    }

    @Test
    public void testCheckUsernameExistWhenNotExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        boolean result = LoginServices.checkUsernameExist("quiNotEsxit", mockConnection);
        assertFalse(result);
    }

    @Test
    public void testCheckEmailExistWhenExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        boolean result = LoginServices.checkEmailExist("email@example.com", mockConnection);
        assertTrue(result);
    }

    @Test
    public void testCheckEmailExistWhenNotExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        boolean result = LoginServices.checkEmailExist("notfound@example.com", mockConnection);
        assertFalse(result);
    }
    
    // Unit test dang nhap
    
    @Test
    public void testCheckUserAuthWhenUserNotFoundReturnsTrue() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        String rawPassword = "Password123!";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashedPassword);

        boolean result = LoginServices.checkUserAuth("notfound", "Password123!", mockConnection);
        assertTrue(result);
    }
    
    @Test
    public void testCheckUserAuthWhenUserNotFoundReturnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        boolean result = LoginServices.checkUserAuth("notfound", "Password123!", mockConnection);
        assertFalse(result);
    }
    
    @Test
    public void testCheckUserAuthWhenPasswordWrongReturnsFalse() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        String rawPassword = "Password123!";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashedPassword);

        boolean result = LoginServices.checkUserAuth("notfound", "12345678aA@", mockConnection);
        assertFalse(result);
    }
    
    // Test dang ky
    
    @Test
    public void testRegisterUserSuccess() throws SQLException{
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        boolean result = LoginServices.registerUser(
                "Trần Lê Anh", "Quí", "qui123456789", "Password123!", "qui@gmail.com", mockConnection);
        assertTrue(result);
        verify(mockStatement, times(1)).executeUpdate();
    }
    
    @Test
    public void testRegisterUserFail() throws SQLException{
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(0);
        boolean result = LoginServices.registerUser(
                "Trần Lê Anh", "Quí", "qui123456789", "Password123!", "qui@gmail.com", mockConnection);
        assertFalse(result);
        verify(mockStatement, times(1)).executeUpdate();
    }
    
    @Test
    public void testRegisterUser_SQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        assertThrows(SQLException.class, () -> {
             LoginServices.registerUser(
                "Trần Lê Anh", "Quí", "qui123456789", "Password123!", "qui@gmail.com", mockConnection);
        });
    }
    
    //Check valid dang ky
     @Test
    public void testEmptyFirstName() throws Exception {
        Connection mockConn = mock(Connection.class);
        String result = LoginServices.validateRegisterInput("", "Nguyen", "user123", "email@example.com",
                "Password1!", "Password1!", mockConn);
        assertEquals("Tên không được để trống!", result);
    }

    @Test
    public void testInvalidEmailFormat() throws Exception {
        Connection mockConn = mock(Connection.class);
        String result = LoginServices.validateRegisterInput("An", "Nguyen", "user123", "invalid-email",
                "Password1!", "Password1!", mockConn);
        assertEquals("Chưa đúng định dạng email", result);
    }

    @Test
    public void testPasswordMismatch() throws Exception {
        Connection mockConn = mock(Connection.class);
        String result = LoginServices.validateRegisterInput("An", "Nguyen", "user123", "a@b.com",
                "Password1!", "WrongConfirm!", mockConn);
        assertEquals("Mật khẩu xác nhận không khớp!", result);
    }


    @Test
    public void testSuccessInput() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // username và email chưa tồn tại

        String result = LoginServices.validateRegisterInput("An", "Nguyen", "newuser", "test@email.com",
                "Password1!", "Password1!", mockConn);

        assertEquals("", result); // valid input
    }
}   

