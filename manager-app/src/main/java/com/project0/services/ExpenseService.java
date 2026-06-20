package com.project0.services;

import com.project0.model.Approvals;
import com.project0.model.Expenses;
import com.project0.repository.ApprovalsDAO;
import com.project0.repository.ApprovalsDAOImpl;
import com.project0.repository.ExpensesDAO;
import com.project0.repository.ExpensesDAOImpl;

import java.util.ArrayList;
import java.util.List;

public class ExpenseService {

    private ExpensesDAO ed;

    public ExpenseService() {
        this.ed = new ExpensesDAOImpl();
    }

    public List<Expenses> getPendingExpenses() {
        return ed.getPendingExpenses();
    }

    public List<Expenses> getAllExpenses() {
        return ed.getListofExpenses();
    }

    public List<Expenses> getExpensesByEmployee(int employee_id) {
        return ed.getExpensesByEmployee(employee_id);
    }

    public List<Expenses> getExpensesByDate(String date) {
        return ed.getExpensesByDate(date);
    }

    public Expenses getExpenseById(int expense_id) {
        return ed.getExpenseByID(expense_id);
    }








}
