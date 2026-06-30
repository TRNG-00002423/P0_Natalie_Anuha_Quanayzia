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
import java.math.BigDecimal;

public class ManagerMenu {



    private AuthService as=new AuthService();
    private ExpenseService es= new ExpenseService();
    private ApprovalService approvalService= new ApprovalService();
    private Users currentUser;
    private Scanner scanner = new Scanner(System.in);

    public void start() {
      
        boolean loggedIn = false;

        for (int i = 0; i < 3; i++) {
            if (login()) {
                loggedIn = true;
                break;
            }
            if (i < 1) {
                System.out.println("Invalid credentials. Try again.");
            }
        }

        if (loggedIn) {
            displayMenu();
        } else {
            System.out.println("Too many failed attempts. Exiting.");
        }
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
        System.out.println("\n=== Reports ===");
        System.out.println("1. All Employees");
        System.out.println("2. By Employee");
        System.out.println("3. By Date");
        System.out.println("4. By Category");
        System.out.println("5. Back");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        List<Expenses> results;

        if (choice.equals("1")) {
            results = es.getAllExpenses();
        } else if (choice.equals("2")) {
            System.out.print("Enter employee ID: ");
            int employeeId;
            try {
                employeeId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID — please enter a number.");
                return;
            }
            results = es.getExpensesByEmployee(employeeId);
        } else if (choice.equals("3")) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            results = es.getExpensesByDate(date);
        } else if (choice.equals("4")) {
            System.out.print("Enter category (MEALS/TRAVEL/OFFICE/OTHER): ");
            String category = scanner.nextLine();
            results = es.getExpensesByCategory(category);
        } else if (choice.equals("5")) {
            return;
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No expenses found for that report.");
            return;
        }

        System.out.printf("%-6s %-10s %-10s %-20s %-10s%n", "ID", "Employee", "Amount", "Description", "Date");
        System.out.println("----------------------------------------------------------");
        BigDecimal total = BigDecimal.ZERO;
        for (Expenses e : results) {
            System.out.printf("%-6d %-10d %-10s %-20s %-10s%n",
                    e.getId(), e.getUser_id(), e.getAmount(), e.getDescription(), e.getDate());
            total = total.add(e.getAmount());
        }
        System.out.println("----------------------------------------------------------");
        System.out.printf("Total: %s%n", total);
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
        System.out.printf("%-6s %-10s %-10s %-20s %-10s%n", "ID", "Employee", "Amount", "Description", "Date");
        System.out.println("----------------------------------------------------------");
        for (Expenses e : pending) {
            System.out.printf("%-6d %-10d %-10s %-20s %-10s%n",
                    e.getId(), e.getUser_id(), e.getAmount(), e.getDescription(), e.getDate());
        }
    }


}
