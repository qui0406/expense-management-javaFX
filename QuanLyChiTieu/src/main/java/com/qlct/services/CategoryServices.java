/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qlct.services;

import com.qlct.pojo.Category;
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
public class CategoryServices {

    public List<Category> getCatetogires() throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM category WHERE user_id IS NULL OR user_id = ?");
            stm.setInt(1, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("user_id"));
                cates.add(c);
            }
        }
        return cates;
    }

    public List<Category> getCateByType(String type) throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM category WHERE type = ? AND (user_id IS NULL OR user_id = ?)");
            stm.setString(1, type);
            stm.setInt(2, currentUserId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("user_id"));
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
        try (Connection conn = JdbcUtils.getConn()) {
            String newTran = "INSERT INTO category (name, type, user_id) VALUE (?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(newTran);
            stm.setString(1, cate.getName());
            stm.setString(2, cate.getType());
            stm.setInt(3, cate.getUserId());
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteCategory(int id) throws SQLException {
        // Kiểm tra danh mục cố định
        Category cate = getCategoryById(id);
        if (cate == null) {
            throw new SQLException("Không tìm thấy danh mục với id hợp lệ");
        }
        if (cate.getUserId() == 0 || cate.getUserId() == -1) {
            throw new IllegalArgumentException("Không thể xóa danh mục cố định!");
        }
        try (Connection conn = JdbcUtils.getConn()) {
            String delCate = "DELETE FROM category WHERE id =  ?";
            PreparedStatement stm = conn.prepareStatement(delCate);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Không tìm thấy giao dịch với id hợp lệ");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TransactionServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateCategory(int id, String name, String type, int userId) throws SQLException {
        // Tạo đối tượng Category để validate
        Category cate = new Category(id, name, type, userId);
        cate.validate();

        // Kiểm tra danh mục cố định
        Category existingCate = getCategoryById(id);
        if (existingCate == null) {
            throw new SQLException("Không tìm thấy danh mục với id hợp lệ");
        }
        if (existingCate.getUserId() == 0 || existingCate.getUserId() == -1) {
            throw new IllegalArgumentException("Không thể sửa danh mục cố định!");
        }

        // Kiểm tra tính duy nhất của tên
        if (isCategoryNameExists(name, type, id)) {
            throw new IllegalArgumentException("Tên danh mục '" + name + "' với loại '" + type + "' đã tồn tại!");
        }
        String sql = "UPDATE category SET name = ?, type = ?, user_id = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, name);
            stm.setString(2, type);
            stm.setInt(3, userId);
            stm.setInt(4, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
    private Category getCategoryById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM category WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("user_id"));
            }
        }
        return null;
    }

    public List<Category> getCateByTypeFromBudget(String type) throws SQLException {
        List<Category> cates = new ArrayList<>();
        int currentUserId = UserSessionServices.getCurrentUser().getId();

        try (Connection conn = JdbcUtils.getConn()) {
            // Chỉ chọn những category có type tồn tại trong bảng budget
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM category "
                    + "            WHERE type = ? "
                    + "              AND (user_id IS NULL OR user_id = ?) "
                    + "              AND EXISTS ("
                    + "                  SELECT 1 FROM budget "
                    + "                  WHERE budget.type = category.type"
                    + "              )");
            stm.setString(1, type);
            stm.setInt(2, currentUserId);

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("user_id")
                );
                cates.add(c);
            }
        }
        return cates;
    }

    // Phương thức lấy tên danh mục từ ID
    public String getCategoryByIdForBudget(int id) {
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
