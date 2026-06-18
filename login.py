import sqlite3


def login(username, password):
    conn = sqlite3.connect('expense_manager.db')
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    cursor.execute(
        "SELECT * FROM users WHERE username = ? AND password = ?", 
        (username, password)
    )

    user = cursor.fetchone()  
    conn.close()

    return user

'''result = login("testuser", "password234")
print(result['username'], result['role'])
result2 = login("testwrong", "password5678")
if result2:
    print(result2['username'], result2['role'])
else:
    print("Login failed")'''

    

