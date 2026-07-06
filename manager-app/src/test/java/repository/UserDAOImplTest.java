package repository;

import com.project0.model.Users;
import com.project0.repository.UserDAO;
import com.project0.repository.UserDAOImpl;
import com.project0.util.DatabaseConnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserDAOImplTest {

    private static final String TEST_USERNAME = "Elon Page";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "EMPLOYEE";

    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAOImpl();

        deleteTestUser();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_user (username, password, role) VALUES (?, ?, ?)")) {

            stmt.setString(1, TEST_USERNAME);
            stmt.setString(2, TEST_PASSWORD);
            stmt.setString(3, TEST_ROLE);

            stmt.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        deleteTestUser();
         DatabaseConnection.closeConnection();
    }

    private void deleteTestUser() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM ExpenseManager_user WHERE username = ?")) {

            stmt.setString(1, TEST_USERNAME);
            stmt.executeUpdate();
        }
    }

    @Test
    void getUserByUsername_returnUserWhenExists() {
        Users user = userDAO.getUserByUsername(TEST_USERNAME);

        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_ROLE, user.getRole());
    }
}