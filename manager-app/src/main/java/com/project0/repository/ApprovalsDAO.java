package com.project0.repository;


import com.project0.model.Approvals;

public interface ApprovalsDAO {

    Approvals approveOrDenyExpense(int expense_id, int approval_id);
    Approvals getApprovalByID(int approval_id);
    Approvals getApprovalByStatus(String status);



}
