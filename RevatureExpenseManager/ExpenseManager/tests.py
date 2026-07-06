import json
import bcrypt

from django.test import TestCase
from django.utils import timezone
from .models import User, Expense, Approval


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


        # creation
        self.assertEqual(user.username, "testuser")
        self.assertEqual(expense.user_id, user)
        self.assertEqual(approval.expense_id, expense)

        # retrieval tests
        retrieved_user = User.objects.get(username="testuser")
        retrieved_expense = Expense.objects.get(id=expense.id)
        retrieved_approval = Approval.objects.get(id=approval.id)

        self.assertEqual(retrieved_user.username, "testuser")
        self.assertEqual(retrieved_expense.description, "Lunch")
        self.assertEqual(retrieved_approval.status, "pending")


class LoginApiTests(TestCase):

    LOGIN_URL = "/ExpenseManager/login/"

    def setUp(self):
        hashed = bcrypt.hashpw(b"pass", bcrypt.gensalt()).decode()
        User.objects.create(username="employee1", password=hashed, role="employee")

    def test_login_valid_returns_200(self):
        response = self.client.post(
            self.LOGIN_URL,
            data=json.dumps({"username": "employee1", "password": "pass"}),
            content_type="application/json",
        )

        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json()["username"], "employee1")

    def test_login_wrong_password_returns_401(self):
        response = self.client.post(
            self.LOGIN_URL,
            data=json.dumps({"username": "employee1", "password": "wrongpass"}),
            content_type="application/json",
        )

        self.assertEqual(response.status_code, 401)

    def test_login_unknown_user_returns_401(self):
        response = self.client.post(
            self.LOGIN_URL,
            data=json.dumps({"username": "nobody", "password": "pass"}),
            content_type="application/json",
        )

        self.assertEqual(response.status_code, 401)
