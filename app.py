import sqlite3
from datetime import date
from login import login


def submit_expense(user):
    print("\n Submit a New Expense")
    amount = input("Enter the amount: ")
    description = input("Enter a description: ")

   #check to see if amount is a positive number
    try:
        amount = float(amount)
    except ValueError:
        print("Invalid amount. Please enter a number.")
        return

    if amount <= 0:
        print("Amount must be greater than zero.")
        return

    today = str(date.today())

    conn = sqlite3.connect('expense_manager.db')
    cursor = conn.cursor()

    #insert expense
    cursor.execute(
        "INSERT INTO expenses (user_id, amount, description, date) VALUES (?, ?, ?, ?)",
        (user['id'], amount, description, today)
    )

 
    expense_id = cursor.lastrowid

    #creates the matching pending approval row
    cursor.execute(
        "INSERT INTO approvals (expense_id, status) VALUES (?, ?)",
        (expense_id, 'pending')
    )

    conn.commit()
    conn.close()
    print("Expense submitted successfully and is pending review.")


def view_expenses(user):
    print("\n My Expenses")

    conn = sqlite3.connect('expense_manager.db')
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    #get all of this user's 
    cursor.execute(
        """
        SELECT e.id, e.amount, e.description, e.date, a.status
        FROM expenses e
        JOIN approvals a ON a.expense_id = e.id
        WHERE e.user_id = ?
        ORDER BY e.id DESC
        """,
        (user['id'],)
    )
    expenses = cursor.fetchall()
    conn.close()

    if not expenses:
        print("You have not submitted any expenses yet.")
        return

    for e in expenses:
        print(f"  #{e['id']}  ${e['amount']:.2f}  {e['date']}  [{e['status']}]  {e['description']}")


def edit_or_delete_expense(user):
    print("\n Edit or Delete a Pending Expense")

    conn = sqlite3.connect('expense_manager.db')
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    #only pending expenses can be edited or deleted
    cursor.execute(
        """
        SELECT e.id, e.amount, e.description, e.date
        FROM expenses e
        JOIN approvals a ON a.expense_id = e.id
        WHERE e.user_id = ? AND a.status = 'pending'
        ORDER BY e.id DESC
        """,
        (user['id'],)
    )
    pending = cursor.fetchall()

    if not pending:
        print("You have no pending expenses to edit or delete.")
        conn.close()
        return

    for p in pending:
        print(f"  #{p['id']}  ${p['amount']:.2f}  {p['date']}  {p['description']}")

    selected_id = input("Enter the ID of the expense to change: ")

    
    selected = None
    for p in pending:
        if str(p['id']) == selected_id:
            selected = p
            break

    if selected is None:
        print("That is not one of your pending expenses.")
        conn.close()
        return

    action = input("Type 'E' to edit or 'D' to delete: ").strip().lower()

    if action == 'd':
       
        cursor.execute("DELETE FROM approvals WHERE expense_id = ?", (selected['id'],))
        cursor.execute("DELETE FROM expenses WHERE id = ?", (selected['id'],))
        conn.commit()
        print("Expense deleted.")

    elif action == 'e':
        new_amount = input("Enter the new amount: ")
        try:
            new_amount = float(new_amount)
        except ValueError:
            print("Invalid amount. Please enter a number.")
            conn.close()
            return

        if new_amount <= 0:
            print("Amount must be greater than zero.")
            conn.close()
            return

        new_description = input("Enter the new description: ")
        cursor.execute(
            "UPDATE expenses SET amount = ?, description = ? WHERE id = ?",
            (new_amount, new_description, selected['id'])
        )
        conn.commit()
        print("Expense updated.")

    else:
        print("Invalid option.")

    conn.close()


def view_history(user):
    print("\n Expense History (Approved / Denied)")

    conn = sqlite3.connect('expense_manager.db')
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    #shows expenses that have already been reviewed
    cursor.execute(
        """
        SELECT e.id, e.amount, e.description, e.date,
               a.status, a.comment, a.review_date
        FROM expenses e
        JOIN approvals a ON a.expense_id = e.id
        WHERE e.user_id = ? AND a.status IN ('approved', 'denied')
        ORDER BY a.review_date DESC
        """,
        (user['id'],)
    )
    history = cursor.fetchall()
    conn.close()

    if not history:
        print("You have no approved or denied expenses yet.")
        return

    for h in history:
        comment = h['comment'] if h['comment'] else "(no comment)"
        print(f"  #{h['id']}  ${h['amount']:.2f}  {h['date']}  [{h['status']}]  {h['description']}")
        print(f"       reviewed {h['review_date']} - {comment}")


def employee_menu(user):
    while True:
        print("\n Employee Menu")
        print("Please select an option:")
        print("1. Submit Expense")
        print("2. View My Expenses")
        print("3. Edit/Delete Pending Expense")
        print("4. View History")
        print("5. Logout")

        choice = input("Enter your choice: ")
        if choice == '1':
            submit_expense(user)
        elif choice == '2':
            view_expenses(user)
        elif choice == '3':
            edit_or_delete_expense(user)

        elif choice == '4':
            view_history(user)
        elif choice == '5':
            print("Logging out. Goodbye!")
            break
        else:
            print("Invalid choice. Please try again.")


def main():
    print("\n Welcome to the Revature Expense Manager")
    username = input("Username: ")
    password = input("Password: ")

    user = login(username, password)
    if user:
        print(f"Welcome, {user['username']}! Your role is {user['role']}.")
        employee_menu(user)
    else:
        print("Login failed. Invalid username or password.")


main()
