
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import com.qlct.services.UserSessionServices;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.qlct.quanlychitieu.AuthController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
/**
 *
 * @author QUI
 */
public class UserSessionServicesTest {
   private static final String TEST_USERNAME = "test_user";
   private static final UserSessionServices userSessionServices = new UserSessionServices(TEST_USERNAME);
   
   @Test
    public void testLoginAndGetCurrentUser() throws Exception {
        try (
            MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(com.qlct.pojo.JdbcUtils.class)
        ) {
            // Mocks
            Connection conn = mock(Connection.class);
            PreparedStatement stm = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            

            mockedJdbc.when(com.qlct.pojo.JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);

            // Giả lập dữ liệu ResultSet
            when(rs.next()).thenReturn(true);
            when(rs.getInt("id")).thenReturn(1);
            when(rs.getString("first_name")).thenReturn("Quí");
            when(rs.getString("last_name")).thenReturn("Trần Lê");
            when(rs.getString("email")).thenReturn("qui@gmail.com");

            // Gọi login
            UserSessionServices.login(TEST_USERNAME);

            User currentUser = UserSessionServices.getCurrentUser();
            Assertions.assertNotNull(currentUser);
            Assertions.assertEquals(1, currentUser.getId());
            Assertions.assertEquals("Quí", currentUser.getFirstName());
            Assertions.assertEquals("Trần Lê", currentUser.getLastName());
            Assertions.assertEquals("test_user", currentUser.getUsername());
            Assertions.assertEquals("qui@gmail.com", currentUser.getEmail());
        }
    }
    
     @Test
    public void testLogout() throws Exception {
        // Đầu tiên login
        try (MockedStatic<com.qlct.pojo.JdbcUtils> mockedJdbc = Mockito.mockStatic(com.qlct.pojo.JdbcUtils.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stm = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedJdbc.when(com.qlct.pojo.JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt("id")).thenReturn(1);
            when(rs.getString("first_name")).thenReturn("Trần Lê");
            when(rs.getString("last_name")).thenReturn("Quí");
            when(rs.getString("email")).thenReturn("qui@gmail.com");

            UserSessionServices.login(TEST_USERNAME);
        }

        // Sau đó logout
        UserSessionServices.logout();
        Assertions.assertNull(UserSessionServices.getCurrentUser());
    }

    @Test
    public void testLoginCalledOnce() throws Exception {
        try (MockedStatic<com.qlct.pojo.JdbcUtils> mockedJdbc = Mockito.mockStatic(com.qlct.pojo.JdbcUtils.class)) {
            Connection conn = mock(Connection.class);
            PreparedStatement stm = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedJdbc.when(com.qlct.pojo.JdbcUtils::getConn).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stm);
            when(stm.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt("id")).thenReturn(1);
            when(rs.getString("first_name")).thenReturn("Trần Lê");
            when(rs.getString("last_name")).thenReturn("Quí");
            when(rs.getString("email")).thenReturn("qui@gmail.com");

            UserSessionServices.login(TEST_USERNAME);

            // Gọi lại lần nữa, instance vẫn giữ nguyên
            UserSessionServices.login("another_user");

            // Không thay đổi currentUser
            User user = UserSessionServices.getCurrentUser();
            Assertions.assertNotNull(user);
            Assertions.assertEquals("test_user", user.getUsername());  // vẫn giữ nguyên user đầu tiên
        }
    }

}
