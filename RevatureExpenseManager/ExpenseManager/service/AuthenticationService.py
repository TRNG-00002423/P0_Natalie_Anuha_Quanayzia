from ..exceptions import AuthenticationError
from ..models import User


import bcrypt
from ExpenseManager.exceptions.AuthenticationError import AuthenticationError
from ..models import User


def login(username, password):
    try:
        user = User.objects.get(username=username)
    except User.DoesNotExist:
        raise AuthenticationError("Username or password is incorrect")

    if not bcrypt.checkpw(password.encode('utf-8'), user.password.encode('utf-8')):
        raise AuthenticationError("Username or password is incorrect")

    return user
    


    

