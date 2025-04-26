////import com.qlct.pojo.Category;
////import com.qlct.services.CategoryServices;
////import org.junit.jupiter.api.BeforeEach;
////import org.junit.jupiter.api.Test;
////import org.junit.jupiter.api.DisplayName;
////
////import static org.junit.jupiter.api.Assertions.*;
////
////public class CategoryServicesTest {
////    
////    private CategoryServices categoryServices;
////    
////    @BeforeEach
////    public void setUp() {
////        categoryServices = new CategoryServices();
////    }
////    
////    @Test
////    @DisplayName("Test name validation - name is null")
////    public void testValidateNameIsNull() {
////        // Arrange
////        String nullName = null;
////        String validType = "Chi";
////        Category category = new Category(1, nullName, validType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test name validation - name is empty")
////    public void testValidateNameIsEmpty() {
////        // Arrange
////        String emptyName = "";
////        String validType = "Chi";
////        Category category = new Category(1, emptyName, validType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test name validation - name is whitespace")
////    public void testValidateNameIsWhitespace() {
////        // Arrange
////        String whitespaceOnlyName = "   ";
////        String validType = "Chi";
////        Category category = new Category(1, whitespaceOnlyName, validType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test type validation - type is null")
////    public void testValidateTypeIsNull() {
////        // Arrange
////        String validName = "Ăn uống";
////        String nullType = null;
////        Category category = new Category(1, validName, nullType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test type validation - type is empty")
////    public void testValidateTypeIsEmpty() {
////        // Arrange
////        String validName = "Ăn uống";
////        String emptyType = "";
////        Category category = new Category(1, validName, emptyType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test type validation - type is whitespace")
////    public void testValidateTypeIsWhitespace() {
////        // Arrange
////        String validName = "Ăn uống";
////        String whitespaceOnlyType = "   ";
////        Category category = new Category(1, validName, whitespaceOnlyType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Loại danh mục không được để trống!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test type validation - type is not 'Chi' or 'Thu'")
////    public void testValidateTypeIsNotChiOrThu() {
////        // Arrange
////        String validName = "Ăn uống";
////        String invalidType = "Khác";
////        Category category = new Category(1, validName, invalidType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Loại danh mục phải là 'Chi' hoặc 'Thu'!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test name validation - name contains numbers")
////    public void testValidateNameContainsNumbers() {
////        // Arrange
////        String invalidName = "Ăn uống123";
////        String validType = "Chi";
////        Category category = new Category(1, invalidName, validType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test name validation - name contains special characters")
////    public void testValidateNameContainsSpecialChars() {
////        // Arrange
////        String invalidName = "Ăn uống!@#";
////        String validType = "Chi";
////        Category category = new Category(1, invalidName, validType);
////        
////        // Act & Assert
////        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
////            category.validate();
////        });
////        
////        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", exception.getMessage());
////    }
////    
////    @Test
////    @DisplayName("Test valid category with type Chi")
////    public void testValidCategoryWithTypeChi() {
////        // Arrange
////        String validName = "Ăn uống";
////        String validType = "Chi";
////        Category category = new Category(1, validName, validType);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            category.validate();
////        });
////    }
////    
////    @Test
////    @DisplayName("Test valid category with type Thu")
////    public void testValidCategoryWithTypeThu() {
////        // Arrange
////        String validName = "Lương";
////        String validType = "Thu";
////        Category category = new Category(1, validName, validType);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            category.validate();
////        });
////    }
////    
////    @Test
////    @DisplayName("Test valid category with Vietnamese characters")
////    public void testValidCategoryWithVietnameseChars() {
////        // Arrange
////        String validName = "Ăn uống Đồ gia dụng";
////        String validType = "Chi";
////        Category category = new Category(1, validName, validType);
////        
////        // Act & Assert
////        assertDoesNotThrow(() -> {
////            category.validate();
////        });
////    }
////}
//import com.qlct.pojo.Category;
//import com.qlct.pojo.JdbcUtils;
//import com.qlct.pojo.User;
//import com.qlct.services.CategoryServices;
//import com.qlct.services.UserSessionServices;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.sql.*;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//public class CategoryServicesTest {
//
//    private CategoryServices categoryServices;
//    private User mockUser;
//
//    private Connection connection;
//    private PreparedStatement preparedStatement;
//    private CallableStatement callableStatement;
//    private ResultSet resultSet;
//
//    private MockedStatic<UserSessionServices> userSessionServicesMock;
//    private MockedStatic<JdbcUtils> jdbcUtilsMock;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // Create a mock user
//        mockUser = new User(1, "testuser", "password", "Test User", "test@example.com");
//
//        // Mock UserSessionServices.getCurrentUser()
//        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
//        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//        // Mock JDBC components
//        connection = mock(Connection.class);
//        preparedStatement = mock(PreparedStatement.class);
//        callableStatement = mock(CallableStatement.class);
//        resultSet = mock(ResultSet.class);
//
//        // Mock JdbcUtils.getConn()
//        jdbcUtilsMock = Mockito.mockStatic(JdbcUtils.class);
//        jdbcUtilsMock.when(JdbcUtils::getConn).thenReturn(connection);
//
//        // Mock connection methods
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//        when(connection.prepareCall(anyString())).thenReturn(callableStatement);
//        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(callableStatement.executeQuery()).thenReturn(resultSet);
//
//        categoryServices = new CategoryServices();
//    }
//
//    @AfterEach
//    void tearDown() {
//        userSessionServicesMock.close();
//        jdbcUtilsMock.close();
//    }
//
//    // Test getCatetogires() method
//    @Test
//    @DisplayName("Test getCatetogires() returns categories for current user")
//    void testGetCategories() throws SQLException {
//        // Mock ResultSet
//        when(resultSet.next()).thenReturn(true, true, false);
//        when(resultSet.getInt("id")).thenReturn(1, 2);
//        when(resultSet.getString("name")).thenReturn("Food", "Transport");
//        when(resultSet.getString("type")).thenReturn("Chi", "Chi");
//        when(resultSet.getBoolean("is_default")).thenReturn(true, false);
//
//        // Call the method
//        List<Category> categories = categoryServices.getCatetogires();
//
//        // Verify results
//        assertEquals(2, categories.size());
//        assertEquals("Food", categories.get(0).getName());
//        assertEquals("Chi", categories.get(0).getType());
//        assertTrue(categories.get(0).isDefault());
//        assertEquals("Transport", categories.get(1).getName());
//        assertEquals("Chi", categories.get(1).getType());
//        assertFalse(categories.get(1).isDefault());
//
//        // Verify interactions
//        verify(callableStatement).setInt(1, mockUser.getId());
//    }
//
//    // Test getCateByType() method
//    @Test
//    @DisplayName("Test getCateByType() filters by type")
//    void testGetCateByType() throws SQLException {
//        // Mock ResultSet
//        when(resultSet.next()).thenReturn(true, false);
//        when(resultSet.getInt("id")).thenReturn(1);
//        when(resultSet.getString("name")).thenReturn("Salary");
//        when(resultSet.getString("type")).thenReturn("Thu");
//        when(resultSet.getBoolean("is_default")).thenReturn(true);
//
//        // Call the method
//        List<Category> categories = categoryServices.getCateByType("Thu");
//
//        // Verify results
//        assertEquals(1, categories.size());
//        assertEquals("Salary", categories.get(0).getName());
//        assertEquals("Thu", categories.get(0).getType());
//        assertTrue(categories.get(0).isDefault());
//
//        // Verify interactions
//        verify(callableStatement).setString(1, "Thu");
//        verify(callableStatement).setInt(2, mockUser.getId());
//    }
//
//    // Test addCategory() with valid data
//    @Test
//    @DisplayName("Test addCategory() with valid category")
//    void testAddCategorySuccess() throws SQLException {
//        // Mock ResultSet for generated keys
//        ResultSet generatedKeys = mock(ResultSet.class);
//        when(generatedKeys.next()).thenReturn(true);
//        when(generatedKeys.getInt(1)).thenReturn(3);
//        when(preparedStatement.executeUpdate()).thenReturn(1);
//        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
//
//        // Mock getCatetogires to return empty list (no duplicate names)
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        when(spyCategoryServices.getCatetogires()).thenReturn(List.of());
//
//        // Create a valid category
//        Category newCategory = new Category(0, "Travel", "Chi");
//
//        // Call the method
//        spyCategoryServices.addCategory(newCategory);
//
//        // Verify interactions
//        verify(connection).setAutoCommit(false);
//        verify(preparedStatement, times(2)).executeUpdate(); // Insert into category and user_cate
//        verify(connection).commit();
//        verify(connection).setAutoCommit(true);
//    }
//
//    // Test addCategory() with duplicate name
//    @Test
//    @DisplayName("Test addCategory() with duplicate name throws exception")
//    void testAddCategoryDuplicateName() throws SQLException {
//        // Mock getCatetogires to return a list with a duplicate name
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        when(spyCategoryServices.getCatetogires()).thenReturn(List.of(
//                new Category(1, "Travel", "Chi", false)
//        ));
//
//        // Create a category with a duplicate name
//        Category duplicateCategory = new Category(0, "Travel", "Chi");
//
//        // Verify exception
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            spyCategoryServices.addCategory(duplicateCategory);
//        });
//        assertEquals("Tên danh mục 'Travel' với loại 'Chi' đã tồn tại!", exception.getMessage());
//    }
//
//    // Test addCategory() with invalid category (validation fails)
//    @Test
//    @DisplayName("Test addCategory() with invalid category throws exception")
//    void testAddCategoryInvalid() {
//        // Create an invalid category (null name)
//        Category invalidCategory = new Category(0, null, "Chi");
//
//        // Verify exception
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            categoryServices.addCategory(invalidCategory);
//        });
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    // Test deleteCategory() with non-default category
//    @Test
//    @DisplayName("Test deleteCategory() with non-default category")
//    void testDeleteCategorySuccess() throws SQLException {
//        // Mock getCategoryById
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        Category nonDefaultCategory = new Category(3, "Travel", "Chi", false);
//        doReturn(nonDefaultCategory).when(spyCategoryServices).getCategoryById(3);
//
//        // Mock isCategoryBelongToUser
//        doReturn(true).when(spyCategoryServices).isCategoryBelongToUser(3, mockUser.getId());
//
//        // Mock ResultSet for user count check
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getInt(1)).thenReturn(0); // No other users using this category
//
//        // Call the method
//        spyCategoryServices.deleteCategory(3);
//
//        // Verify interactions
////        verify(connection).setAutoCommit(false);
//        verify(preparedStatement, times(3)).executeUpdate(); // Delete from user_cate, check count, delete category
////        verify(connection).commit();
//    }
//
//    // Test deleteCategory() with default category
//    @Test
//    @DisplayName("Test deleteCategory() with default category throws exception")
//    void testDeleteDefaultCategory() throws SQLException {
//        // Mock getCategoryById
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        Category defaultCategory = new Category(1, "Food", "Chi", true);
//        doReturn(defaultCategory).when(spyCategoryServices).getCategoryById(1);
//
//        // Verify exception
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            spyCategoryServices.deleteCategory(1);
//        });
//        assertEquals("Không thể xóa danh mục cố định!", exception.getMessage());
//    }
//
//    // Test updateCategory() with valid data
//    @Test
//    @DisplayName("Test updateCategory() with valid category")
//    void testUpdateCategorySuccess() throws SQLException {
//        // Mock getCategoryById
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        Category nonDefaultCategory = new Category(3, "Old Travel", "Chi", false);
//        doReturn(nonDefaultCategory).when(spyCategoryServices).getCategoryById(3);
//
//        // Mock getCatetogires to return empty list (no duplicate names)
//        doReturn(List.of()).when(spyCategoryServices).getCatetogires();
//
//        // Call the method
//        spyCategoryServices.updateCategory(3, "New Travel", "Chi");
//
//        // Verify interactions
//        verify(preparedStatement).setString(1, "New Travel");
//        verify(preparedStatement).setString(2, "Chi");
//        verify(preparedStatement).setInt(3, 3);
//        verify(preparedStatement).executeUpdate();
//    }
//
//    // Test updateCategory() with default category
//    @Test
//    @DisplayName("Test updateCategory() with default category throws exception")
//    void testUpdateDefaultCategory() throws SQLException {
//        // Mock getCategoryById
//        CategoryServices spyCategoryServices = spy(categoryServices);
//        Category defaultCategory = new Category(1, "Food", "Chi", true);
//        doReturn(defaultCategory).when(spyCategoryServices).getCategoryById(1);
//
//        // Verify exception
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            spyCategoryServices.updateCategory(1, "New Food", "Chi");
//        });
//        assertEquals("Không thể sửa danh mục cố định!", exception.getMessage());
//    }
//
//    // Test validation constraints for Category
//    @Test
//    @DisplayName("Test validation - name is null")
//    void testValidateNameIsNull() {
//        Category category = new Category(1, null, "Chi");
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - name is empty")
//    void testValidateNameIsEmpty() {
//        Category category = new Category(1, "", "Chi");
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
//        assertEquals("Tên danh mục không được để trống!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - name contains numbers")
//    void testValidateNameContainsNumbers() {
//        Category category = new Category(1, "Food123", "Chi");
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
//        assertEquals("Tên danh mục chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt!", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Test validation - type is invalid")
//    void testValidateTypeInvalid() {
//        Category category = new Category(1, "Food", "Other");
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, category::validate);
//        assertEquals("Loại danh mục phải là 'Chi' hoặc 'Thu'!", exception.getMessage());
//    }
//}