package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;
import service.Service;
import service.ServiceException;
import spark.utils.Assert;

import java.sql.SQLOutput;
import java.util.Collection;

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

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
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
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null);});
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {

        // Create Authentication Data and Verify Existence
        String authToken = authDAO.createAuth("Bob");
        authDAO.getUser(authToken);

        // Delete User Authentication
        authDAO.deleteAuth(authToken);

        // Verify Authentication Data No Longer Exists
        String username = authDAO.getUser(authToken);
        Assertions.assertNull(username);
    }

    @Test
    public void testDeleteAuthFail() throws DataAccessException {

        // Attempt to Delete Authentication Data with Invalid Token
        authDAO.deleteAuth("InvalidAuthToken");

        String username = authDAO.getUser("InvalidAuthToken");
        Assertions.assertNull(username);
    }

    @Test
    public void testAuthGetUserSuccess() throws DataAccessException {

        // Create User
        userDAO.createUser("Susan", "password", "mywork@gmail.com");

        // Get User
        String username = authDAO.getUser(authDAO.createAuth("Susan"));
        Assertions.assertEquals("Susan", username);
    }

    @Test
    public void testAuthGetUserFail() throws DataAccessException {

        // Create Auth
        authDAO.createAuth("Bob");

        // Attempt to Get User with Invalid AuthToken
        String username = authDAO.getUser("Invalid AuthToken");
        Assertions.assertNull(username);
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
    public void testCreateUserSuccess() throws DataAccessException {

        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Create Authentication Data
        String authToken = authDAO.createAuth("Bob");

        // Get User
        String username = authDAO.getUser(authToken);
        Assertions.assertEquals("Bob", username);
    }

    @Test
    public void testCreateUserFail() {

        // Attempt to Create User with No Password
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser("Bob", null, "work@gmail.com");});
    }

    @Test
    public void testUserGetUserSuccess() throws DataAccessException {

        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Get User Information
        UserData userInfo = userDAO.getUser("Bob");

        // Assertions
        Assertions.assertEquals("Bob", userInfo.username());
        // Test Password?
        Assertions.assertEquals("work@gmail.com", userInfo.email());
    }

    @Test
    public void testUserGetUserFail() throws DataAccessException {

        UserData userInfo = userDAO.getUser("NonExistentUser");
        Assertions.assertNull(userInfo);
    }

    @Test
    public void testClearUser() throws DataAccessException {

        // Create User
        userDAO.createUser("Bob", "password", "work@gmail.com");

        // Verify User Information Exists
        UserData userInfo = userDAO.getUser("Bob");
        Assertions.assertNotNull(userInfo);

        // Clear User Table
        userDAO.clear();

        // Verify AuthData No Longer Exists
        userInfo = userDAO.getUser("Bob");
        Assertions.assertNull(userInfo);
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {

        // Create Game
        Integer gameID = gameDAO.createGame("GameName");

        // Get Game Data
        GameData gameInfo = gameDAO.getGame(gameDAO.createGame("GameName"));

        // Assertions
        Assertions.assertNull(gameInfo.game());
    }

    @Test
    public void testCreateGameFail() throws DataAccessException {

        // Attempt to Create Game with No Name
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);});
    }

    @Test
    public void testListGamesSuccess() throws DataAccessException {

        // Create Game
        gameDAO.createGame("Game1");

        // List Games
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games);
    }

    @Test
    public void testListGamesNone() throws DataAccessException {

        // List Games
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void testAddGameSuccess() throws DataAccessException {

        // Add Game
        GameData newGameInfo = new GameData(12345, "Bob", null, "Game1", null);
        gameDAO.addGame(newGameInfo);

        // List Games
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games);
    }

    @Test
    public void testAddGameFail() throws DataAccessException {

        // Update Game with Invalid GameID
        GameData newGameInfo = new GameData(123, "Bob", null, null, null);

        assertThrows(DataAccessException.class, () -> {
            gameDAO.addGame(newGameInfo);});
    }

    @Test
    public void testGetGameSuccess() throws DataAccessException {

        // Create Game
        Integer gameID = gameDAO.createGame("Game1");

        // Get Game
        GameData gameInfo = gameDAO.getGame(gameID);

        // Assertions
        Assertions.assertEquals(gameID, gameInfo.gameID());
        Assertions.assertNull(gameInfo.whiteUsername());
        Assertions.assertNull(gameInfo.blackUsername());
        Assertions.assertEquals("Game1", gameInfo.gameName());
        Assertions.assertNull(gameInfo.game());
    }

    @Test
    public void testGetGameFail() {

        // Get Game with Invalid GameID
        assertThrows(DataAccessException.class, () -> {
            gameDAO.getGame(null);});
    }

    @Test
    public void testRemoveGameSuccess() throws DataAccessException {

        // Create Game
        Integer gameID = gameDAO.createGame("Game1");

        // List of Games Before Removing Game
        Collection<GameData> gamesBefore = gameDAO.listGames();

        // Remove Game
        gameDAO.removeGame(gameID);

        // List of Games After Removing Game
        Collection<GameData> gamesAfter = gameDAO.listGames();

        // Assertions
        Assertions.assertEquals(gamesBefore.size(), 1);
        Assertions.assertEquals(gamesAfter.size(), 0);
    }

    @Test
    public void testRemoveGameFail() throws DataAccessException {

        // List of Games Before Removing Game
        Collection<GameData> gamesBefore = gameDAO.listGames();

        // Remove Nonexistent Game
        gameDAO.getGame(456);

        // List of Games After Removing Game
        Collection<GameData> gamesAfter = gameDAO.listGames();
        Assertions.assertEquals(gamesBefore.size(), gamesAfter.size());

    }

    @Test
    public void testClearGame() throws DataAccessException {

        // Create Game
        Integer gameID = gameDAO.createGame("Game1");

        // Verify Game Information Exists
        GameData gameInfo = gameDAO.getGame(gameID);
        Assertions.assertNotNull(gameInfo);

        // Clear Games
        gameDAO.clear();

        // Verify Game Information No Longer Exists
        gameInfo = gameDAO.getGame(gameID);
        Assertions.assertNull(gameInfo);
    }

}
