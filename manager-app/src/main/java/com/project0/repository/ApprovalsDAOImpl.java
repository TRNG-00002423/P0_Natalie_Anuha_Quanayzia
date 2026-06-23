package com.project0.repository;

import com.project0.model.Approvals;
import com.project0.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ApprovalsDAOImpl implements ApprovalsDAO {
    @Override
    public Approvals approveOrDenyExpense(int expense_id, int reviewer_id, String status, String comment) {
        String sqlUpdate =
                "UPDATE ExpenseManager_approval " +
                "SET status = ?, comment = ?, reviewer_id = ?, approved_date = ? " +
                "WHERE expense_id_id = ?";

        String reviewDate = LocalDateTime.now().toString();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {

            stmt.setString(1, status);      
            stmt.setString(2, comment);      
            stmt.setInt(3, reviewer_id);     
            stmt.setString(4, reviewDate);   
            stmt.setInt(5, expense_id);   

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getApprovalByExpenseId(expense_id);
    }

    @Override
    public Approvals getApprovalByID(int approval_id) {
        String sqlSelect = "SELECT * FROM ExpenseManager_approval WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setInt(1, approval_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Approvals(
                        rs.getInt("id"),
                        rs.getInt("expense_id_id"),
                        rs.getString("status"),
                        rs.getInt("reviewer_id"),
                        rs.getString("comment"),
                        rs.getString("approved_date")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Approvals getApprovalByExpenseId(int expense_id) {
        String sqlSelect = "SELECT * FROM ExpenseManager_approval WHERE expense_id_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setInt(1, expense_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Approvals(
                        rs.getInt("id"),
                        rs.getInt("expense_id_id"),
                        rs.getString("status"),
                        rs.getInt("reviewer_id"),
                        rs.getString("comment"),
                        rs.getString("approved_date")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Approvals getApprovalByStatus(String status) {
        String sqlSelect = "SELECT * FROM ExpenseManager_approval WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Approvals(
                        rs.getInt("id"),
                        rs.getInt("expense_id_id"),
                        rs.getString("status"),
                        rs.getInt("reviewer_id"),
                        rs.getString("comment"),
                        rs.getString("approved_date")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
