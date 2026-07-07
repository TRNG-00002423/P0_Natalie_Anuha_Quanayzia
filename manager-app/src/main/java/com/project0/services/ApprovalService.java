package com.project0.services;

import com.project0.model.Approvals;
import com.project0.repository.ApprovalsDAO;
import com.project0.repository.ApprovalsDAOImpl;
import com.project0.util.AppLogger;

import java.util.logging.Logger;

public class ApprovalService {

    private static final Logger logger = AppLogger.getLogger();

    private ApprovalsDAO ad;

    public ApprovalService() {
        this.ad = new ApprovalsDAOImpl();
    }

    public Approvals reviewExpense(int expenseId, int reviewerId, String status, String comment) {
        Approvals result = ad.approveOrDenyExpense(expenseId, reviewerId, status, comment);

        if (result != null) {
            logger.info("Expense #" + expenseId + " " + status + " by manager #" + reviewerId);
        } else {
            logger.warning("Review failed: no approval found for expense #" + expenseId);
        }

        return result;
    }

    public String getStatus(int expenseId) {
        Approvals approval = ad.getApprovalByExpenseId(expenseId);
        return approval != null ? approval.getStatus() : "none";
    }
}
