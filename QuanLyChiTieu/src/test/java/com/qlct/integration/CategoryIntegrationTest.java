package com.qlct.integration;

import com.qlct.pojo.Category;
import com.qlct.services.CategoryServices;
import com.qlct.services.UserSessionServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CategoryIntegrationTest - Kiểm thử tích hợp danh mục hệ thống (mặc định)
 * và danh mục cá nhân (phân quyền dữ liệu theo từng người dùng).
 */
public class CategoryIntegrationTest extends BaseIntegrationTest {

    private CategoryServices categoryServices;

    @BeforeEach
    public void setUp() throws SQLException {
        categoryServices = new CategoryServices();
        UserSessionServices.logout();
        UserSessionServices.login("admin");
    }

    @AfterEach
    public void tearDown() {
        UserSessionServices.logout();
    }

    @Test
    @DisplayName("IT-CATE-01: Lấy danh sách danh mục mặc định toàn hệ thống")
    public void testGetDefaultCategories() throws SQLException {
        List<Category> categories = categoryServices.getCatetogires();

        assertThat(categories)
                .as("Danh sách danh mục phải nạp được ít nhất 5 danh mục mặc định")
                .hasSizeGreaterThanOrEqualTo(5)
                .extracting(Category::getName)
                .contains("Ăn uống", "Mua sắm", "Lương", "Thưởng", "Di chuyển");
    }

    @Test
    @DisplayName("IT-CATE-02: Phân lập dữ liệu danh mục cá nhân giữa các người dùng (Data Isolation)")
    public void testCustomCategoryIsolationBetweenUsers() throws Exception {
        // 1. Tạo thêm User thứ 2 trong database
        int user2Id = -1;
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement(
                     "INSERT INTO user (username, password, email, first_name, last_name) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, "user2");
            stm.setString(2, "hashed_pw");
            stm.setString(3, "user2@test.com");
            stm.setString(4, "Bob");
            stm.setString(5, "Smith");
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                user2Id = rs.getInt(1);
            }
        }
        assertThat(user2Id).isGreaterThan(0);

        // 2. Thêm một danh mục cá nhân riêng cho user 1 (admin): "Đầu tư Crypto"
        int customCateId = -1;
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement(
                     "INSERT INTO category (name, type, is_default) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, "Đầu tư Crypto");
            stm.setString(2, "Chi");
            stm.setBoolean(3, false); // Danh mục riêng, không phải mặc định
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                customCateId = rs.getInt(1);
            }
        }
        assertThat(customCateId).isGreaterThan(0);

        // Gán danh mục này cho user 1 trong bảng user_cate
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement(
                     "INSERT INTO user_cate (user_id, category_id) VALUES (?, ?)")) {
            stm.setInt(1, 1); // User admin (id = 1)
            stm.setInt(2, customCateId);
            stm.executeUpdate();
        }

        // 3. Kiểm tra: User 1 (admin) PHẢI thấy danh mục "Đầu tư Crypto"
        List<Category> user1Categories = categoryServices.getCatetogires();
        assertThat(user1Categories)
                .extracting(Category::getName)
                .contains("Đầu tư Crypto");

        // 4. Kiểm tra: Chuyển sang phiên đăng nhập của User 2
        UserSessionServices.logout();
        UserSessionServices.login("user2");

        List<Category> user2Categories = categoryServices.getCatetogires();
        assertThat(user2Categories)
                .as("User 2 KHÔNG được thấy danh mục cá nhân của User 1")
                .extracting(Category::getName)
                .doesNotContain("Đầu tư Crypto");
    }
}
