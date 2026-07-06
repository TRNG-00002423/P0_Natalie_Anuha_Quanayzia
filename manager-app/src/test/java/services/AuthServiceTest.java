package services;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Users;
import com.project0.services.AuthService;
import com.project0.util.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private static final String TEST_USERNAME = "Quinn Walton";
    private static final String TEST_PASSWORD = "EXPENSEMANAGER";
    private static final String TEST_ROLE = "MANAGER";

    private AuthService authService;


    @BeforeEach
    void setUp() throws SQLException {
        authService = new AuthService();
        deleteTestUser(); // in case a prior failed run left it behind

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
    void login_validCredentials_returnsUser() {
        Users result = authService.login(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(TEST_ROLE, result.getRole());
    }

    @Test
    void login_wrongPassword_throwsAuthenticationException() {
        assertThrows(AuthenticationException.class,
                () -> authService.login(TEST_USERNAME, "wrongPassword"));
    }

    @Test
    void login_usernameDoesNotExist_throwsUserNotFoundException() {
        assertThrows(UserNotFoundException.class,
                () -> authService.login("definitely_not_a_real_user_98765", "anyPassword"));
    }

    @Test
    void login_nonManager_throwsAuthenticationException() throws SQLException {
        String employeeUsername = "test_employee_login";
        deleteEmployee(employeeUsername);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ExpenseManager_user (username, password, role) VALUES (?, ?, ?)")) {
            stmt.setString(1, employeeUsername);
            stmt.setString(2, TEST_PASSWORD);
            stmt.setString(3, "EMPLOYEE");
            stmt.executeUpdate();
        }


        assertThrows(AuthenticationException.class,
                () -> authService.login(employeeUsername, TEST_PASSWORD));

        deleteEmployee(employeeUsername);
    }

    private void deleteEmployee(String username) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM ExpenseManager_user WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

}


