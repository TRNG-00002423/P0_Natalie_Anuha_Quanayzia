package com.project0.repository;

import com.project0.model.Expenses;
import com.project0.util.DatabaseConnection;
import com.project0.util.AppLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpensesDAOImpl implements ExpensesDAO {

    private static final Logger logger = AppLogger.getLogger();

    @Override
    public Expenses getExpenseByID(int expense_id) {

        String sqlSelect = "SELECT * FROM ExpenseManager_expense WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setInt(1, expense_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToExpense(rs);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Expenses> getListofExpenses() {
        String sqlSelect = "SELECT * FROM ExpenseManager_expense";
        List<Expenses> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapRowToExpense(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return expenses;
    }

    @Override
    public List<Expenses> getExpensesByEmployee(int employee_id) {
        String sqlSelect = "SELECT * FROM ExpenseManager_expense WHERE user_id_id = ?";
        List<Expenses> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setInt(1, employee_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapRowToExpense(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return expenses;
    }

    @Override
    public List<Expenses> getExpensesByDate(String date) {
        String sqlSelect = "SELECT * FROM ExpenseManager_expense WHERE created_date = ?";
        List<Expenses> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapRowToExpense(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return expenses;
    }

    @Override
    public List<Expenses> getPendingExpenses() {
        String sql = "SELECT e.* FROM ExpenseManager_expense e " +
                "JOIN ExpenseManager_approval a ON e.id = a.expense_id_id " +
                "WHERE a.status = 'pending'";
        List<Expenses> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapRowToExpense(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return expenses;
    }

    @Override
    public List<Expenses> getExpensesByCategory(String category) {
        String sqlSelect = "SELECT * FROM ExpenseManager_expense WHERE category = ?";
        List<Expenses> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapRowToExpense(rs));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        }

        return expenses;
    }

    private Expenses mapRowToExpense(ResultSet rs) throws SQLException {
        return new Expenses(
                rs.getInt("id"),
                rs.getInt("user_id_id"),
                rs.getBigDecimal("amount"),
                rs.getString("description"),
                rs.getString("created_date"),
                rs.getString("category"));
    }
}
