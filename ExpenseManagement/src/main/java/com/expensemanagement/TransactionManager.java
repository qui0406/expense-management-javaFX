package com.expensemanagement;

import com.expensemanagement.pojo.Category;
import com.expensemanagement.pojo.Transaction;
import com.expensemanagement.services.TransactionServices;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

public class TransactionManager {
    private final TransactionServices transactionServices;
    public final Map<Integer, Category> categoryCache;

    public TransactionManager(TransactionServices transactionServices, Map<Integer, Category> categoryCache) {
        this.transactionServices = transactionServices;
        this.categoryCache = categoryCache;
    }

    // Tính tổng thu và chi, cập nhật UI components
    public void updateTotalAmount(ObservableList<Transaction> transactions, Text totalAmountText, Text expenseAmountText) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMinimumFractionDigits(2);

        double incomeAmount = transactions.stream()
                .filter(t -> {
                    Category category = categoryCache.get(t.getCategoryId());
                    return category != null && "Thu".equals(category.getType());
                })
                .mapToDouble(Transaction::getAmount)
                .sum();
        double expenseAmount = transactions.stream()
                .filter(t -> {
                    Category category = categoryCache.get(t.getCategoryId());
                    return category != null && "Chi".equals(category.getType());
                })
                .mapToDouble(Transaction::getAmount)
                .sum();

        if (incomeAmount > 0) {
            totalAmountText.setText(currencyFormat.format(incomeAmount) + " VND");
            totalAmountText.setVisible(true);
        } else {
            totalAmountText.setVisible(false);
        }

        if (expenseAmount > 0) {
            expenseAmountText.setText(currencyFormat.format(expenseAmount) + " VND");
            expenseAmountText.setVisible(true);
        } else {
            expenseAmountText.setVisible(false);
        }
    }

    // Thêm giao dịch
    public void addTransaction(double amount, int categoryId, LocalDate date, String note, int userId) throws SQLException {
        if (amount <= 0 || amount > 1_000_000_000_000.0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày giao dịch không được vượt quá ngày hiện tại!");
        }
        Transaction transaction = new Transaction(amount, categoryId, date, note, userId);
        transactionServices.addTransaction(transaction);
    }

    // Cập nhật giao dịch
    public void updateTransaction(int transactionId, double amount, int categoryId, LocalDate date, String note, int userId) throws SQLException {
        if (amount <= 0 || amount > 1_000_000_000_000.0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0 và không vượt quá 1,000,000,000,000!");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày giao dịch không được vượt quá ngày hiện tại!");
        }
        transactionServices.updateTransaction(transactionId, amount, categoryId, date, note, userId);
    }

    // Lọc giao dịch theo khoảng thời gian
    public ObservableList<Transaction> filterTransactions(String filterType, LocalDate filterDate) throws SQLException {
        if (filterType == null || filterDate == null) {
            throw new IllegalArgumentException("Kiểu lọc và ngày không được để trống!");
        }

        LocalDate startDate;
        LocalDate endDate;

        switch (filterType) {
            case "Ngày":
                startDate = filterDate;
                endDate = filterDate;
                break;
            case "Tuần":
                startDate = filterDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                endDate = startDate.plusDays(6);
                break;
            case "Tháng":
                startDate = filterDate.withDayOfMonth(1);
                endDate = filterDate.withDayOfMonth(filterDate.lengthOfMonth());
                break;
            case "Quý":
                int quarter = (filterDate.getMonthValue() - 1) / 3 + 1;
                startDate = LocalDate.of(filterDate.getYear(), (quarter - 1) * 3 + 1, 1);
                endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());
                break;
            case "Năm":
                startDate = LocalDate.of(filterDate.getYear(), 1, 1);
                endDate = LocalDate.of(filterDate.getYear(), 12, 31);
                break;
            default:
                throw new IllegalArgumentException("Kiểu lọc không hợp lệ!");
        }

        return javafx.collections.FXCollections.observableArrayList(transactionServices.getTransactionsByDateRange(startDate, endDate));
    }

    // Load tất cả giao dịch
    public ObservableList<Transaction> loadAllTransactions() throws SQLException {
        return javafx.collections.FXCollections.observableArrayList(transactionServices.getAllTran());
    }
}