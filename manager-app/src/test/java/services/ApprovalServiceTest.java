package services;

import com.project0.model.Approvals;
import com.project0.services.ApprovalService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalServiceTest {

    ApprovalService approvalService = new ApprovalService();

    @Test
    void reviewExpense_approvesTheExpense() {
        Approvals result = approvalService.reviewExpense(1, 2, "approved", "Looks good");

        assertNotNull(result);
        assertEquals("approved", result.getStatus());
        assertEquals(2, result.getReviewer());
        assertEquals("Looks good", result.getComment());
    }

    @Test
    void reviewExpense_deniesTheExpense() {
        Approvals result = approvalService.reviewExpense(1, 2, "denied", "Missing receipt");

        assertNotNull(result);
        assertEquals("denied", result.getStatus());
    }

    @Test
    void reviewExpense_expenseDoesNotExist_returnsNull() {
        Approvals result = approvalService.reviewExpense(-1, 2, "approved", "nope");

        assertNull(result);
    }
}
