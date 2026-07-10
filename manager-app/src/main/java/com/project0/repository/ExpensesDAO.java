package com.project0.repository;

import com.project0.model.Expenses;

import java.util.List;

public interface ExpensesDAO {

    Expenses getExpenseByID(int expense_id);
    List<Expenses> getListofExpenses();
    // will need to use a join table
    List<Expenses> getExpensesByEmployee(int employee_id);
    List<Expenses> getExpensesByDate(String date);
    List<Expenses> getExpensesByDateRange(String start, String endExclusive);
    List<Expenses> getExpensesByCategory(String category);
    List<Expenses> getPendingExpenses();
    List<Expenses> getExpensesByStatus(String status);





}
