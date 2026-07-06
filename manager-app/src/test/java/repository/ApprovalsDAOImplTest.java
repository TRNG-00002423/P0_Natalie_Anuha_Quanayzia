package repository;

import com.project0.model.Approvals;
import com.project0.repository.ApprovalsDAO;
import com.project0.repository.ApprovalsDAOImpl;
import com.project0.util.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalsDAOImplTest {

    private static final int TEST_USER_ID = 9997;
    private static final int TEST_EXPENSE_ID = 9997;
    private static final int TEST_REVIEWER_ID = 9996;
    private static final String PASSWORD = "PASSWORD111";

    ApprovalsDAO approvalsDAO = new ApprovalsDAOImpl();

    @BeforeEach
    void setUp() throws SQLException {
        deleteTestData();
        String hashedPassword = BCrypt.hashpw(PASSWORD, BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Insert employee
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, TEST_USER_ID);
                stmt.setString(2, "test_employee_9997");
                stmt.setString(3, hashedPassword);
                stmt.setString(4, "employee");
                stmt.executeUpdate();
            }

            // Insert reviewer
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, TEST_REVIEWER_ID);
                stmt.setString(2, "test_manager_9996");
                stmt.setString(3, hashedPassword);
                stmt.setString(4, "manager");
                stmt.executeUpdate();
            }

            // Insert expense
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ExpenseManager_expense (id, amount, description, user_id_id, category, created_date) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, TEST_EXPENSE_ID);
                stmt.setDouble(2, 50.00);
                stmt.setString(3, "Test expense");
                stmt.setInt(4, TEST_USER_ID);
                stmt.setString(5, "MEALS");
                stmt.setString(6, "2026-06-28 21:30:00");
                stmt.executeUpdate();
            }

            // Insert pending approval
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO ExpenseManager_approval (expense_id_id, status, reviewer_id, comment) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, TEST_EXPENSE_ID);
                stmt.setString(2, "pending");
                stmt.setNull(3, java.sql.Types.INTEGER);
                stmt.setString(4, "");
                stmt.executeUpdate();
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        deleteTestData();
        DatabaseConnection.closeConnection();
    }

    private void deleteTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ExpenseManager_approval WHERE expense_id_id = ?")) {
                stmt.setInt(1, TEST_EXPENSE_ID);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ExpenseManager_expense WHERE id = ?")) {
                stmt.setInt(1, TEST_EXPENSE_ID);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM ExpenseManager_user WHERE id IN (?, ?)")) {
                stmt.setInt(1, TEST_USER_ID);
                stmt.setInt(2, TEST_REVIEWER_ID);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void approveOrDenyExpense_updatesThePendingApproval() {
        Approvals result = approvalsDAO.approveOrDenyExpense(TEST_EXPENSE_ID, TEST_REVIEWER_ID, "approved", "Looks good");

        assertNotNull(result);
        assertEquals("approved", result.getStatus());
        assertEquals(TEST_REVIEWER_ID, result.getReviewer());
        assertEquals("Looks good", result.getComment());
    }
}