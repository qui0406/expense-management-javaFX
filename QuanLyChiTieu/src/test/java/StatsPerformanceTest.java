
//import com.qlct.pojo.Transaction;
//import com.qlct.pojo.Expense;
//import com.qlct.pojo.User;
//import com.qlct.services.StatsServices;
//import com.qlct.services.TransactionServices;
//import com.qlct.services.UserSessionServices;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.List;
//import java.util.Random;
//
//public class StatsPerformanceTest {
//
//    private StatsServices statsServices;
//    private TransactionServices transactionServices;
//    private User mockUser;
//    private MockedStatic<UserSessionServices> userSessionServicesMock;
//    private Random random;
//
//    @BeforeEach
//    void setUp() {
//        // Tạo user giả lập
//        mockUser = new User(1, "Quí", "Trần", "qui123", "qui123@gmail.com");
//
//        // Mock static class UserSessionServices
//        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
//        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);
//
//        statsServices = new StatsServices();
//        transactionServices = new TransactionServices();
//        random = new Random();
//    }
//
//    @AfterEach
//    void tearDown() {
//        userSessionServicesMock.close();
//    }
//
//    private void createManyTransactions(int count) throws SQLException {
//        int[] categoryIds = {1, 2, 3, 4, 5};
//        LocalDate today = LocalDate.now();
//
//        for (int i = 0; i < count; i++) {
//            int categoryId = categoryIds[random.nextInt(categoryIds.length)];
//            double amount = 10000 + random.nextDouble() * 9990000;
//            LocalDate date = today.minusDays(random.nextInt(180));
//
//            Transaction transaction = new Transaction(
//                0,
//                amount,
//                categoryId,
//                date,
//                "Performance test transaction #" + i,
//                mockUser.getId()
//            );
//
//            transactionServices.addTransaction(transaction);
//        }
//    }
//
//    @Test
//    void testPerformanceWithLargeDataset() throws SQLException {
//        createManyTransactions(100);
//
//        Instant start = Instant.now();
//        List<Expense> monthlyExpenses = statsServices.getExpenseCurrentMonth("Chi");
//        Instant end = Instant.now();
//
//        long timeElapsedMs = Duration.between(start, end).toMillis();
//        assertNotNull(monthlyExpenses, "Monthly expenses should not be null");
//        System.out.println("Time to get monthly expenses: " + timeElapsedMs + " ms");
//        assertTrue(timeElapsedMs < 3000, "Getting monthly expenses should take less than 3 seconds");
//
//        start = Instant.now();
//        List<Expense> monthlyStatistics = statsServices.getMonthlyExpenses("Chi");
//        end = Instant.now();
//
//        timeElapsedMs = Duration.between(start, end).toMillis();
//        assertNotNull(monthlyStatistics, "Monthly statistics should not be null");
//        System.out.println("Time to get yearly statistics: " + timeElapsedMs + " ms");
//        assertTrue(timeElapsedMs < 3000, "Getting yearly statistics should take less than 3 seconds");
//    }
//}

import com.qlct.pojo.Transaction;
import com.qlct.pojo.Expense;
import com.qlct.pojo.User;
import com.qlct.services.StatsServices;
import com.qlct.services.TransactionServices;
import com.qlct.services.UserSessionServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

public class StatsPerformanceTest {

    private StatsServices statsServices;
    private TransactionServices transactionServices;
    private User mockUser;
    private MockedStatic<UserSessionServices> userSessionServicesMock;
    private Random random;

    @BeforeEach
    void setUp() {
        // Tạo user giả lập
        mockUser = new User(1, "Quí", "Trần", "qui123", "qui123@gmail.com");

        // Mock static class UserSessionServices
        userSessionServicesMock = Mockito.mockStatic(UserSessionServices.class);
        userSessionServicesMock.when(UserSessionServices::getCurrentUser).thenReturn(mockUser);

        statsServices = new StatsServices();
        transactionServices = new TransactionServices();
        random = new Random();
    }

    @AfterEach
    void tearDown() {
        userSessionServicesMock.close();
    }

    private void createManyTransactions(int count) throws SQLException {
        int[] categoryIds = {1, 2, 3, 4, 5};
        LocalDate today = LocalDate.now();

        for (int i = 0; i < count; i++) {
            int categoryId = categoryIds[random.nextInt(categoryIds.length)];
            double amount = 10000 + random.nextDouble() * 9990000;
            LocalDate date = today.minusDays(random.nextInt(180));

            Transaction transaction = new Transaction(
                    0,
                    amount,
                    categoryId,
                    date,
                    "Performance test transaction #" + i,
                    mockUser.getId()
            );

            transactionServices.addTransaction(transaction);
        }
    }

    @Test
    void testPerformanceWithLargeDataset() throws SQLException {
        createManyTransactions(100);

        Instant start = Instant.now();
        List<Expense> monthlyExpenses = statsServices.getExpenseCurrentMonth("Chi");
        Instant end = Instant.now();

        long timeElapsedMs = Duration.between(start, end).toMillis();
        assertNotNull(monthlyExpenses, "Monthly expenses should not be null");
        System.out.println("Time to get monthly expenses: " + timeElapsedMs + " ms");
        assertTrue(timeElapsedMs < 3000, "Getting monthly expenses should take less than 3 seconds");

        start = Instant.now();
        List<Expense> monthlyStatistics = statsServices.getMonthlyExpenses("Chi");
        end = Instant.now();

        timeElapsedMs = Duration.between(start, end).toMillis();
        assertNotNull(monthlyStatistics, "Monthly statistics should not be null");
        System.out.println("Time to get yearly statistics: " + timeElapsedMs + " ms");
        assertTrue(timeElapsedMs < 3000, "Getting yearly statistics should take less than 3 seconds");
    }
    }

