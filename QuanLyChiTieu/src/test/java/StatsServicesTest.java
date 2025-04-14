/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author QUI
 */
import com.qlct.pojo.Expense;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import com.qlct.services.StatsServices;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatsServicesTest {
    private Connection conn;
    private PreparedStatement stm;
    private ResultSet rs;
    private StatsServices statsServices;

    private static final int USER_ID = 1;
    private static final String TYPE = "expense";
    
    @BeforeEach
    void setUp() throws Exception {
        conn = mock(Connection.class);
        stm = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        statsServices = new StatsServices();
    }
    
    @Test
    public void testGetExpenseCurrentMonth() throws Exception {
        try (
            MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> mockedSession = mockStatic(UserSessionServices.class)
        ) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);

            mockedSession.when(UserSessionServices::getCurrentUser)
                    .thenReturn(new User(USER_ID, "Nguyen", "Van A", "user", "email@example.com"));

            when(rs.next()).thenReturn(true, false);
            when(rs.getInt("id")).thenReturn(1);
            when(rs.getString("name")).thenReturn("Ăn uống");
            when(rs.getDouble("amount")).thenReturn(100.0);

            List<Expense> result = statsServices.getExpenseCurrentMonth(TYPE);
            Assertions.assertEquals(1, result.size());
        }
    }
    
     @Test
    public void testGetExpensesFilterByDate() throws Exception {
        try (
            MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> mockedSession = mockStatic(UserSessionServices.class)
        ) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);

            mockedSession.when(UserSessionServices::getCurrentUser)
                    .thenReturn(new User(USER_ID, "Nguyen", "Van A", "user", "email@example.com"));

            when(rs.next()).thenReturn(true, false);
            when(rs.getInt("id")).thenReturn(2);
            when(rs.getString("name")).thenReturn("Giải trí");
            when(rs.getDouble("amount")).thenReturn(200.0);

            LocalDate fromDate = LocalDate.of(2024, 4, 1);
            LocalDate toDate = LocalDate.of(2024, 4, 30);

            List<Expense> result = statsServices.getExpensesFilterByDate(fromDate, toDate, TYPE);
            assertEquals(1, result.size());

            Expense e = result.get(0);
//            assertEquals(2, e.getId());
//            assertEquals("Giải trí", e.getName());
            assertEquals(200.0, e.getAmount());
        }
    }

    @Test
    public void testGetMonthlyExpenses() throws Exception {
        try (
            MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class);
            MockedStatic<UserSessionServices> mockedSession = mockStatic(UserSessionServices.class)
        ) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);

            mockedSession.when(UserSessionServices::getCurrentUser)
                    .thenReturn(new User(USER_ID, "Nguyen", "Van A", "user", "email@example.com"));

            when(rs.next()).thenReturn(true, false);
            when(rs.getInt("month")).thenReturn(4);
            when(rs.getDouble("total_amount")).thenReturn(500.0);

            List<Expense> result = statsServices.getMonthlyExpenses(TYPE);
            assertEquals(1, result.size());

            Expense e = result.get(0);
//            assertEquals(4, e.getId());
//            assertEquals("Tháng 4", e.getName());
            assertEquals(500.0, e.getAmount());
        }
    }
}
