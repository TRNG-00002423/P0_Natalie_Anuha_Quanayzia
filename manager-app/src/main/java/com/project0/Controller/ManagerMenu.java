package com.project0.Controller;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Expenses;
import com.project0.model.Users;
import com.project0.services.AuthService;
import com.project0.services.ExpenseService;
import com.project0.model.Approvals;
import com.project0.services.ApprovalService;

import java.util.List;
import java.util.Scanner;

public class ManagerMenu {


    private AuthService as=new AuthService();
    private ExpenseService es= new ExpenseService();
    private ApprovalService approvalService= new ApprovalService();
    private Users currentUser;
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        login();
        displayMenu();
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            currentUser = as.login(username, password);
        } catch (UserNotFoundException | AuthenticationException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (!"manager".equalsIgnoreCase(currentUser.getRole())) {
            System.out.println("This account is not a manager account.");
            currentUser = null;
            return false;
        }
        return true;
    }


    private void displayMenu(){
        boolean running=true;

        while(running){
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. View Pending Expenses");
            System.out.println("2. Review an Expense (Approve/Deny)");
            System.out.println("3. Generate Reports");
            System.out.println("4. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();

            switch(choice){

                case "1" -> viewPendingExpenses();
                case "2" -> reviewExpense();
                case "3" -> reportsMenu();
                case "4" -> running = false;
                default -> System.out.println("Invalid option.");


            }


        }


    }

    private void reportsMenu() {
    }

    private void reviewExpense() {
        viewPendingExpenses();

        System.out.print("Enter the expense ID to review: ");
        int expenseId;
        try {
            expenseId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID — please enter a number.");
            return;
        }

        System.out.print("Approve or Deny? (a/d): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        String status;
        if (choice.equals("a")) {
            status = "approved";
        } else if (choice.equals("d")) {
            status = "denied";
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Comment: ");
        String comment = scanner.nextLine();

        Approvals result = approvalService.reviewExpense(expenseId, currentUser.getId(), status, comment);

        if (result != null) {
            System.out.println("Expense #" + expenseId + " has been " + result.getStatus() + ".");
        } else {
            System.out.println("Could not update — no approval found for that expense.");
        }
    }


    private void viewPendingExpenses() {
        List<Expenses> pending = es.getPendingExpenses();
        if (pending.isEmpty()) {
            System.out.println("No pending expenses.");
            return;
        }
        for (Expenses e : pending) {
            System.out.printf("ID: %d | Employee: %d | Amount: %s | %s | %s%n",
                    e.getId(), e.getUser_id(), e.getAmount(), e.getDescription(), e.getDate());
        }
    }


}
