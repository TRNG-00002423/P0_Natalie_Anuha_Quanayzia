# Revature Expense Manager - Development Checklist
## Database & Schema
- [x] Create SQLite schema file
- [x] Define users table
- [x] Define expenses table
- [x] Define approvals table
- [ ] Java and Python apps both use shared SQLite DB
---

## Java Manager App
### Entities
- [x] UserEntity created
- [x] ExpenseEntity created
- [x] ApprovalEntity created

### DAO Layer
- [x] UserDao - interface
- [ ] UserDao - implementation
- [x] ExpenseDao- interface
- [ ] ExpenseDao - implementation
- [x] ApprovalDao - interface
- [ ] ApprovalDao - implementation

---

## JUnit Testing
### DAO Tests
- [x] UserDao 
- [ ] ExpenseDao
- [ ] ApprovalDao 

---

## Service Layer
- [ ] UserService implementation
- [ ] ExpenseService implementation
- [ ] ApprovalService implementation
- [ ] Enforce role-based access rules
- [ ] Validate expense lifecycle rules

---

## CLI Applications

### Java Manager CLI
- [ ] Login system for managers
- [ ] View pending expenses
- [ ] Approve expenses
- [ ] Deny expenses
- [ ] Add approval comments
- [ ] Generate reports (by user/category/date)

### Python Employee CLI
- [ ] Employee login system
- [ ] Submit expense
- [ ] View expense status
- [ ] Edit pending expense
- [ ] Delete pending expense
- [ ] View expense history


