from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.login, name='login'),
    path('logout/', views.logout, name='logout'),
    path('expenses/submit/', views.submit_expense, name='submit_expense'),
    path('expenses/<int:user_id>/', views.view_expenses, name='view_expenses'),
    path('expenses/<int:user_id>/history/', views.view_history, name='view_history'),
    path('expenses/<int:user_id>/pending/', views.get_pending_expenses, name='pending_expenses'),
    path('expenses/<int:user_id>/pending/<int:expense_id>/', views.edit_expense, name='edit_expense'),
    path('expenses/<int:user_id>/pending/<int:expense_id>/delete/', views.delete_expense, name='delete_expense'),
]