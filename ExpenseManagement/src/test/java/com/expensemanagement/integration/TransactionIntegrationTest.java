package com.expensemanagement.integration;

import com.expensemanagement.pojo.Transaction;
import com.expensemanagement.services.TransactionServices;
import com.expensemanagement.services.UserSessionServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionIntegrationTest - Kiểm thử tích hợp các thao tác CRUD và bộ lọc giao dịch
 * trực tiếp trên cơ sở dữ liệu thật.
 */
public class TransactionIntegrationTest extends BaseIntegrationTest {

    private TransactionServices transactionServices;

    @BeforeEach
    public void setUp() throws SQLException {
        transactionServices = new TransactionServices();
        // Thiết lập phiên đăng nhập cho user 'admin' (id = 1) đã được nạp trong test-data.sql
        UserSessionServices.logout();
        UserSessionServices.login("admin");
    }

    @AfterEach
    public void tearDown() {
        UserSessionServices.logout();
    }

    @Test
    @DisplayName("IT-TRAN-01: Thêm mới giao dịch thành công và lưu chính xác vào DB")
    public void testAddTransaction_Success() throws SQLException {
        Transaction tran = new Transaction(
                250000.0,
                1, // Danh mục Ăn uống
                LocalDate.now(),
                "Ăn trưa cùng team",
                1  // User admin
        );

        boolean added = transactionServices.addTransaction(tran);
        assertThat(added).as("Hàm addTransaction phải trả về true").isTrue();

        // Kiểm tra trực tiếp bản ghi trong bảng transaction
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * FROM transaction WHERE note = ?")) {
            stm.setString(1, "Ăn trưa cùng team");
            ResultSet rs = stm.executeQuery();

            assertThat(rs.next()).as("Giao dịch mới thêm phải tồn tại trong database").isTrue();
            assertThat(rs.getDouble("amount")).isEqualTo(250000.0);
            assertThat(rs.getInt("category_id")).isEqualTo(1);
            assertThat(rs.getInt("user_id")).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("IT-TRAN-02: Lọc danh sách giao dịch theo khoảng ngày (Date Range)")
    public void testGetTransactionsByDateRange() throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDate tenDaysAgo = today.minusDays(10);

        // Thêm 2 giao dịch ở các mốc thời gian khác nhau
        transactionServices.addTransaction(new Transaction(100000.0, 1, today, "Giao dịch hôm nay", 1));
        transactionServices.addTransaction(new Transaction(200000.0, 2, threeDaysAgo, "Giao dịch 3 ngày trước", 1));
        transactionServices.addTransaction(new Transaction(300000.0, 1, tenDaysAgo, "Giao dịch 10 ngày trước", 1));

        // Lọc trong 5 ngày gần nhất -> kỳ vọng chỉ có 2 giao dịch (hôm nay và 3 ngày trước)
        List<Transaction> recentTransactions = transactionServices.getTransactionsByDateRange(
                today.minusDays(5),
                today
        );

        assertThat(recentTransactions)
                .as("Bộ lọc trong 5 ngày gần nhất phải trả về đúng 2 giao dịch")
                .hasSize(2)
                .extracting(Transaction::getNote)
                .containsExactlyInAnyOrder("Giao dịch hôm nay", "Giao dịch 3 ngày trước");
    }

    @Test
    @DisplayName("IT-TRAN-03: Cập nhật giao dịch thành công và phản ánh vào DB")
    public void testUpdateTransaction_Success() throws SQLException {
        // 1. Thêm giao dịch ban đầu
        transactionServices.addTransaction(new Transaction(50000.0, 1, LocalDate.now(), "Cafe sáng", 1));

        int tranId = -1;
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT id FROM transaction WHERE note = ?")) {
            stm.setString(1, "Cafe sáng");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                tranId = rs.getInt("id");
            }
        }
        assertThat(tranId).as("Phải lấy được ID của giao dịch vừa thêm").isGreaterThan(0);

        // 2. Thực hiện cập nhật
        transactionServices.updateTransaction(
                tranId,
                75000.0,
                2, // Đổi sang Mua sắm
                LocalDate.now(),
                "Cafe sáng và bánh ngọt",
                1
        );

        // 3. Kiểm tra DB sau khi update
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * FROM transaction WHERE id = ?")) {
            stm.setInt(1, tranId);
            ResultSet rs = stm.executeQuery();

            assertThat(rs.next()).isTrue();
            assertThat(rs.getDouble("amount")).isEqualTo(75000.0);
            assertThat(rs.getInt("category_id")).isEqualTo(2);
            assertThat(rs.getString("note")).isEqualTo("Cafe sáng và bánh ngọt");
        }
    }

    @Test
    @DisplayName("IT-TRAN-04: Xóa giao dịch thành công khỏi DB")
    public void testDeleteTransaction_Success() throws SQLException {
        transactionServices.addTransaction(new Transaction(30000.0, 1, LocalDate.now(), "Giao dịch cần xóa", 1));

        int tranId = -1;
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT id FROM transaction WHERE note = ?")) {
            stm.setString(1, "Giao dịch cần xóa");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                tranId = rs.getInt("id");
            }
        }

        // Thực hiện xóa
        transactionServices.deleteTransaction(tranId);

        // Kiểm tra bản ghi không còn trong database
        try (Connection conn = getTestConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * FROM transaction WHERE id = ?")) {
            stm.setInt(1, tranId);
            ResultSet rs = stm.executeQuery();
            assertThat(rs.next()).as("Giao dịch sau khi xóa không được tồn tại trong DB").isFalse();
        }
    }
}
