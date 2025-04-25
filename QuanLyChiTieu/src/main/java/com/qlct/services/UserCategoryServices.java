package com.qlct.services;

import com.qlct.pojo.UserCategory;
import com.qlct.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ttthu
 */
public class UserCategoryServices {
    

    public void addUserCategory(int userId, int categoryId) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO user_cate (user_id, category_id) VALUES (?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setInt(2, categoryId);
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserCategoryServices.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public void removeUserCategory(int userId, int categoryId) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM user_cate WHERE user_id = ? AND category_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setInt(2, categoryId);
            stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(UserCategoryServices.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public List<UserCategory> getUserCategoriesByUserId(int userId) throws SQLException {
        List<UserCategory> userCategories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM user_cate WHERE user_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UserCategory uc = new UserCategory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("category_id")
                );
                userCategories.add(uc);
            }
        }
        return userCategories;
    }

    public List<UserCategory> getUserCategoriesByCategoryId(int categoryId) throws SQLException {
        List<UserCategory> userCategories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM user_cate WHERE category_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UserCategory uc = new UserCategory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("category_id")
                );
                userCategories.add(uc);
            }
        }
        return userCategories;
    }

    
    
    

    public boolean assignDefaultCategoriesToUser(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stm = null;
        ResultSet rs = null;

        try {
            conn = JdbcUtils.getConn();
            conn.setAutoCommit(false);

            // Lấy tất cả danh mục mặc định
            String getDefaultCatesSql = "SELECT id FROM category WHERE is_default = true";
            stm = conn.prepareStatement(getDefaultCatesSql);
            rs = stm.executeQuery();

            // Gán các danh mục mặc định cho người dùng mới
            PreparedStatement insertStm = conn.prepareStatement(
                    "INSERT INTO user_cate (user_id, category_id) VALUES (?, ?)");

            boolean hasInserted = false;
            while (rs.next()) {
                int categoryId = rs.getInt("id");

                // Kiểm tra xem liên kết đã tồn tại chưa
                if (!isUserCategoryExists(userId, categoryId)) {
                    insertStm.setInt(1, userId);
                    insertStm.setInt(2, categoryId);
                    insertStm.executeUpdate();
                    hasInserted = true;
                }
            }

            conn.commit();
            return hasInserted;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    Logger.getLogger(UserCategoryServices.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            Logger.getLogger(UserCategoryServices.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stm != null) {
                stm.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    
    
    public boolean assignDefaultCategoriesToUser(int userId, int cateIdDefault) throws SQLException {
        String sql = "INSERT INTO user_cate (user_id, category_id) VALUES (?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, userId);
            stm.setInt(2, cateIdDefault);

            int rowsAffected = stm.executeUpdate(); 

            return rowsAffected > 0; 

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserCategoryExists(int userId, int categoryId) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT COUNT(*) FROM user_cate WHERE user_id = ? AND category_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);
            stm.setInt(2, categoryId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    public List<Integer> getListDefaultCategory() {
        String sql = "SELECT id FROM category WHERE is_default = true"; // Chỉ chọn cột id
        List<Integer> arrIdCate = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                arrIdCate.add(rs.getInt("id")); // Lấy cột id
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Xử lý lỗi nếu có
        }

        return arrIdCate;
    }

    public int isUserDefaultCategoryExists(int userId, int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_cate WHERE user_id = ? AND category_id= ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, userId);
            stm.setInt(2, categoryId);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return rs.getInt(1); // Cột đầu tiên chứa COUNT(*)
            }
        }

        return 0; // Nếu không có kết quả
    }


    
    
}
