from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .service import ExpenseService, AuthenticationService
from .exceptions import AuthenticationError


@api_view(['POST'])
def login(request):
    try:
        user = AuthenticationService.login(
            request.data.get('username'),
            request.data.get('password')
        )
    except AuthenticationError as e:
        return Response({'error': str(e)}, status=status.HTTP_401_UNAUTHORIZED)
    return Response({'user_id': user.id})


@api_view(['POST'])
def submit_expense(request):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    amount = request.data.get('amount')
    description = request.data.get('description')

    try:
        amount = float(amount)
    except (ValueError, TypeError):
        return Response({'error': 'Invalid amount.'}, status=status.HTTP_400_BAD_REQUEST)
    if amount <= 0:
        return Response({'error': 'Amount must be greater than zero.'}, status=status.HTTP_400_BAD_REQUEST)

    employee = ExpenseService.get_user_by_id(user_id)
    ExpenseService.submit_expense(employee, amount, description)
    return Response({'message': 'Expense submitted and pending review.'}, status=status.HTTP_201_CREATED)


@api_view(['GET'])
def view_expenses(request):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    employee = ExpenseService.get_user_by_id(user_id)
    rows = ExpenseService.get_expenses_with_status(employee)
    data = [{'expense_id': r['expense'].id, 'amount': r['expense'].amount, 'description': r['expense'].description, 'status': r['status']} for r in rows]
    return Response(data)


@api_view(['GET'])
def view_history(request):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    employee = ExpenseService.get_user_by_id(user_id)
    rows, total_approved, total_denied = ExpenseService.get_expense_history(employee)
    data = [{'expense_id': r['expense'].id, 'amount': r['expense'].amount, 'description': r['expense'].description, 'status': r['status']} for r in rows]
    return Response({'expenses': data, 'total_approved': total_approved, 'total_denied': total_denied})


@api_view(['GET'])
def get_pending_expenses(request):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    employee = ExpenseService.get_user_by_id(user_id)
    expenses = ExpenseService.get_pending_expenses(employee)
    data = [{'expense_id': e.id, 'amount': e.amount, 'description': e.description} for e in expenses]
    return Response(data)


@api_view(['PUT'])
def edit_expense(request, expense_id):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    employee = ExpenseService.get_user_by_id(user_id)
    expense, _ = ExpenseService.get_pending_expense(employee, expense_id)
    if not expense:
        return Response({'error': 'Expense not found or not editable.'}, status=status.HTTP_404_NOT_FOUND)

    amount = request.data.get('amount')
    description = request.data.get('description')

    try:
        amount = float(amount)
    except (ValueError, TypeError):
        return Response({'error': 'Invalid amount.'}, status=status.HTTP_400_BAD_REQUEST)
    if amount <= 0:
        return Response({'error': 'Amount must be greater than zero.'}, status=status.HTTP_400_BAD_REQUEST)

    ExpenseService.update_expense(expense, amount, description)
    return Response({'message': 'Expense updated.'})


@api_view(['DELETE'])
def delete_expense(request, expense_id):
    user_id = request.data.get('user_id')
    if not user_id:
        return Response({'error': 'Unauthorized.'}, status=status.HTTP_401_UNAUTHORIZED)

    employee = ExpenseService.get_user_by_id(user_id)
    deleted = ExpenseService.delete_pending_expense(employee, expense_id)
    if deleted:
        return Response({'message': 'Expense deleted.'})
    return Response({'error': 'Expense not found or not deletable.'}, status=status.HTTP_404_NOT_FOUND)