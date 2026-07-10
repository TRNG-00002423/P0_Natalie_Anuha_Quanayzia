package com.project0.Controller;

import com.project0.exceptions.AuthenticationException;
import com.project0.exceptions.UserNotFoundException;
import com.project0.model.Expenses;
import com.project0.model.Users;
import com.project0.services.AuthService;
import com.project0.services.ExpenseService;
import com.project0.model.Approvals;
import com.project0.services.ApprovalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ManagerMenu {

    private static final String GREEN = (char) 27 + "[32m";
    private static final String RED = (char) 27 + "[31m";
    private static final String CYAN = (char) 27 + "[36m";
    private static final String BOLD = (char) 27 + "[1m";
    private static final String RESET = (char) 27 + "[0m";

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
            System.out.println("\n" + CYAN + "=== Manager Menu ===" + RESET);
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
        System.out.println("\n" + CYAN + "=== Reports ===" + RESET);
        System.out.println("1. All Employees");
        System.out.println("2. By Employee");
        System.out.println("3. By Date");
        System.out.println("4. By Category");
        System.out.println("5. By Status");
        System.out.println("6. Back");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        List<Expenses> results;

        if (choice.equals("1")) {
            results = es.getAllExpenses();
        } else if (choice.equals("2")) {
            List<Users> employees = as.getAllEmployees();
            String header = center("Employees", 36);
            int indent = header.length() - "Employees".length();
            System.out.println(CYAN + BOLD + header + RESET);
            for (Users u : employees) {
                System.out.println(" ".repeat(indent) + u.getId() + " - " + u.getUsername());
            }
            System.out.print("Enter employee ID or name (0 to go back): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                return;
            }
            int employeeId = -1;
            for (Users u : employees) {
                if (u.getUsername().equalsIgnoreCase(input) || String.valueOf(u.getId()).equals(input)) {
                    employeeId = u.getId();
                    break;
                }
            }
            if (employeeId == -1) {
                System.out.println("No employee found matching '" + input + "'.");
                return;
            }
            results = es.getExpensesByEmployee(employeeId);
        } else if (choice.equals("3")) {
            System.out.print("Enter start date (YYYY-MM-DD, blank to go back): ");
            String start = scanner.nextLine().trim();
            if (start.isBlank()) {
                return;
            }
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String end = scanner.nextLine().trim();

            String endExclusive;
            try {
                LocalDate.parse(start);                                      // reject a bad start date
                endExclusive = LocalDate.parse(end).plusDays(1).toString();  // reject a bad end date + include the whole day
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please use the format YYYY-MM-DD.");
                return;
            }
            results = es.getExpensesByDateRange(start, endExclusive);
        } else if (choice.equals("4")) {
            System.out.print("Enter category (MEALS/TRAVEL/LODGING/OFFICE/OTHER, blank to go back): ");
            String category = scanner.nextLine().trim().toUpperCase();
            if (category.isBlank()) {
                return;
            }
            results = es.getExpensesByCategory(category);
        } else if (choice.equals("5")) {
            System.out.print("Enter status (approved/denied, blank to go back): ");
            String status = scanner.nextLine().trim().toLowerCase();
            if (status.isBlank()) {
                return;
            }
            if (!status.equals("approved") && !status.equals("denied")) {
                System.out.println("Please enter 'approved' or 'denied'.");
                return;
            }
            results = es.getExpensesByStatus(status);
        } else if (choice.equals("6")) {
            return;
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (results.isEmpty()) {
            System.out.println("No expenses found for that report.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Expenses e : results) {
            total = total.add(e.getAmount());
        }
        printExpenseTable(results, total, true);
    }

    private void reviewExpense() {
        viewPendingExpenses();

        while (true) {
            System.out.print("Enter the expense ID to review (0 to go back): ");
            int expenseId;
            try {
                expenseId = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID — please enter a number.");
                continue;
            }

            if (expenseId == 0) {
                return;
            }

            if (es.getExpenseById(expenseId) == null) {
                System.out.println("No expense found with ID " + expenseId + ".");
                continue;
            }

            System.out.print("Approve or Deny? (a/d, blank to go back): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.isBlank()) {
                continue;
            }

            String status;
            if (choice.equals("a")) {
                status = "approved";
            } else if (choice.equals("d")) {
                status = "denied";
            } else {
                System.out.println("Invalid choice.");
                continue;
            }

            System.out.print("Comment: ");
            String comment = scanner.nextLine();

            Approvals result = approvalService.reviewExpense(expenseId, currentUser.getId(), status, comment);

            if (result != null) {
                String color = RED;
                if (result.getStatus().equals("approved")) {
                    color = GREEN;
                }
                System.out.println(color + "Expense #" + expenseId + " has been " + result.getStatus() + "." + RESET);
            } else {
                System.out.println(RED + "Could not update — no approval found for that expense." + RESET);
            }
            return;
        }
    }


    private void viewPendingExpenses() {
        List<Expenses> pending = es.getPendingExpenses();
        if (pending.isEmpty()) {
            System.out.println("No pending expenses.");
            return;
        }
        printExpenseTable(pending, null, false);
    }

    private String money(BigDecimal amount) {
        return String.format("$%.2f", amount);
    }

    // ----- Bordered table rendering -----
    private static final int W_ID = 5, W_EMP = 10, W_AMT = 9, W_CAT = 8, W_STAT = 9, W_DESC = 22, W_DESC_NARROW = 16, W_DATE = 10, W_REV = 9, W_RDATE = 10;

    private String tableBorder(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    private String tableRow(String[] vals, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < vals.length; i++) {
            sb.append(String.format(" %-" + widths[i] + "s |", vals[i]));
        }
        return sb.toString();
    }

    private String fit(String s, int width) {
        if (s == null) return "";
        if (s.length() > width) return s.substring(0, width - 3) + "...";
        return s;
    }

    private String center(String s, int width) {
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        return " ".repeat(left) + s;
    }

    private void printExpenseTable(List<Expenses> expenses, BigDecimal total, boolean showReview) {
        Map<Integer, String> names = new HashMap<>();
        for (Users u : as.getAllUsers()) {
            names.put(u.getId(), u.getUsername());
        }

        int descW = showReview ? W_DESC_NARROW : W_DESC;
        int[] widths;
        String[] headers;
        if (showReview) {
            widths = new int[]{W_EMP, W_ID, W_AMT, W_CAT, W_STAT, descW, W_DATE, W_REV, W_RDATE};
            headers = new String[]{"Employee", "ID", "Amount", "Category", "Status", "Description", "Submitted", "Reviewer", "Reviewed"};
        } else {
            widths = new int[]{W_EMP, W_ID, W_AMT, W_CAT, W_STAT, descW, W_DATE};
            headers = new String[]{"Employee", "ID", "Amount", "Category", "Status", "Description", "Submitted"};
        }

        String border = tableBorder(widths);
        System.out.println(border);
        System.out.println(CYAN + tableRow(headers, widths) + RESET);
        System.out.println(border);
        int approved = 0, denied = 0, pending = 0;
        BigDecimal deniedTotal = BigDecimal.ZERO;
        for (Expenses e : expenses) {
            String submitted = e.getDate() == null ? "" : e.getDate();
            if (submitted.length() > W_DATE) {
                submitted = submitted.substring(0, W_DATE);
            }
            Approvals approval = approvalService.getApproval(e.getId());
            String status = approval != null ? approval.getStatus() : "none";
            if (status.equals("approved")) {
                approved++;
            } else if (status.equals("denied")) {
                denied++;
                deniedTotal = deniedTotal.add(e.getAmount());
            } else if (status.equals("pending")) {
                pending++;
            }
            String employee = names.getOrDefault(e.getUser_id(), String.valueOf(e.getUser_id()));

            String[] vals;
            if (showReview) {
                String reviewer = "-";
                if (approval != null && approval.getReviewer() > 0) {
                    reviewer = names.getOrDefault(approval.getReviewer(), String.valueOf(approval.getReviewer()));
                }
                String reviewed = "-";
                if (approval != null && approval.getReview_date() != null) {
                    reviewed = approval.getReview_date();
                    if (reviewed.length() > W_RDATE) {
                        reviewed = reviewed.substring(0, W_RDATE);
                    }
                }
                vals = new String[]{
                        fit(employee, W_EMP), String.valueOf(e.getId()), money(e.getAmount()),
                        e.getCategory(), status, fit(e.getDescription(), descW), submitted,
                        fit(reviewer, W_REV), reviewed
                };
            } else {
                vals = new String[]{
                        fit(employee, W_EMP), String.valueOf(e.getId()), money(e.getAmount()),
                        e.getCategory(), status, fit(e.getDescription(), descW), submitted
                };
            }
            System.out.println(tableRow(vals, widths));
        }
        System.out.println(border);
        if (total != null) {
            BigDecimal trueTotal = total.subtract(deniedTotal);
            System.out.println(
                    BOLD + "Total: " + money(trueTotal) + RESET
                    + "    Pending: " + pending
                    + "    " + GREEN + "Approved: " + approved + RESET
                    + "    " + RED + "Denied: " + denied + RESET);
        }
    }

}
