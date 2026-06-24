from ..models import Expense, Approval

def submit_expense(user, amount, description, date):

    expense = Expense.objects.create(
        user_id=user.id,
        amount=amount,
        description=description,
        date=date
    )

    Approval.objects.create(
        expense_id=expense.id,
        status="PENDING",
        reviewer=None,
        comment=None,
        review_date=None
    )

    return expense


def get_expense_status(user):
    expenses = Expense.objects.filter(user_id=user.id)

    result = []

    for expense in expenses:
        approval = Approval.objects.get(expense_id=expense.id)

        result.append({
            "expense_id": expense.id,
            "amount": expense.amount,
            "description": expense.description,
            "date": expense.date,
            "status": approval.status
        })

    return result