from django.urls import path
from . import views


urlpatterns = [
    path('login/', views.login, name='login'),
    path('expenses/submit/', views.submit_expense, name='submit_expense'),
    path('expenses/', views.view_expenses, name='view_expenses'),
    path('expenses/history/', views.view_history, name='view_history'),
    path('expenses/pending/', views.get_pending_expenses, name='pending_expenses'),
    path('expenses/pending/<int:expense_id>/', views.edit_expense, name='edit_expense'),
    path('expenses/pending/<int:expense_id>/delete/', views.delete_expense, name='delete_expense'),
]