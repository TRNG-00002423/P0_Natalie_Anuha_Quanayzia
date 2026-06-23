from django.db import models
import datetime
from django.utils import timezone



class User(models.Model):
    username = models.CharField(max_length=25, unique=True)
    password = models.CharField(max_length=15)
    # role_choices=(
    #     (1, 'Manager'),
    #     (2, 'Employee'),
    # )
    # role = models.IntegerField(choices=role_choices, default=2)
    role = models.CharField(max_length=9)

    def __str__(self):
        return self.username



class Expense(models.Model):
    user_id = models.ForeignKey(User, on_delete=models.CASCADE)
    amount = models.FloatField(default=0)
    description = models.CharField(max_length=255)
    created_date = models.DateTimeField('Date Of Expense')

    def __str__(self):
        return self.description
    
    
    def was_published_recently(self):
        return self.created_date >= timezone.now() - datetime.timedelta(days=1)



class Approval(models.Model):
    expense_id = models.ForeignKey(Expense, on_delete=models.CASCADE)
    status = models.CharField(max_length=20)
    # status_choices=(
    #     (1, 'Pending'),
    #     (2, 'Approved'),
    #     (3, 'Denied'),
    # )
    # status = models.IntegerField(choices=status_choices, default=1)
    # Null if not reviewed yet

    reviewer = models.ForeignKey(User, on_delete=models.CASCADE, null=True,blank=True)
    comment = models.TextField(blank=True)
    approved_date = models.DateTimeField('Date Of Review',null=True,blank=True)

    def __str__(self):
        return f"Approval {self.id} - {self.status}"