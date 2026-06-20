package com.project0.repository;

import com.project0.model.Approvals;

public class ApprovalsDAOImpl implements ApprovalsDAO {
    @Override
    public Approvals approveOrDenyExpense(int expense_id, int reviewer_id, String status, String comment) {
        return null;
    }

    @Override
    public Approvals getApprovalByID(int approval_id) {
        return null;
    }

    @Override
    public Approvals getApprovalByExpenseId(int expense_id) {
        return null;
    }

    @Override
    public Approvals getApprovalByStatus(String status) {
        return null;
    }
}
