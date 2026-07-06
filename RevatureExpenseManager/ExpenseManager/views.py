import logging

from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .service import ExpenseService, AuthenticationService
from .exceptions.AuthenticationError import AuthenticationError

logger = logging.getLogger('expensemanager')


@api_view(['POST'])
def login(request):
    username = request.data.get('username')
    try:
        user = AuthenticationService.login(
            username,
            request.data.get('password')
        )
    except AuthenticationError as e:
        logger.warning(f"Login failed: username '{username}'")
        return Response({'error': str(e)}, status=status.HTTP_401_UNAUTHORIZED)
    logger.info(f"Login success: {user.username} (id {user.id})")
    return Response({'user_id': user.id, "username": user.username})


@api_view(['POST'])
def logout(request):
    user_id = request.data.get('user_id')
    logger.info(f"Logout: user {user_id}")
    return Response({'message': 'Logged out.'})


@api_view(['POST'])
def submit_expense(request):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    amount = request.data.get('amount')
    description = request.data.get('description')
    category = request.data.get('category', 'OTHER')

    try:
        amount = float(amount)
    except (ValueError, TypeError):
        return Response({'error': 'Invalid amount.'}, status=status.HTTP_400_BAD_REQUEST)
    if amount <= 0:
        return Response({'error': 'Amount must be greater than zero.'}, status=status.HTTP_400_BAD_REQUEST)

    employee = ExpenseService.get_user_by_id(user_id)
    ExpenseService.submit_expense(employee, amount, description, category)
    logger.info(f"Expense submitted by user {user_id}: {amount} {category}")
    return Response({'message': 'Expense submitted and pending review.'}, status=status.HTTP_201_CREATED)


@api_view(['GET'])
def view_expenses(request, user_id):
    employee = ExpenseService.get_user_by_id(user_id)
    rows = ExpenseService.get_expenses_with_status(employee)
    data = []
    for r in rows:
        entry = {
            'expense_id': r['expense'].id,
            'amount': r['expense'].amount,
            'category': r['expense'].category,
            'description': r['expense'].description,
            'status': r['status'],
            'submitted': r['submitted'],
        }
        if r['status'] != 'pending':
            entry['reviewed_date'] = r['reviewed_date']
        data.append(entry)
    return Response(data)


@api_view(['GET'])
def view_history(request, user_id):
    employee = ExpenseService.get_user_by_id(user_id)
    rows, total_approved, total_denied = ExpenseService.get_expense_history(employee)
    data = [{'expense_id': r['expense'].id, 'amount': r['expense'].amount, 'category': r['expense'].category, 'description': r['expense'].description, 'status': r['status'], 'submitted': r['submitted'], 'reviewed_date': r['reviewed_date']} for r in rows]
    return Response({'expenses': data, 'total_approved': total_approved, 'total_denied': total_denied})


@api_view(['GET'])
def get_pending_expenses(request, user_id):
    employee = ExpenseService.get_user_by_id(user_id)
    expenses = ExpenseService.get_pending_expenses(employee)
    data = [{'expense_id': e.id, 'amount': e.amount, 'category': e.category, 'description': e.description, 'submitted': e.created_date} for e in expenses]
    return Response(data)


# views.py
@api_view(['PUT'])
def edit_expense(request, user_id, expense_id):
    employee = ExpenseService.get_user_by_id(user_id)
    expense, _ = ExpenseService.get_pending_expense(employee, expense_id)
    if not expense:
        logger.warning(f"Edit failed: expense {expense_id} not editable for user {user_id}")
        return Response({'error': 'Expense not found or not editable.'}, status=status.HTTP_404_NOT_FOUND)

    amount = request.data.get('amount')
    description = request.data.get('description')
    category = request.data.get('category')

    try:
        amount = float(amount)
    except (ValueError, TypeError):
        return Response({'error': 'Invalid amount.'}, status=status.HTTP_400_BAD_REQUEST)
    if amount <= 0:
        return Response({'error': 'Amount must be greater than zero.'}, status=status.HTTP_400_BAD_REQUEST)

    ExpenseService.update_expense(expense, amount, description, category)
    logger.info(f"Expense {expense_id} edited by user {user_id}: amount={amount}, category={category}, description='{description}'")
    return Response({'message': 'Expense updated.'})

@api_view(['DELETE'])
def delete_expense(request, user_id, expense_id):
    employee = ExpenseService.get_user_by_id(user_id)
    deleted = ExpenseService.delete_pending_expense(employee, expense_id)
    if deleted:
        logger.info(f"Expense {expense_id} deleted by user {user_id}")
        return Response({'message': 'Expense deleted.'})
    logger.warning(f"Delete failed: expense {expense_id} not deletable for user {user_id}")
    return Response({'error': 'Expense not found or not deletable.'}, status=status.HTTP_404_NOT_FOUND)