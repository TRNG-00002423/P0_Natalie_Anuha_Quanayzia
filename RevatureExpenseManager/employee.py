import requests
from rich.console import Console
from rich.table import Table

BASE_URL = "http://127.0.0.1:8000/ExpenseManager"
user_id = None
user_name=None
console = Console()


def employee_login():
    global user_id
    global username
    console.print("\n[bold]--- Employee Login ---[/bold]")
    username = input("Username: ")
    password = input("Password: ")

    res = requests.post(f"{BASE_URL}/login/", json={"username": username, "password": password})

    if res.status_code == 200:
        user_id = res.json()["user_id"]
        username=res.json()["username"]
        console.print(f"[green] {username} succesfully logged in.[/green]")
        employee_menu()
    else:
        console.print(f"[red]Error: {res.json().get('error')}[/red]")


def employee_menu():
    global user_id
    global username
    while True:
        console.print("\n[bold]--- Employee Portal ---[/bold]")
        print("1. Submit Expense")
        print("2. View My Expenses")
        print("3. View History")
        print("4. View Pending Expenses")
        print("5. Edit Pending Expense")
        print("6. Delete Pending Expense")
        print("7. Logout")

        options = {
            "1": submit_expense,
            "2": view_expenses,
            "3": view_history,
            "4": get_pending,
            "5": edit_expense,
            "6": delete_expense,
        }

        choice = input("Select an option: ").strip()
        if choice == "7":
            console.print(f"[yellow] {username} has been logged out.[/yellow]")
            user_id = None
            username=None
            break
        elif choice in options:
            options[choice]()
        else:
            console.print("[red]Invalid option, please try again.[/red]")


def submit_expense():
    console.print("\n[bold]--- Submit Expense ---[/bold]")

    valid_categories = ["MEALS", "TRAVEL", "LODGING", "OFFICE", "OTHER"]
    console.print(f"Categories: {', '.join(valid_categories)}")

    while True:
        category = input("Category: ").strip().upper()
        if category in valid_categories:
            break
        console.print(f"[red]Invalid category. Please choose from: {', '.join(valid_categories)}[/red]")

    while True:
        amount = input("Amount: ")
        try:
            amount = float(amount)
            if amount <= 0:
                console.print("[red]Amount must be greater than zero.[/red]")
            else:
                break
        except ValueError:
            console.print("[red]Invalid amount. Please enter a number.[/red]")

    description = input("Description: ")

    res = requests.post(f"{BASE_URL}/expenses/submit/", json={
        "user_id": user_id,
        "amount": amount,
        "description": description,
        "category": category
    })

    msg = res.json().get("message") or res.json().get("error")
    if res.status_code == 201:
        console.print(f"[green]{msg}[/green]")
    else:
        console.print(f"[red]{msg}[/red]")


def view_expenses():
    console.print("\n[bold]--- My Expenses ---[/bold]")
    res = requests.get(f"{BASE_URL}/expenses/{user_id}/")
    if res.status_code == 200:
        expenses = res.json()
        if not expenses:
            console.print("[yellow]No expenses found.[/yellow]")
            return

        table = Table(title="My Expenses")
        table.add_column("ID", style="cyan")
        table.add_column("Amount", style="green")
        table.add_column("Category")
        table.add_column("Description")
        table.add_column("Status")
        table.add_column("Submitted")
        table.add_column("Reviewed")

        for e in expenses:
            reviewed = e.get('reviewed_date') or "N/A"
            table.add_row(
                str(e['expense_id']), f"${e['amount']:.2f}", e['category'],
                e['description'], e['status'], e['submitted'], reviewed
                )

        console.print(table)
    else:
        console.print(f"[red]{res.json().get('error')}[/red]")


def view_history():
    console.print("\n[bold]--- Expense History ---[/bold]")
    res = requests.get(f"{BASE_URL}/expenses/{user_id}/history/")
    if res.status_code == 200:
        data = res.json()
        if not data["expenses"]:
            console.print("[yellow]No history found.[/yellow]")
            return

        table = Table(title="Expense History")
        table.add_column("ID", style="cyan")
        table.add_column("Amount", style="green")
        table.add_column("Category")
        table.add_column("Description")
        table.add_column("Status")
        table.add_column("Submitted")
        table.add_column("Reviewed")

        for e in data["expenses"]:
            reviewed = e.get('reviewed_date') or "N/A"
            table.add_row(
                str(e['expense_id']), f"${e['amount']:.2f}", e['category'],
                e['description'], e['status'], e['submitted'], reviewed
)

        console.print(table)
        console.print(f"\n[green]Total Approved: ${data['total_approved']}[/green]")
        console.print(f"[red]Total Denied:   ${data['total_denied']}[/red]")
    else:
        console.print(f"[red]{res.json().get('error')}[/red]")


def get_pending():
    console.print("\n[bold]--- Pending Expenses ---[/bold]")
    res = requests.get(f"{BASE_URL}/expenses/{user_id}/pending/")
    if res.status_code == 200:
        expenses = res.json()
        if not expenses:
            console.print("[yellow]No pending expenses.[/yellow]")
            return []

        table = Table(title="Pending Expenses")
        table.add_column("ID", style="cyan")
        table.add_column("Amount", style="green")
        table.add_column("Category")
        table.add_column("Description")
        table.add_column("Submitted")

        for e in expenses:
            table.add_row(
                str(e['expense_id']), f"${e['amount']:.2f}", e['category'],
                e['description'], e['submitted']
            )

        console.print(table)
        return expenses
    else:
        console.print(f"[red]{res.json().get('error')}[/red]")
        return []


def edit_expense():
    console.print("\n[bold]--- Edit Pending Expense ---[/bold]")
    expenses = get_pending()
    if not expenses:
        return

    expense_id = input("Enter expense ID to edit: ").strip()

    matched_expense = None
    for e in expenses:
        if str(e['expense_id']) == expense_id:
            matched_expense = e
            break

    if not matched_expense:
        console.print(f"[red]Expense with ID {expense_id} not found or does not belong to you.[/red]")
        return

    print("\nWhat would you like to edit?")
    print("1. Amount")
    print("2. Description")
    print("3. Category")
    print("4. All")

    edit_choice = input("Select an option: ").strip()

    amount = matched_expense['amount']
    description = matched_expense['description']
    category = matched_expense['category']

    if edit_choice in ("1", "4"):
        while True:
            amount = input("New amount: ")
            try:
                amount = float(amount)
                if amount <= 0:
                    console.print("[red]Amount must be greater than zero.[/red]")
                else:
                    break
            except ValueError:
                console.print("[red]Invalid amount. Please enter a number.[/red]")

    if edit_choice in ("2", "4"):
        description = input("New description: ")

    if edit_choice in ("3", "4"):
        valid_categories = ["MEALS", "TRAVEL", "LODGING", "OFFICE", "OTHER"]
        console.print(f"Categories: {', '.join(valid_categories)}")
        while True:
            category = input("New category: ").strip().upper()
            if category in valid_categories:
                break
            console.print(f"[red]Invalid category. Please choose from: {', '.join(valid_categories)}[/red]")

    res = requests.put(f"{BASE_URL}/expenses/{user_id}/pending/{expense_id}/", json={
        "amount": amount,
        "description": description,
        "category": category
    })
    msg = res.json().get("message") or res.json().get("error")
    if res.status_code == 200:
        console.print(f"[green]{msg}[/green]")
    else:
        console.print(f"[red]{msg}[/red]")


def delete_expense():
    console.print("\n[bold]--- Delete Pending Expense ---[/bold]")
    expenses = get_pending()
    if not expenses:
        return

    expense_id = input("Enter expense ID to delete: ").strip()
    res = requests.delete(f"{BASE_URL}/expenses/{user_id}/pending/{expense_id}/delete/")
    msg = res.json().get("message") or res.json().get("error")
    if res.status_code == 200:
        console.print(f"[green]{msg}[/green]")
    else:
        console.print(f"[red]{msg}[/red]")