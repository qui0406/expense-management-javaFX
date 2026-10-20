-- Xóa sạch dữ liệu trước khi nạp
DELETE FROM budget;
DELETE FROM transaction;
DELETE FROM user_cate;
DELETE FROM category;
DELETE FROM user;

-- Danh mục mặc định
INSERT INTO category (id, name, type, is_default) VALUES
(1, 'Ăn uống', 'Chi', TRUE),
(2, 'Mua sắm', 'Chi', TRUE),
(3, 'Lương', 'Thu', TRUE),
(4, 'Thưởng', 'Thu', TRUE),
(5, 'Di chuyển', 'Chi', TRUE);

-- Người dùng mẫu (Mật khẩu 'Admin@123' băm bằng BCrypt)
INSERT INTO user (id, username, password, email, first_name, last_name, created_date) VALUES
(1, 'admin', '$2a$12$e09Z9mG1fL67p3yW.pXo.O9eL20r3u8z3V8k4e7q2B2r0e1u9y7e6', 'admin@expense.com', 'Admin', 'System', CURRENT_TIMESTAMP);
