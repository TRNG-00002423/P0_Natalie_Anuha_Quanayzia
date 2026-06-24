from django.utils import timezone
from ..models import User, Expense, Approval


def get_user_by_id(user_id):
    return User.objects.get(id=user_id)


def submit_expense(employee, amount, description):
    expense = Expense.objects.create(
        user_id=employee,
        amount=amount,
        description=description,
        created_date=timezone.now(),
    )
    Approval.objects.create(expense_id=expense, status='pending')
    return expense


def get_expenses_with_status(employee):
    expenses = Expense.objects.filter(user_id=employee)
    rows = []
    for e in expenses:
        approval = Approval.objects.filter(expense_id=e).first()
        status = approval.status if approval else 'pending'
        rows.append({'expense': e, 'status': status})
    return rows


def get_expense_history(employee):
    expenses = Expense.objects.filter(user_id=employee)
    rows = []
    total_approved = 0
    total_denied = 0
    for e in expenses:
        approval = Approval.objects.filter(expense_id=e).first()
        if approval and approval.status == 'approved':
            rows.append({'expense': e, 'status': 'approved'})
            total_approved += e.amount
        elif approval and approval.status == 'denied':
            rows.append({'expense': e, 'status': 'denied'})
            total_denied += e.amount
    return rows, total_approved, total_denied


def get_pending_expenses(employee):
    all_expenses = Expense.objects.filter(user_id=employee)
    return [
        e for e in all_expenses
        if (a := Approval.objects.filter(expense_id=e).first()) and a.status == 'pending'
    ]


def delete_pending_expense(employee, expense_id):
    expense = Expense.objects.filter(id=expense_id, user_id=employee).first()
    if expense:
        approval = Approval.objects.filter(expense_id=expense).first()
        if approval and approval.status == 'pending':
            expense.delete()
            return True
    return False


def get_pending_expense(employee, expense_id):
    expense = Expense.objects.filter(id=expense_id, user_id=employee).first()
    if not expense:
        return None, None
    approval = Approval.objects.filter(expense_id=expense).first()
    if not approval or approval.status != 'pending':
        return None, None
    return expense, approval


def update_expense(expense, amount, description):
    expense.amount = amount
    expense.description = description
    expense.save()
    return expense


def authenticate_user(username, password):
    return User.objects.filter(username=username, password=password).first()