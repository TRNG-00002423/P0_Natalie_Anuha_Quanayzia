package repository;

import com.project0.model.Expenses;
import com.project0.repository.ExpensesDAO;
import com.project0.repository.ExpensesDAOImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpensesDAOImplTest {

    ExpensesDAO expensesDAO = new ExpensesDAOImpl();

   
    @Test
    void getExpenseByID_returnsExpenseWhenExists() {
        Expenses expense = expensesDAO.getExpenseByID(2);
        assertNotNull(expense);
        assertEquals(2, expense.getId());
    }

   
    @Test
    void getExpenseByID_returnsNullWhenNotFound() {
        Expenses expense = expensesDAO.getExpenseByID(99999);
        assertNull(expense);
    }

}
