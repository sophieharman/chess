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
    public static void beforeAll(){
        try{
            authDAO = new MySqlAuthDAO();
            gameDAO = new MemoryGameDAO(); // CHANGE TO SQL
            userDAO = new MemoryUserDAO(); // CHANGE TO SQL
            service = new Service(authDAO, gameDAO, userDAO);
        } catch(DataAccessException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // ADD TRY CATCH BLOCKS TO ALL TEST METHODS

    @Test
    public void testRegisterUsernameAvailable() {

        try{
            // Register User
            Assertions.assertDoesNotThrow(()->userDAO.createUser("Bob","pword", "bobwork@email.com"));

        } catch (DataAccessException ex){
            Assertions.assertNull(ex);
        }
    }

    @Test
    public void testRegisterUsernameTaken() throws ServiceException {

        // Add User into System
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        service.register(userInfo);

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.register(userInfo);});
    }

    @Test
    public void testLoginCorrectInfo() throws ServiceException {

        // Add User into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Login User
        LoginResult loginResult = service.login("Lucy", "Fish123");
        Assertions.assertEquals(userInfo.username(), loginResult.username());
        Assertions.assertNotNull(loginResult.authToken());
    }

    @Test
    public void testLoginUsernameNotFound() throws ServiceException {

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.login("Loser", "ThePassword");});
    }

    @Test
    public void testLoginIncorrectPassword() throws ServiceException {

        // Add User into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.login("Lucy", "Crab123");});
    }

    @Test
    public void testLogoutSuccessful() throws ServiceException {

        // Add User Into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Login User and Get Authentication
        LoginResult loginResult = service.login(userInfo.username(), userInfo.password());

        // Logout User
        service.logout(loginResult.authToken());

        // Assert AuthToken is Deleted
        String username = authDAO.getUser(loginResult.authToken());
        Assertions.assertNull(username);
    }

    @Test
    public void testLogoutIncorrectAuth() throws ServiceException {

        // Add User Into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Login User and Get Authentication
        LoginResult loginResult = service.login(userInfo.username(), userInfo.password());

        // Attempt Logout
        assertThrows(ServiceException.class, () -> {
            service.logout("InvalidAuthToken");});
    }

    @Test
    public void testListGamesAll() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create Game
        service.createGame("Game1NoUpdates", authToken);

        // Create Another Game
        CreateGameResult game2Result = service.createGame("Game2WithUpdates", authToken);

        // Join Game
        service.joinGame("WHITE", authToken, game2Result.gameID());

        // Assert Games Listed
        ListGamesResult listGamesResult = service.listGames(authToken);
        Assertions.assertFalse(listGamesResult.games().isEmpty());
    }

    @Test
    public void testListGamesNone() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // List Games
        ListGamesResult listGamesResult = service.listGames(authToken);
        Assertions.assertTrue(listGamesResult.games().isEmpty());
    }

    @Test
    public void testListGamesInvalidAuth() throws ServiceException {

        // Attempt to List Games with Invalid Authentication Token
        assertThrows(ServiceException.class, () -> {
            service.listGames("InvalidAuth");});

    }

    @Test
    public void testCreateGame() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create Game
        CreateGameResult createGameResult = service.createGame("Game1", authToken);

        // Assertions
        Assertions.assertNotNull(createGameResult.gameID());
        Assertions.assertNull(createGameResult.whiteUsername());
        Assertions.assertNull(createGameResult.blackUsername());
        Assertions.assertEquals("Game1", createGameResult.gameName());
    }

    @Test
    public void testCreateGameSameName() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create Game
        CreateGameResult createGameResult1 = service.createGame("Game1", authToken);

        // Create Another Game with Same Name
        CreateGameResult createGameResult2 = service.createGame("Game1", authToken);

        // Assertions
        Assertions.assertEquals(createGameResult1.gameName(), createGameResult2.gameName());
        Assertions.assertNotEquals(createGameResult1.gameID(), createGameResult2.gameID());
    }

    @Test
    public void testCreateGameInvalidAuth() throws ServiceException {

        // Assert Invalid Authentication
        assertThrows(ServiceException.class, () -> {
            service.createGame("Game1", "invalidAuth");});
    }

    @Test
    public void testJoinGameSuccess() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create New Game
        CreateGameResult createGameResult = service.createGame("Game1", authToken);

        // Join Game
        JoinGameResult joinGameResult = service.joinGame("WHITE", authToken, createGameResult.gameID());

        // Grab Game Information
        GameData game = gameDAO.getGame(createGameResult.gameID());

        //Assertions
        Assertions.assertEquals("Susan", game.whiteUsername());
    }

    @Test
    public void testJoinGameWhitePlayerTaken() throws ServiceException {

        // Add Users into System and get Authentication
        UserData userInfo1 = new UserData("User1","pword", "user1@email.com");
        UserData userInfo2 = new UserData("User2","pword", "user1@email.com");
        RegisterResult registerResult1 = service.register(userInfo1);
        RegisterResult registerResult2 = service.register(userInfo2);
        String authTokenUser1 = registerResult1.authToken();
        String authTokenUser2 = registerResult2.authToken();

        // Create New Game
        CreateGameResult createGameResult = service.createGame("Game1", authTokenUser1);

        // User1 Join Game as White
        JoinGameResult joinGameResult1 = service.joinGame("WHITE", authTokenUser1, createGameResult.gameID());

        // User2 Attempt to Join Game as White
        assertThrows(AlreadyTakenException.class, () -> {
            service.joinGame("WHITE", authTokenUser2, createGameResult.gameID());});
    }

    @Test
    public void testJoinGameInvalidAuth() throws ServiceException {

        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create New Game
        CreateGameResult createGameResult = service.createGame("Game1", authToken);

        // Assert Invalid Authentication
        assertThrows(ServiceException.class, () -> {
            service.joinGame("WHITE", "InvalidAuth", createGameResult.gameID());});
    }

    @Test
    public void testClear() throws ServiceException {

        // Add User into System
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create Game
        CreateGameResult createGameResult = service.createGame("Game1", authToken);

        // Assert Information Can Be Found
        userInfo = userDAO.getUser("Susan");
        String user = authDAO.getUser(authToken);
        GameData games = gameDAO.getGame(createGameResult.gameID());
        Assertions.assertNotNull(userInfo);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(games);

        // Clear Information
        ClearResult clearResult = service.clear();

        // Assert there is No Information
        userInfo = userDAO.getUser("Susan");
        user = authDAO.getUser(authToken);
        games = gameDAO.getGame(createGameResult.gameID());
        Assertions.assertNull(userInfo);
        Assertions.assertNull(user);
        Assertions.assertNull(games);

    }
}
