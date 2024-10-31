package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.*;
import service.AlreadyTakenException;
import service.Service;
import service.ServiceException;
import spark.utils.Assert;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataAccessTest {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static Service service;


    @BeforeAll
    public static void beforeAll() throws DataAccessException{
            authDAO = new MySqlAuthDAO();
            gameDAO = new MemoryGameDAO(); // CHANGE TO SQL
            userDAO = new MemoryUserDAO(); // CHANGE TO SQL
    }

    @Test
    public void testRegisterUsernameAvailable() throws DataAccessException {

        // Register User
        // Call getUser
        userDAO.createUser("Bob","pword", "bobwork@email.com");
    }

}
