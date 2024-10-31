package dataaccess;

import model.AuthData;
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

import java.sql.SQLOutput;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataAccessTest {

    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    @BeforeAll
    public static void beforeAll() throws DataAccessException{
            authDAO = new MySqlAuthDAO();
            gameDAO = new MySqlGameDAO(); // CHANGE TO SQL
            userDAO = new MySqlUserDAO(); // CHANGE TO SQL
    }

    @Test
    public void testCreateAuthSuccess() throws DataAccessException {

        // Create Authentication Data
        String authToken = authDAO.createAuth("Bob");

        // Get User by AuthToken
        String username = authDAO.getUser(authToken);
        Assertions.assertEquals("Bob", username);
    }

    @Test
    public void testCreateAuthFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testDeleteAuthSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testDeleteAuthFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testAuthGetUserSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testAuthGetUserFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testClearAuth() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testCreateUserSuccess() {

        // Create User
//        userDAO.createUser("Bob","pword", "bobwork@email.com");

        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testCreateUserFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testUserGetUserSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testUserGetUserFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testClearUser() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testCreateGameSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testCreateGameFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testListGamesSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testListGamesFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testAddGameSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testAddGameFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testGetGameSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testGetGameFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testRemoveGameSuccess() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testRemoveGameFail() {
        throw new UnsupportedOperationException("Implement!");
    }

    @Test
    public void testClearGame() {
        throw new UnsupportedOperationException("Implement!");
    }

}
