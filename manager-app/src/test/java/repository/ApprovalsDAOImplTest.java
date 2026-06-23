package repository;

import com.project0.model.Approvals;
import com.project0.repository.ApprovalsDAO;
import com.project0.repository.ApprovalsDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApprovalsDAOImplTest {

    ApprovalsDAO approvalsDAO = new ApprovalsDAOImpl();

    @Test
    void approveOrDenyExpense_updatesThePendingApproval() {
        
        Approvals result = approvalsDAO.approveOrDenyExpense(1, 2, "approved", "Looks good");

        assertNotNull(result);                            
        assertEquals("approved", result.getStatus());     
        assertEquals(2, result.getReviewer());            
        assertEquals("Looks good", result.getComment());  
    }
}
