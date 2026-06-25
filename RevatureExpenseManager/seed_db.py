import sqlite3, os

here = os.path.dirname(os.path.abspath(__file__))
c = sqlite3.connect(os.path.join(here, "db.sqlite3"))
cur = c.cursor()

cur.execute("DELETE FROM ExpenseManager_approval")
cur.execute("DELETE FROM ExpenseManager_expense")
cur.execute("DELETE FROM ExpenseManager_user")


cur.execute("INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (1,'employee1','pass','employee')")
cur.execute("INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (2,'manager1','pass','manager')")
cur.execute("INSERT INTO ExpenseManager_user (id, username, password, role) VALUES (3,'employee2','pass','employee')")


expenses = [
    (1, 75.0,  'Team lunch',      '2026-06-23', 1, 'MEALS'),
    (2, 120.0, 'Office supplies', '2026-06-20', 1, 'OFFICE'),
    (3, 200.0, 'Flight',          '2026-06-23', 3, 'TRAVEL'),
    (4, 45.0,  'Taxi',            '2026-06-20', 3, 'TRAVEL'),
]
for eid, amount, desc, date, uid, cat in expenses:
    cur.execute("INSERT INTO ExpenseManager_expense (id, amount, description, created_date, user_id_id, category) VALUES (?,?,?,?,?,?)",
                (eid, amount, desc, date, uid, cat))
    cur.execute("INSERT INTO ExpenseManager_approval (status, approved_date, expense_id_id, reviewer_id, comment) VALUES ('pending', NULL, ?, NULL, '')",
                (eid,))

c.commit()
c.close()
print("Seeded: 3 users, 4 expenses (employee1 & employee2), all pending")
