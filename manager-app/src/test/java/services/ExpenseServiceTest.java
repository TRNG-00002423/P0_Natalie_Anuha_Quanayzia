package service;

import com.project0.model.Expenses;
import com.project0.services.ExpenseService;
import com.project0.util.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseServiceTest {

    private static final String TEST_DATE = "2026-06-01";
    private static final String TEST_USERNAME = "integration_test_employee";
    private static final String TEST_DESCRIPTION = "integration_test_expense";

    private ExpenseService expenseService;
    private int testUserId;
    private int insertedExpenseId;

    @BeforeEach
    void setUp() throws SQLException {
        expenseService = new ExpenseService();
        cleanupTestRows(); // in case a prior failed run left rows behind

        testUserId = insertTestUser();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_expense (user_id_id, amount, description, created_date, category) VALUES (?, ?, ?, ?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, testUserId);
            stmt.setBigDecimal(2, new BigDecimal("42.50"));
            stmt.setString(3, TEST_DESCRIPTION);
            stmt.setString(4, TEST_DATE);
            stmt.setString(5, TEST_DATE);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    insertedExpenseId = keys.getInt(1);
                }
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        cleanupTestRows();
    }

    private int insertTestUser() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_user (username, password, role) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, TEST_USERNAME);
            stmt.setString(2, "irrelevant");
            stmt.setString(3, "employee");
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to insert test user");
    }

    private void cleanupTestRows() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ExpenseManager_expense WHERE description = ?")) {
                stmt.setString(1, TEST_DESCRIPTION);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ExpenseManager_user WHERE username = ?")) {
                stmt.setString(1, TEST_USERNAME);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void getExpenseById_found_returnsExpense() {
        Expenses result = expenseService.getExpenseById(insertedExpenseId);

        assertNotNull(result);
        assertEquals("integration_test_expense", result.getDescription());
        assertEquals(0, new BigDecimal("42.50").compareTo(result.getAmount()));
    }

    @Test
    void getExpenseById_notFound_returnsNull() {
        assertNull(expenseService.getExpenseById(-1));
    }

    @Test
    void getExpensesByEmployee_returnsInsertedExpense() {
        List<Expenses> result = expenseService.getExpensesByEmployee(testUserId);

        assertEquals(1, result.size());
        assertEquals("integration_test_expense", result.get(0).getDescription());
    }

    @Test
    void getExpensesByDate_returnsInsertedExpense() {
        List<Expenses> result = expenseService.getExpensesByDate(TEST_DATE);

        assertTrue(result.stream()
                .anyMatch(e -> e.getDescription().equals("integration_test_expense")));
    }


}
