import sqlite3

connection = sqlite3.connect('expense_manager.db')

with open('schema.sql', 'r') as f:
    schema = f.read()

connection.executescript(schema)
connection.commit()
connection.close()

print('database setup complete')