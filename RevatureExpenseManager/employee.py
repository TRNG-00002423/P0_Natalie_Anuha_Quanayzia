import requests


BASE_URL = "http://127.0.0.1:8000/ExpenseManager"
user_id = None



def employee_login():
    global user_id
    print("\n--- Employee Login ---")
    username = input("Username: ")
    password = input("Password: ")

    requests.post(f"{BASE_URL}/login/", json={"username": username, "password": password})

    if res.status_code == 200:
        user_id = res.json()["user_id"]
        print("Login successful.")
        employee_menu()
    else:
        print(f"Error: {res.json().get('error')}")

def employee_menu():
    global user_id
    while True:
        print("\n--- Employee Portal ---")
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
            print(f"User {user_id} has been logged out")
            user_id = None
            break
        elif choice in options:
            options[choice]()
        else:
            print("Invalid option, please try again.")




def submit_expense():

    print("\n--- Submit Expense ---")
    print("Categories: MEALS, TRAVEL, LODGING, OFFICE, OTHER")

    valid_categories = ["MEALS", "TRAVEL", "LODGING", "OFFICE", "OTHER"]

    while True:
        category = input("Category: ").strip().upper()
        if category in valid_categories:
            break
        print(f"Invalid category. Please choose from: {', '.join(valid_categories)}")

    while True:
        amount = input("Amount: ")
        try:
            amount = float(amount)
            if amount <= 0:
                print("Amount must be greater than zero.")
            else:
                break
        except ValueError:
            print("Invalid amount. Please enter a number.")

    description = input("Description: ")

    res = requests.post(f"{BASE_URL}/expenses/submit/", json={
        "user_id": user_id,
        "amount": amount,
        "description": description,
        "category": category
    })
    print(res.json().get("message") or res.json().get("error"))


def view_expenses():
    
    print("\n--- My Expenses ---")
    
    res= requests.get(f"{BASE_URL}/expenses/{user_id}/")
    
    if res.status_code==200:
        print("No expenses found")
        return
    print(f"\n{'ID':<5} {'Amount':<10} {'Category':<10} {'Description':<30} {'Status':<10} {'Submitted':<30} {'Reviewed':<30}")
    print("-" * 110)
    for e in expenses:
        reviewed= e.get('reviewed_date', 'N/A') or 'N/A'
        print(f"{e['expense_id']:<5} ${e['amount']:<9} {e['category']:<10} {e['description']:<30} {e['status']:<10} {e['submitted']:<30} {reviewed:<30}")
    else:
        print(res.json().get("error"))

def view_history():
    print("\n--- Expense History ---")
    res = requests.get(f"{BASE_URL}/expenses/{user_id}/history/")
    if res.status_code == 200:
        data = res.json()
        if not data["expenses"]:
            print("No history found.")
            return
        print(f"\n{'ID':<5} {'Amount':<10} {'Category':<10} {'Description':<30} {'Status':<10} {'Submitted':<30} {'Reviewed':<30}")
        print("-" * 110)
        for e in data["expenses"]:
            reviewed = e.get('reviewed_date') or 'N/A'
            print(f"{e['expense_id']:<5} ${e['amount']:<9} {e['category']:<10} {e['description']:<30} {e['status']:<10} {e['submitted']:<30} {reviewed:<30}")
        print(f"\nTotal Approved: ${data['total_approved']}")
        print(f"Total Denied:   ${data['total_denied']}")
    else:
        print(res.json().get("error"))

   

def get_pending():
    print("\n--- Pending Expenses ---")
    res = requests.get(f"{BASE_URL}/expenses/{user_id}/pending/")
    if res.status_code == 200:
        expenses = res.json()
        if not expenses:
            print("No pending expenses.")
            return []
        print(f"\n{'ID':<5} {'Amount':<10} {'Category':<10} {'Description':<30} {'Submitted':<30}")
        print("-" * 90)
        for e in expenses:
            print(f"{e['expense_id']:<5} ${e['amount']:<9} {e['category']:<10} {e['description']:<30} {e['submitted']:<30}")
        return expenses
    else:
        print(res.json().get("error"))
        return []


def edit_expense():
    print("\n--- Edit Pending Expense ---")
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
        print(f"Expense with ID {expense_id} not found ")
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
                    print("Amount must be greater than zero.")
                else:
                    break
            except ValueError:
                print("Invalid amount. Please enter a number.")

    if edit_choice in ("2", "4"):
        description = input("New description: ")

    if edit_choice in ("3", "4"):
        valid_categories = ["MEALS", "TRAVEL", "LODGING", "OFFICE", "OTHER"]
        print("Categories: MEALS, TRAVEL, LODGING, OFFICE, OTHER")
        while True:
            category = input("New category: ").strip().upper()
            if category in valid_categories:
                break
            print(f"Invalid category. Please choose from: {', '.join(valid_categories)}")

    res = requests.put(f"{BASE_URL}/expenses/{user_id}/pending/{expense_id}/", json={
        "amount": amount,
        "description": description,
        "category": category
    })
    print(res.json().get("message") or res.json().get("error"))

def delete_expense():
    print("\n--- Delete Pending Expense ---")
    expenses = get_pending()
    if not expenses:
        return

    expense_id = input("Enter expense ID to delete: ").strip()
    res = requests.delete(f"{BASE_URL}/expenses/{user_id}/pending/{expense_id}/delete/")
    print(res.json().get("message") or res.json().get("error"))

           
