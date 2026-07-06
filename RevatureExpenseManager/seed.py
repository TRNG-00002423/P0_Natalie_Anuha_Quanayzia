import os
import django
import bcrypt

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'RevatureExpenseManager.settings')
django.setup()

from django.utils import timezone
from ExpenseManager.models import User, Expense, Approval


def hash_password(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')


# Clear existing data
Approval.objects.all().delete()
Expense.objects.all().delete()
User.objects.all().delete()

# Create users
alice = User.objects.create(username='alice', password=hash_password('password123'), role='employee')
bob = User.objects.create(username='bob', password=hash_password('password123'), role='employee')
manager = User.objects.create(username='manager', password=hash_password('password123'), role='manager')

# Create expenses
e1 = Expense.objects.create(user_id=alice, amount=49.99, description='Office supplies', category='OFFICE', created_date=timezone.now())
e2 = Expense.objects.create(user_id=alice, amount=120.00, description='Team lunch', category='MEALS', created_date=timezone.now())
e3 = Expense.objects.create(user_id=alice, amount=300.00, description='Travel reimbursement', category='TRAVEL', created_date=timezone.now())
e4 = Expense.objects.create(user_id=bob, amount=85.50, description='Software subscription', category='OTHER', created_date=timezone.now())
e5 = Expense.objects.create(user_id=bob, amount=200.00, description='Conference ticket', category='TRAVEL', created_date=timezone.now())

# Create approvals
Approval.objects.create(expense_id=e1, status='approved', reviewer=manager, comment='Looks good.', approved_date=timezone.now())
Approval.objects.create(expense_id=e2, status='denied', reviewer=manager, comment='Over budget.', approved_date=timezone.now())
Approval.objects.create(expense_id=e3, status='pending')
Approval.objects.create(expense_id=e4, status='approved', reviewer=manager, comment='Approved.', approved_date=timezone.now())
Approval.objects.create(expense_id=e5, status='pending')

print("Database seeded successfully.")
print(f"Users: {User.objects.count()}")
print(f"Expenses: {Expense.objects.count()}")
print(f"Approvals: {Approval.objects.count()}")