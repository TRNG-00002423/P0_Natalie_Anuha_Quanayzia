import sqlite3


def seed_database():
    connection = sqlite3.connect('expense_manager.db')
    cursor = connection.cursor()

    cursor.execute(
        "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
        ("testuser", "password234", "employee")

    )

    cursor.execute(
        "INSERT INTO users (username, password, role) VALUES(?, ?, ?)",
        ('testmanager', 'password678', 'manager')
    )

    connection.commit()
    connection.close()


seed_database()

