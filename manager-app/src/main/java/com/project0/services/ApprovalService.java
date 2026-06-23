package com.project0.services;

import com.project0.model.Approvals;
import com.project0.repository.ApprovalsDAO;
import com.project0.repository.ApprovalsDAOImpl;

public class ApprovalService {

    private ApprovalsDAO ad;

    public ApprovalService() {
        this.ad = new ApprovalsDAOImpl();
    }

    public Approvals reviewExpense(int expenseId, int reviewerId, String status, String comment) {
        return ad.approveOrDenyExpense(expenseId, reviewerId, status, comment);
    }
}
