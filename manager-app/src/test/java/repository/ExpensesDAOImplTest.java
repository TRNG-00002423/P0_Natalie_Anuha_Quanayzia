package repository;

import com.project0.model.Expenses;
import com.project0.repository.ExpensesDAO;
import com.project0.repository.ExpensesDAOImpl;
import com.project0.util.DatabaseConnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;

public class ExpensesDAOImplTest {

    private ExpensesDAO expensesDAO;

    private static final int TEST_ID = 9999;
    private static final int TEST_USER_ID = 9998;

    private static final BigDecimal TEST_AMOUNT = new BigDecimal("50.00");
    private static final String TEST_DESCRIPTION = "JUnit Test Expense";
    private static final String TEST_CATEGORY = "MEALS";
     private static final String PASSWORD = "PASSWORD111";

    @BeforeEach
    void setUp() throws SQLException {
        expensesDAO = new ExpensesDAOImpl();
        deleteTestExpense();
        deleteTestUser();
        String hashedPassword = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.setString(2, "test_user_9998");
            stmt.setString(3, hashedPassword);
            stmt.setString(4, "employee");
            stmt.executeUpdate();
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_expense " +
                     "(id, amount, description, user_id_id, category, created_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, TEST_ID);
            stmt.setBigDecimal(2, TEST_AMOUNT);
            stmt.setString(3, TEST_DESCRIPTION);
            stmt.setInt(4, TEST_USER_ID);
            stmt.setString(5, TEST_CATEGORY);
            stmt.setString(6, "2026-06-28 21:30:00");
            stmt.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        deleteTestExpense();
        deleteTestUser();
    }

    private void deleteTestExpense() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM ExpenseManager_expense WHERE id = ?")) {
            stmt.setInt(1, TEST_ID);
            stmt.executeUpdate();
        }
    }

    private void deleteTestUser() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM ExpenseManager_user WHERE id = ?")) {
            stmt.setInt(1, TEST_USER_ID);
            stmt.executeUpdate();
        }
    }

    @Test
    void getExpenseByID_returnsExpenseWhenExists() {
        Expenses expense = expensesDAO.getExpenseByID(TEST_ID);

        assertNotNull(expense);
        assertEquals(TEST_ID, expense.getId());
        assertEquals(0, expense.getAmount().compareTo(TEST_AMOUNT));
        assertEquals(TEST_DESCRIPTION, expense.getDescription());
        assertEquals(TEST_CATEGORY, expense.getCategory());
    }

    @Test
    void getExpenseByID_returnsNullWhenNotFound() {
        Expenses expense = expensesDAO.getExpenseByID(-1);
        assertNull(expense);
    }
}