import os
import django
import bcrypt
import datetime

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'RevatureExpenseManager.settings')
django.setup()

from django.utils import timezone
from ExpenseManager.models import User, Expense, Approval


def hash_password(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')


def on(month, day):
    return timezone.make_aware(datetime.datetime(2026, month, day, 12, 0))


# Clear existing data
Approval.objects.all().delete()
Expense.objects.all().delete()
User.objects.all().delete()

# Create users
alice = User.objects.create(username='alice', password=hash_password('password123'), role='employee')
bob = User.objects.create(username='bob', password=hash_password('password123'), role='employee')
carol = User.objects.create(username='carol', password=hash_password('password123'), role='employee')
dave = User.objects.create(username='dave', password=hash_password('password123'), role='employee')
manager = User.objects.create(username='manager', password=hash_password('password123'), role='manager')

# Create expenses
e1 = Expense.objects.create(user_id=alice, amount=49.99, description='Office supplies', category='OFFICE', created_date=on(6, 1))
e2 = Expense.objects.create(user_id=alice, amount=120.00, description='Team lunch', category='MEALS', created_date=on(6, 3))
e3 = Expense.objects.create(user_id=alice, amount=300.00, description='Travel reimbursement', category='TRAVEL', created_date=on(6, 5))
e4 = Expense.objects.create(user_id=bob, amount=85.50, description='Software subscription', category='OTHER', created_date=on(6, 2))
e5 = Expense.objects.create(user_id=bob, amount=200.00, description='Conference ticket', category='TRAVEL', created_date=on(6, 7))
e6 = Expense.objects.create(user_id=alice, amount=89.50, description='Hotel night', category='LODGING', created_date=on(6, 9))
e7 = Expense.objects.create(user_id=carol, amount=250.00, description='Standing desk', category='OFFICE', created_date=on(6, 4))
e8 = Expense.objects.create(user_id=carol, amount=60.00, description='Team breakfast', category='MEALS', created_date=on(6, 10))
e9 = Expense.objects.create(user_id=carol, amount=180.00, description='Airport hotel', category='LODGING', created_date=on(6, 11))
e10 = Expense.objects.create(user_id=dave, amount=32.40, description='Notebooks and pens', category='OFFICE', created_date=on(6, 6))
e11 = Expense.objects.create(user_id=dave, amount=410.00, description='Train tickets', category='TRAVEL', created_date=on(6, 13))
e12 = Expense.objects.create(user_id=dave, amount=27.00, description='Coffee run', category='MEALS', created_date=on(6, 15))

# Create approvals
Approval.objects.create(expense_id=e1, status='approved', reviewer=manager, comment='Looks good.', approved_date=timezone.now())
Approval.objects.create(expense_id=e2, status='denied', reviewer=manager, comment='Over budget.', approved_date=timezone.now())
Approval.objects.create(expense_id=e3, status='pending')
Approval.objects.create(expense_id=e4, status='approved', reviewer=manager, comment='Approved.', approved_date=timezone.now())
Approval.objects.create(expense_id=e5, status='pending')
Approval.objects.create(expense_id=e6, status='pending')
Approval.objects.create(expense_id=e7, status='approved', reviewer=manager, comment='Approved.', approved_date=timezone.now())
Approval.objects.create(expense_id=e8, status='pending')
Approval.objects.create(expense_id=e9, status='pending')
Approval.objects.create(expense_id=e10, status='approved', reviewer=manager, comment='Approved.', approved_date=timezone.now())
Approval.objects.create(expense_id=e11, status='pending')
Approval.objects.create(expense_id=e12, status='denied', reviewer=manager, comment='Over budget.', approved_date=timezone.now())

print("Database seeded successfully.")
print(f"Users: {User.objects.count()}")
print(f"Expenses: {Expense.objects.count()}")
print(f"Approvals: {Approval.objects.count()}")