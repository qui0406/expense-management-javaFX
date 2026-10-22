package com.expensemanagement.services;

import com.expensemanagement.pojo.JdbcUtils;
import com.expensemanagement.pojo.User;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserSessionServicesTest {

    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;

    @BeforeEach
    public void setUp() {
        mockConn = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);
    }

    @AfterEach
    public void tearDown() {
        UserSessionServices.logout(); 
    }

    @Test
    public void testLoginSuccess() throws Exception {
        try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("id")).thenReturn(1);
            when(mockRs.getString("first_name")).thenReturn("Quí");
            when(mockRs.getString("last_name")).thenReturn("Trần");
            when(mockRs.getString("email")).thenReturn("anhqui@gmail.com");

            UserSessionServices.login("qui");

            User currentUser = UserSessionServices.getCurrentUser();

            assertNotNull(currentUser);
            assertEquals("Quí", currentUser.getFirstName());
            assertEquals("Trần", currentUser.getLastName());
            assertEquals("qui", currentUser.getUsername());
            assertEquals("anhqui@gmail.com", currentUser.getEmail());
        }
    }

    @Test
    public void testLoginAlreadyLoggedIn() throws Exception {
        try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);

            UserSessionServices.login("qui");

            // Gọi login lại, không gọi lại getConn/getDataUser
            UserSessionServices.login("qui1");

            User currentUser = UserSessionServices.getCurrentUser();
            assertNotNull(currentUser);
            assertEquals("qui", currentUser.getUsername());
        }
    }

    @Test
    public void testLogout() throws Exception {
        try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);

            UserSessionServices.login("qui1");

            assertNotNull(UserSessionServices.getCurrentUser());

            UserSessionServices.logout();

            assertNull(UserSessionServices.getCurrentUser());
        }
    }

    @Test
    public void testGetDataUserSuccess() throws Exception {
        try (MockedStatic<JdbcUtils> jdbcMock = mockStatic(JdbcUtils.class)) {
            jdbcMock.when(JdbcUtils::getConn).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("id")).thenReturn(1);
            when(mockRs.getString("first_name")).thenReturn("Quí");
            when(mockRs.getString("last_name")).thenReturn("Trần");
            when(mockRs.getString("email")).thenReturn("anhqui@gmail.com");

            UserSessionServices service = new UserSessionServices("anhqui");
            User user = service.getDataUser();

            assertNotNull(user);
            assertEquals("Quí", user.getFirstName());
            assertEquals("Trần", user.getLastName());
            assertEquals("anhqui", user.getUsername());
            assertEquals("anhqui@gmail.com", user.getEmail());
        }
    }

    @Test
    public void testGetUsernameAndUserId() {
        UserSessionServices service = new UserSessionServices(1, "qui");

        assertEquals("qui", service.getUsername());
        assertEquals(1, service.getUserId());
    }
}
