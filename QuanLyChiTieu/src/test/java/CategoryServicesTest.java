import com.qlct.pojo.Category;
import com.qlct.pojo.JdbcUtils;
import com.qlct.pojo.User;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CategoryServicesTest {

    private CategoryServices categoryServices;
    private User mockUser;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private CallableStatement callableStatement;
    private ResultSet resultSet;

    private MockedStatic<UserSessionServices> userSessionServicesMock;
    private MockedStatic<JdbcUtils> jdbcUtilsMock;

    @BeforeEach
    void setUp() throws SQLException {
        // Tạo mock user
        mockUser = new User(1, "testuser", "password", "Test User", "test@example.com");

        // Mock UserSessionServices.getCurrentUser()
        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

        // Mock các thành phần JDBC
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        callableStatement = mock(CallableStatement.class);
        resultSet = mock(ResultSet.class);

        // Mock JdbcUtils.getConn()
        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(connection);

        // Mock các phương thức connection
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareCall(anyString())).thenReturn(callableStatement);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(callableStatement.executeQuery()).thenReturn(resultSet);

        categoryServices = new CategoryServices();
    }

    @AfterEach
    void tearDown() {
        userSessionServicesMock.close();
        jdbcUtilsMock.close();
    }

    // Kiểm tra validate tên danh mục
    @Test
    @DisplayName("Kiểm tra validate khi tên là null")
    void testValidateNameIsNull() {
        Category category = new Category(1, null, "Chi");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi tên rỗng")
    void testValidateNameIsEmpty() {
        Category category = new Category(1, "", "Chi");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi tên chỉ có khoảng trắng")
    void testValidateNameIsWhitespace() {
        Category category = new Category(1, "   ", "Chi");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi tên có chứa số")
    void testValidateNameContainsNumbers() {
        Category category = new Category(1, "Food123", "Chi");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi tên có ký tự đặc biệt")
    void testValidateNameContainsSpecialChars() {
        Category category = new Category(1, "Ăn uống!@#", "Chi");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", exception.getMessage());
    }

    // Kiểm tra validate loại danh mục
    @Test
    @DisplayName("Kiểm tra validate khi loại là null")
    void testValidateTypeIsNull() {
        Category category = new Category(1, "Ăn uống", null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi loại rỗng")
    void testValidateTypeIsEmpty() {
        Category category = new Category(1, "Ăn uống", "");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi loại chỉ có khoảng trắng")
    void testValidateTypeIsWhitespace() {
        Category category = new Category(1, "Ăn uống", "   ");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra validate khi loại không phải 'Chi' hoặc 'Thu'")
    void testValidateTypeInvalid() {
        Category category = new Category(1, "Ăn uống", "Khác");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
        assertEquals("Loại danh mục phải là 'Chi' hoặc 'Thu'!", exception.getMessage());
    }

    // Kiểm tra validate khi danh mục hợp lệ
    @Test
    @DisplayName("Kiểm tra validate với danh mục hợp lệ loại Chi")
    void testValidCategoryWithTypeChi() {
        Category category = new Category(1, "Ăn uống", "Chi");
        assertDoesNotThrow(category::validate);
    }

    @Test
    @DisplayName("Kiểm tra validate với danh mục hợp lệ loại Thu")
    void testValidCategoryWithTypeThu() {
        Category category = new Category(1, "Lương", "Thu");
        assertDoesNotThrow(category::validate);
    }

    @Test
    @DisplayName("Kiểm tra validate với tên có chữ tiếng Việt")
    void testValidCategoryWithVietnameseChars() {
        Category category = new Category(1, "Ăn uống Đồ gia dụng", "Chi");
        assertDoesNotThrow(category::validate);
    }

    // Kiểm tra phương thức getCatetogires()
    @Test
    @DisplayName("Kiểm tra getCatetogires() trả về danh mục cho người dùng hiện tại")
    void testGetCategories() throws SQLException {
        // Mock ResultSet
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id")).thenReturn(1, 2);
        when(resultSet.getString("name")).thenReturn("Ăn uống", "Di chuyển");
        when(resultSet.getString("type")).thenReturn("Chi", "Chi");
        when(resultSet.getBoolean("is_default")).thenReturn(true, false);

        // Gọi phương thức
        List<Category> categories = categoryServices.getCatetogires();

        // Kiểm tra kết quả
        assertEquals(2, categories.size());
        assertEquals("Ăn uống", categories.get(0).getName());
        assertEquals("Chi", categories.get(0).getType());
        assertTrue(categories.get(0).isDefault());
        assertEquals("Di chuyển", categories.get(1).getName());
        assertEquals("Chi", categories.get(1).getType());
        assertFalse(categories.get(1).isDefault());

        // Kiểm tra tương tác
        verify(callableStatement).setInt(1, mockUser.getId());
    }

    // Kiểm tra phương thức getCateByType()
    @Test
    @DisplayName("Kiểm tra getCateByType() lọc theo loại")
    void testGetCateByType() throws SQLException {
        // Mock ResultSet
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Lương");
        when(resultSet.getString("type")).thenReturn("Thu");
        when(resultSet.getBoolean("is_default")).thenReturn(true);

        // Gọi phương thức
        List<Category> categories = categoryServices.getCateByType("Thu");

        // Kiểm tra kết quả
        assertEquals(1, categories.size());
        assertEquals("Lương", categories.get(0).getName());
        assertEquals("Thu", categories.get(0).getType());
        assertTrue(categories.get(0).isDefault());

        // Kiểm tra tương tác
        verify(callableStatement).setString(1, "Thu");
        verify(callableStatement).setInt(2, mockUser.getId());
    }

    // Kiểm tra phương thức addCategory()
    @Test
    @DisplayName("Kiểm tra addCategory() với danh mục hợp lệ")
    void testAddCategorySuccess() throws SQLException {
        // Mock ResultSet cho generated keys
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(3);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);

        // Mock getCatetogires để trả về danh sách rỗng (không trùng tên)
        CategoryServices spyCategoryServices = spy(categoryServices);
        doReturn(List.of()).when(spyCategoryServices).getCatetogires(); // Return empty list to avoid duplicate name error

        // Tạo danh mục hợp lệ
        Category newCategory = new Category(0, "Du lịch", "Chi");

        // Gọi phương thức
        spyCategoryServices.addCategory(newCategory);

        // Kiểm tra tương tác
        verify(connection).setAutoCommit(false);
        verify(preparedStatement, times(2)).executeUpdate(); // Insert into category and user_cate
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @DisplayName("Kiểm tra addCategory() với tên trùng lặp ném ngoại lệ")
    void testAddCategoryDuplicateName() throws SQLException {
        // Mock getCatetogires để trả về danh sách có tên trùng lặp
        CategoryServices spyCategoryServices = spy(categoryServices);
        doReturn(List.of(new Category(0, "Du lịch", "Chi", false))).when(spyCategoryServices).getCatetogires();

        // Tạo danh mục có tên trùng lặp
        Category duplicateCategory = new Category(0, "Du lịch", "Chi", false);

        // Kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCategoryServices.addCategory(duplicateCategory);
        });
        assertEquals("Tên danh mục 'Du lịch' với loại 'Chi' đã tồn tại!", exception.getMessage());
    }

    @Test
    @DisplayName("Kiểm tra addCategory() với danh mục không hợp lệ ném ngoại lệ")
    void testAddCategoryInvalid() {
        // Tạo danh mục không hợp lệ (tên null)
        Category invalidCategory = new Category(0, null, "Chi");

        // Kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryServices.addCategory(invalidCategory);
        });
        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
    }

    // Kiểm tra phương thức deleteCategory()
    @Test
    @DisplayName("Kiểm tra deleteCategory() với danh mục không mặc định")
    void testDeleteCategorySuccess() throws SQLException {
        // Mock getCategoryById
        CategoryServices spyCategoryServices = spy(categoryServices);
        Category nonDefaultCategory = new Category(3, "Du lịch", "Chi", false);
        doReturn(nonDefaultCategory).when(spyCategoryServices).getCategoryById(3);

        // Mock isCategoryBelongToUser
        doReturn(true).when(spyCategoryServices).isCategoryBelongToUser(3, mockUser.getId());

        // Mock ResultSet cho kiểm tra số lượng người dùng
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); // Không có người dùng khác sử dụng danh mục này

        // Gọi phương thức
        spyCategoryServices.deleteCategory(3);

        // Kiểm tra tương tác
        verify(connection).setAutoCommit(false);
        verify(preparedStatement, times(2)).executeUpdate(); // Xóa từ user_cate, kiểm tra số lượng, xóa category
        verify(connection).commit();
    }

    @Test
    @DisplayName("Kiểm tra deleteCategory() với danh mục mặc định ném ngoại lệ")
    void testDeleteDefaultCategory() throws SQLException {
        // Mock getCategoryById
        CategoryServices spyCategoryServices = spy(categoryServices);
        Category defaultCategory = new Category(1, "Ăn uống", "Chi", true);
        doReturn(defaultCategory).when(spyCategoryServices).getCategoryById(1);

        // Kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCategoryServices.deleteCategory(1);
        });
        assertEquals("Không thể xóa danh mục cố định!", exception.getMessage());
    }

    // Kiểm tra phương thức updateCategory()
    @Test
    @DisplayName("Kiểm tra updateCategory() với danh mục hợp lệ")
    void testUpdateCategorySuccess() throws SQLException {
        // Mock getCategoryById
        CategoryServices spyCategoryServices = spy(categoryServices);
        Category nonDefaultCategory = new Category(3, "Du lịch cũ", "Chi", false);
        doReturn(nonDefaultCategory).when(spyCategoryServices).getCategoryById(3);

        // Mock getCatetogires để trả về danh sách rỗng (không trùng tên)
        doReturn(List.of()).when(spyCategoryServices).getCatetogires();

        // Gọi phương thức
        spyCategoryServices.updateCategory(3, "Du lịch mới", "Chi");

        // Kiểm tra tương tác
        verify(preparedStatement).setString(1, "Du lịch mới");
        verify(preparedStatement).setString(2, "Chi");
        verify(preparedStatement).setInt(3, 3);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    @DisplayName("Kiểm tra updateCategory() với danh mục mặc định ném ngoại lệ")
    void testUpdateDefaultCategory() throws SQLException {
        // Mock getCategoryById
        CategoryServices spyCategoryServices = spy(categoryServices);
        Category defaultCategory = new Category(1, "Ăn uống", "Chi", true);
        doReturn(defaultCategory).when(spyCategoryServices).getCategoryById(1);

        // Kiểm tra ngoại lệ
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            spyCategoryServices.updateCategory(1, "Ăn uống mới", "Chi");
        });
        assertEquals("Không thể sửa danh mục cố định!", exception.getMessage());
    }
}
