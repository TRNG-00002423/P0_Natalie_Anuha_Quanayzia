from ExpenseManager.exceptions import AuthenticationError

from ..models import User


def login(username, password):
    try:
         user = User.objects.get(username=username)
    except User.DoesNotExist:
        raise AuthenticationError("Username or password is incorrect")
    
    if user.password != password:
        raise AuthenticationError("Username or password is incorrect")

    return user

    


    

