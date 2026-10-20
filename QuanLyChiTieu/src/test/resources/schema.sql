-- Bảng người dùng (user)
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng danh mục thu / chi (category)
CREATE TABLE IF NOT EXISTS category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE
);

-- Bảng liên kết người dùng và danh mục cá nhân (user_cate)
CREATE TABLE IF NOT EXISTS user_cate (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL
);

-- Bảng giao dịch thu chi (transaction)
CREATE TABLE IF NOT EXISTS transaction (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    category_id INT NOT NULL,
    date DATE NOT NULL,
    note VARCHAR(255),
    user_id INT NOT NULL
);

-- Bảng ngân sách (budget)
CREATE TABLE IF NOT EXISTS budget (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount_budget DOUBLE NOT NULL,
    created_date DATE,
    updated_date DATE,
    user_id INT NOT NULL,
    type VARCHAR(50),
    category_id INT
);
