package repository;

import com.project0.model.Users;
import com.project0.repository.UserDAO;
import com.project0.repository.UserDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserDAOImplTest {

    UserDAO userDAO= new UserDAOImpl();

    @Test
    void getUserByUsername_returnUserWhenExists(){
        Users user=userDAO.getUserByUsername("Elon Page");
        assertNotNull(user);;
        assertEquals("Elon Page",user.getUsername());
        assertEquals("EMPLOYEE", user.getRole());

    }
}
