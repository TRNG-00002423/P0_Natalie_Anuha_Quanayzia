from django.shortcuts import render, redirect
from django.http import HttpResponse
from django.template import loader
from django.utils import timezone
from .models import User, Expense, Approval


def index(request):
    template = loader.get_template('expensemanager.html')
    return HttpResponse(template.render())


def submit_expense(request):
    
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)

    if request.method == 'POST':
        amount = request.POST.get('amount')
        description = request.POST.get('description')

        # make sure its valid
        try:
            amount = float(amount)
        except (ValueError, TypeError):
            return render(request, 'submit.html', {'error': 'Invalid amount. Please enter a number.'})
        if amount <= 0:
            return render(request, 'submit.html', {'error': 'Amount must be greater than zero.'})

        

        expense = Expense.objects.create(
            user_id=employee,
            amount=amount,
            description=description,
            created_date=timezone.now(),)
        

        Approval.objects.create(expense_id=expense, status='pending')

        return render(request, 'submit.html', {'message': 'Expense submitted and pending review.'})

    # empty form
    return render(request, 'submit.html')


def login_view(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')

        if not username or not password:
            return render(request, 'login.html', {'error': 'Please enter both username and password.'})

        user = User.objects.filter(username=username, password=password).first()

        if user:
            request.session['user_id'] = user.id
            return redirect('menu')
        else:
            return render(request, 'login.html', {'error': 'Invalid username or password.'})

    #login form
    return render(request, 'login.html')


def logout_view(request):
    request.session.flush()
    return redirect('login')


def menu(request):
    
    if not request.session.get('user_id'):
        return redirect('login')
    return render(request, 'menu.html')


def view_expenses(request):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)

    expenses = Expense.objects.filter(user_id=employee)
    rows = []
    for e in expenses:
        approval = Approval.objects.filter(expense_id=e).first()
        status = approval.status if approval else 'pending'
        rows.append({'expense': e, 'status': status})

    return render(request, 'view_expenses.html', {'rows': rows})

def view_history(request):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)
    expenses = Expense.objects.filter(user_id=employee)
    rows = []
    total_approved = 0
    total_denied = 0
    for e in expenses:
        approval = Approval.objects.filter(expense_id=e).first()
        if approval and approval.status == 'approved':
            rows.append({'expense': e, 'status': 'approved'})
            total_approved += e.amount
        elif approval and approval.status == 'denied':
            rows.append({'expense': e, 'status': 'denied'})
            total_denied += e.amount

    return render(request, 'view_history.html', {
        'rows': rows,
        'total_approved': total_approved,
        'total_denied': total_denied,
    })


def edit_pending(request):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)

    all_expenses = Expense.objects.filter(user_id=employee)
    expenses = []
    for e in all_expenses:
        approval = Approval.objects.filter(expense_id=e).first()
        if approval and approval.status == 'pending':
            expenses.append(e)

    return render(request, 'edit_pending.html', {'expenses': expenses})


def delete_expense(request, expense_id):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)

    if request.method == 'POST':
        expense = Expense.objects.filter(id=expense_id, user_id=employee).first()
        if expense:
            approval = Approval.objects.filter(expense_id=expense).first()
     
            if approval and approval.status == 'pending':
                expense.delete() 

    return redirect('edit_pending')


def edit_expense(request, expense_id):
    user_id = request.session.get('user_id')
    if not user_id:
        return redirect('login')
    employee = User.objects.get(id=user_id)


    expense = Expense.objects.filter(id=expense_id, user_id=employee).first()
    if not expense:
        return redirect('edit_pending')

    
    approval = Approval.objects.filter(expense_id=expense).first()
    if not approval or approval.status != 'pending':
        return redirect('edit_pending')

    if request.method == 'POST':
        amount = request.POST.get('amount')
        description = request.POST.get('description')

        try:
            amount = float(amount)
        except (ValueError, TypeError):
            return render(request, 'edit_expense.html', {'expense': expense, 'error': 'Invalid amount. Please enter a number.'})
        if amount <= 0:
            return render(request, 'edit_expense.html', {'expense': expense, 'error': 'Amount must be greater than zero.'})


        expense.amount = amount
        expense.description = description
        expense.save()
        return redirect('edit_pending')

    
    return render(request, 'edit_expense.html', {'expense': expense})



        
    

