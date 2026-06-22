from django.test import TestCase
from django.utils import timezone
from .models import User,Expense, Approval 


class ModelTests(TestCase):


    def test_models_creation(self):
        user=User.objects.create(
            username="testuser",
                password="pass",
                role="Employee"

        )

        expense = Expense.objects.create(
            user_id=user,
            amount=50,
            description="Lunch",
            created_date=timezone.now()
        )

        approval = Approval.objects.create(
            expense_id=expense,
            status="pending",
            reviewer=user,
            comment="ok",
            approved_date=timezone.now()
        )


        self.assertEqual(user.username, "testuser")
        self.assertEqual(expense.user_id, user)
        self.assertEqual(approval.expense_id, expense)



   





