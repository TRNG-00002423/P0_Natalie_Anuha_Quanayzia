from django.urls import path
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('submit/', views.submit_expense, name='submit_expense'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
    path('menu/', views.menu, name='menu'),
    path('view_expenses/', views.view_expenses, name='view_expenses'),
    path('history/', views.view_history, name='view_history'),
    path('edit/', views.edit_pending, name='edit_pending'),
    path('delete/<int:expense_id>/', views.delete_expense, name='delete_expense'),
    path('edit/<int:expense_id>/', views.edit_expense, name='edit_expense'),
]