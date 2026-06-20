package com.project0.repository;


import com.project0.model.Approvals;

public interface ApprovalsDAO {

    Approvals approveOrDenyExpense(int expense_id, int reviewer_id, String status, String comment);
    Approvals getApprovalByID(int approval_id);
    Approvals getApprovalByExpenseId(int expense_id);
    Approvals getApprovalByStatus(String status);



}
