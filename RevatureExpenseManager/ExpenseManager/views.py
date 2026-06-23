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
            return redirect('submit_expense')
        else:
            return render(request, 'login.html', {'error': 'Invalid username or password.'})

    #login form
    return render(request, 'login.html')


def logout_view(request):
    request.session.flush()
    return redirect('login')
        
    

