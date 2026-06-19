
PRAGMA foreign_keys = ON;

create table users (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE,
    password TEXT,
    role TEXT
);

create table expenses (
    id INTEGER PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    amount REAL,
    description TEXT,
    date TEXT
);

create table approvals (
    id INTEGER PRIMARY KEY,
    expense_id INTEGER REFERENCES expenses(id) UNIQUE,
    status TEXT DEFAULT 'pending',
    reviewer INTEGER,
    comment TEXT,
    review_date TEXT
);