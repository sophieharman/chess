package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.*;
import service.AlreadyTakenException;
import service.BadRequestException;
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
            gameDAO = new MySqlGameDAO();
            userDAO = new MySqlUserDAO();
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
    public void testCreateAuthFail() throws DataAccessException {

        // Attempt to Get Authentication of Nonexistent USer
        assertThrows(BadRequestException.class, () -> {
            authDAO.createAuth("NonExistentUser");});
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Create Authentication Data and Verify Existence
        String authToken = authDAO.createAuth("Bob");
        authDAO.getUser(authToken);

        // Delete User Authentication
        authDAO.deleteAuth(authToken);

        // Verify Authentication Data No Longer Exists
        assertThrows(BadRequestException.class, () -> {
            authDAO.getUser(authToken);});
    }

    @Test
    public void testDeleteAuthFail() {

        // Attempt to Delete Authentication Data with Invalid Token
        assertThrows(BadRequestException.class, () -> {
            authDAO.getUser("InvalidAuthToken");});
    }

    @Test
    public void testAuthGetUserSuccess() throws DataAccessException {

        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Create Auth
        String authToken = authDAO.createAuth("Bob");

        // Get User
        String username = authDAO.getUser(authToken);
        Assertions.assertEquals("Bob", username);
    }

    @Test
    public void testAuthGetUserFail() throws DataAccessException {
        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Create Auth
        authDAO.createAuth("Bob");

        // Attempt to Get User with Invalid AuthToken
        assertThrows(BadRequestException.class, () -> {
            authDAO.getUser("Invalid AuthToken");});
    }

    @Test
    public void testClearAuth() throws DataAccessException {

        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Create Auth
        String authToken = authDAO.createAuth("Bob");

        // Verify Existence of AuthData
        String username = authDAO.getUser(authToken);
        Assertions.assertNotNull(username);

        // Clear Auth Table
        authDAO.clear();

        // Verify AuthData No Longer Exists
        username = authDAO.getUser(authToken);
        Assertions.assertNull(username);
    }

    @Test
    public void testCreateUserSuccess() {
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
