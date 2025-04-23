package com.qlct.services;

import com.qlct.pojo.Category;
import com.qlct.pojo.UserCategory;
import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ttthu
 */
public class CategoryServices {

    public List<Category> getCatetogires() throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        try (Connection conn = JdbcUtils.getConn()) {
            // Cải thiện câu truy vấn để đảm bảo không bị lỗi khi truy xuất
            String sql = "SELECT DISTINCT c.* FROM category c "
                    + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                    + "WHERE c.is_default = true OR uc.user_id = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getBoolean("is_default"));
                cates.add(c);
            }
        }
        return cates;
    }

    public List<Category> getCateByType(String type) throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();

        try (Connection conn = JdbcUtils.getConn()) {
            // Thêm DISTINCT để tránh duplicate
            String sql = "SELECT DISTINCT c.* FROM category c "
                    + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                    + "WHERE c.type = ? AND (c.is_default = true OR uc.user_id = ?)";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, type);
            stm.setInt(2, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getBoolean("is_default"));
                cates.add(c);
            }
        }
        return cates;
    }

    public void addCategory(Category cate) throws SQLException {
        // Kiểm tra ràng buộc
        cate.validate();
        if (isCategoryNameExists(cate.getName(), cate.getType(), -1)) {
            throw new IllegalArgumentException("Tên danh mục '" + cate.getName() + "' với loại '" + cate.getType() + "' đã tồn tại!");
        }

        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);  // Bật chế độ transaction

            // Thêm danh mục mới
            String newCateSql = "INSERT INTO category (name, type, is_default) VALUES (?, ?, false)";
            stm = conn.prepareStatement(newCateSql, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, cate.getName());
            stm.setString(2, cate.getType());
            int rowsAffected = stm.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Tạo danh mục thất bại, không có dòng nào được thêm vào!");
            }

            // Lấy ID của danh mục vừa thêm
            rs = stm.getGeneratedKeys();
            if (rs.next()) {
                int categoryId = rs.getInt(1);

                // Thêm vào bảng user_cate
                String newUserCateSql = "INSERT INTO user_cate (user_id, category_id) VALUES (?, ?)";
                stm = conn.prepareStatement(newUserCateSql);
                stm.setInt(1, UserSessionServices.getCurrentUser().getId());
                stm.setInt(2, categoryId);
                stm.executeUpdate();
            } else {
                throw new SQLException("Tạo danh mục thất bại, không lấy được ID!");
            }

            conn.commit();  // Commit transaction
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback khi có lỗi
                } catch (SQLException e) {
                    Logger.getLogger(CategoryServices.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(CategoryServices.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);  // Đặt lại trạng thái mặc định
                conn.close();
            }
        }
    }

    public void deleteCategory(int categoryId) throws SQLException {
        // Kiểm tra danh mục cố định
        Category cate = getCategoryById(categoryId);
        if (cate == null) {
            throw new SQLException("Không tìm thấy danh mục với id hợp lệ");
        }
        if (cate.isDefault()) {
            throw new IllegalArgumentException("Không thể xóa danh mục cố định!");
        }

        // Kiểm tra xem người dùng hiện tại có quyền xóa danh mục này không
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        if (!isCategoryBelongToUser(categoryId, currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa danh mục này!");
        }

        Connection conn = null;
        PreparedStatement stm = null;
        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);  // Bật chế độ transaction

            // Xóa liên kết trong bảng user_cate
            String delUserCateSql = "DELETE FROM user_cate WHERE category_id = ? AND user_id = ?";
            stm = conn.prepareStatement(delUserCateSql);
            stm.setInt(1, categoryId);
            stm.setInt(2, currentUserId);
            stm.executeUpdate();

            // Kiểm tra xem danh mục còn được sử dụng bởi người dùng khác không
            String checkUserSql = "SELECT COUNT(*) FROM user_cate WHERE category_id = ?";
            stm = conn.prepareStatement(checkUserSql);
            stm.setInt(1, categoryId);
            ResultSet rs = stm.executeQuery();
            rs.next();
            int userCount = rs.getInt(1);

            // Nếu không còn ai sử dụng, xóa danh mục
            if (userCount == 0) {
                String delCateSql = "DELETE FROM category WHERE id = ?";
                stm = conn.prepareStatement(delCateSql);
                stm.setInt(1, categoryId);
                stm.executeUpdate();
            }

            conn.commit();  // Commit transaction
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();  // Rollback khi có lỗi
                } catch (SQLException e) {
                    Logger.getLogger(CategoryServices.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(CategoryServices.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);  // Đặt lại trạng thái mặc định
                conn.close();
            }
        }
    }

    public void updateCategory(int categoryId, String name, String type) throws SQLException {
        // Tạo đối tượng Category để validate
        Category cate = new Category(categoryId, name, type);
        cate.validate();

        // Kiểm tra danh mục cố định
        Category existingCate = getCategoryById(categoryId);
        if (existingCate == null) {
            throw new SQLException("Không tìm thấy danh mục với id hợp lệ");
        }
        if (existingCate.isDefault()) {
            throw new IllegalArgumentException("Không thể sửa danh mục cố định!");
        }

        // Kiểm tra tính duy nhất của tên
        if (isCategoryNameExists(name, type, categoryId)) {
            throw new IllegalArgumentException("Tên danh mục '" + name + "' với loại '" + type + "' đã tồn tại!");
        }

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE category SET name = ?, type = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, name);
            stm.setString(2, type);
            stm.setInt(3, categoryId);
            stm.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(CategoryServices.class.getName()).log(Level.SEVERE, null, e);
            throw e;
        }
    }

    // Phương thức kiểm tra tính duy nhất của tên danh mục
    private boolean isCategoryNameExists(String name, String type, int excludeId) throws SQLException {
        List<Category> category = getCatetogires();
        for (Category cate : category) {
            if (cate.getName().equalsIgnoreCase(name) && cate.getType().equals(type) && cate.getId() != excludeId) {
                return true;
            }
        }
        return false;
    }

    // Phương thức hỗ trợ để lấy danh mục theo id
    public Category getCategoryById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM category WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getBoolean("is_default"));
            }
        }
        return null;
    }

    // Kiểm tra xem danh mục có thuộc về người dùng hay không
    public boolean isCategoryBelongToUser(int categoryId, int userId) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT COUNT(*) FROM user_cate WHERE category_id = ? AND user_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Category> getCateByTypeFromBudget(String type) throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();

        try (Connection conn = JdbcUtils.getConn()) {
            // Chỉ chọn những category có type tồn tại trong bảng budget
            String sql = "SELECT c.* FROM category c "
                    + "LEFT JOIN user_cate uc ON c.id = uc.category_id "
                    + "WHERE c.type = ? AND (c.is_default = true OR uc.user_id = ?) "
                    + "AND EXISTS (SELECT 1 FROM budget WHERE budget.type = c.type)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, type);
            stm.setInt(2, currentUserId);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getBoolean("is_default")
                );
                cates.add(c);
            }
        }
        return cates;
    }

    // Phương thức lấy tên danh mục từ ID
    public String getCategoryNameById(int id) {
        String name = null; // Giá trị mặc định nếu không tìm thấy
        String query = "SELECT name FROM category WHERE id = ?"; // Câu truy vấn SQL

        try (Connection conn = JdbcUtils.getConn(); // Kết nối đến CSDL
                 PreparedStatement stm = conn.prepareStatement(query)) {

            stm.setInt(1, id); // Thiết lập giá trị ID
            ResultSet rs = stm.executeQuery(); // Thực thi truy vấn
            if (rs.next()) {
                name = rs.getString("name"); // Lấy tên danh mục từ kết quả
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý ngoại lệ
        }
        return name; // Trả về tên danh mục hoặc null
    }
}
