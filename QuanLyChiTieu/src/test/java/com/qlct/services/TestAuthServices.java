
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.qlct.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
/**
 *
 * @author QUI
 */
public class TestAuthServices {
   @Mock
    private Connection conn;

    @Mock
    private PreparedStatement stm;

    @Mock
    private ResultSet rs;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(conn.prepareStatement(anyString())).thenReturn(stm);
        when(stm.executeQuery()).thenReturn(rs);
    }

    @Test
    public void testCheckUserAuth_ValidCredentials() throws SQLException {
        String username = "qui123";
        String password = "12345678aA@";
        String storedHash = "$2a$12$l1CPuxb50nPxe/FqjwBbau4MijJjnA1MM8DOEQQz3c8kyZYiAtT3."; // BCrypt hash for "password123"

        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(storedHash);

        boolean result = LoginServices.checkUserAuth(username, password);

        assertTrue(result);
    }

    @Test
    public void testCheckUserAuth_InvalidCredentials() throws SQLException {
        String username = "testuser";
        String password = "12345678aA@";
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn("$2a$12$l1CPuxb50nPxe/FqjwBbau4MijJjnA1MM8DOEQQz3c8kyZYiAtT3.");

        boolean result = LoginServices.checkUserAuth(username, password);

        assertFalse(result);
    }

    @Test
    public void testCheckUserAuth_UserNotFound() throws SQLException {
        String username = "testuser";
        String password = "password123";
        when(rs.next()).thenReturn(false); // No user found
        boolean result = LoginServices.checkUserAuth(username, password);

        assertFalse(result);
    }
    
    

    @Test
    public void testRegisterUser_Success() throws SQLException {
        String firstName = "John";
        String lastName = "Doe";
        String username = "johndoe";
        String password = "Password123!";
        String email = "johndoe@example.com";

        when(stm.executeUpdate()).thenReturn(1); // Simulate successful insertion

        boolean result = LoginServices.registerUser(firstName, lastName, username, password, email);

        assertTrue(result);
    }

    @Test
    public void testRegisterUser_Failure() throws SQLException {
        String firstName = "John";
        String lastName = "Doe";
        String username = "johndoe";
        String password = "Password123!";
        String email = "johndoe@example.com";

        when(stm.executeUpdate()).thenReturn(0); // Simulate failure to insert

        boolean result = LoginServices.registerUser(firstName, lastName, username, password, email);

        assertFalse(result);
        verify(stm, times(1)).executeUpdate();
    }
    
    
//    @Test
//    public void testValidateRegisterInput_Success() {
//        String firstName = "John";
//        String lastName = "Doe";
//        String username = "johndoe";
//        String password = "Password123!";
//        String confirmPassword = "Password123!";
//
//        String result = LoginServices(firstName, lastName, username, password, confirmPassword);
//
//        assertEquals("", result); // No errors, valid input
//    }
//
//    @Test
//    public void testValidateRegisterInput_Failure() {
//        String firstName = "John";
//        String lastName = "Doe";
//        String username = "jd";
//        String password = "123";
//        String confirmPassword = "1234";
//
//        String result = LoginServices.validateRegisterInput(firstName, lastName, username, password, confirmPassword);
//
//        assertNotEquals("", result); // Validation failure, input not valid
//    }
}

