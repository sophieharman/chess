package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceTest {

    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private final Service service = new Service(authDAO, gameDAO, userDAO);

    @Test
    public void testRegisterUsernameAvailable() {
        UserData userInfo = new UserData("Bob","pword", "bobwork@email.com");

        RegisterResult actual = service.register(userInfo);
        Assertions.assertEquals(userInfo.username(), actual.username());
        Assertions.assertNotNull(actual.authToken());
    }

    @Test
    public void testRegisterUsernameTaken() {
        // Add User into System
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        service.register(userInfo);

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.register(userInfo);});
    }

    @Test
    public void testLoginCorrectInfo() {
        // Add User into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Login User
        LoginResult actual = service.login("Lucy", "Fish123");
        Assertions.assertEquals(userInfo.username(), actual.username());
        Assertions.assertNotNull(actual.authToken());
    }

    @Test
    public void testLoginUsernameNotFound() {
        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.login("Loser", "ThePassword");});
    }

    @Test
    public void testLoginIncorrectPassword() {
        // Add User into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.login("Lucy", "Crab123");});
    }

    @Test
    public void testLogoutSuccessful() {

        // Add User into System
        UserData userInfo = new UserData("Lucy","Fish123", "Lucywork@email.com");
        service.register(userInfo);

        // Login User and get Authentication
        LoginResult loginResult = service.login(userInfo.username(), userInfo.password());

        // Logout User
        LogoutResult actual = service.logout(loginResult.authToken());

        // Will Memory remember Logouts?
        // FINISH LOGIN TEST!!!
    }

    @Test
    public void testLogoutFailed() {
        System.out.println("Implement");
    }

    @Test
    public void testListGamesAll() {
        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // Create Game
        service.createGame("Game1NoUpdates", authToken);

        // Create Another Game
        service.createGame("Game2WithUpdates", authToken);

        // Join Game

        ListGamesResult listGamesResult = service.listGames(authToken);
        Assertions.assertTrue(!listGamesResult.games().isEmpty());
    }

    @Test
    public void testListGamesNone() {
        // Add User into System and get Authentication
        UserData userInfo = new UserData("Susan","pword", "susanwork@email.com");
        RegisterResult registerResult = service.register(userInfo);
        String authToken = registerResult.authToken();

        // List Games
        ListGamesResult listGamesResult = service.listGames(authToken);
        Assertions.assertTrue(listGamesResult.games().isEmpty());
    }

    @Test
    public void testCreateGame() {
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
    public void testCreateGameSameName() {
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
    public void testCreateGameFail() {
        System.out.println("Implement");
    }

    @Test
    public void testJoinGameSuccess() {
        System.out.println("Implement");
    }

    @Test
    public void testJoinGameFail() {
        System.out.println("Implement");
    }

    @Test
    public void testClear() {
        System.out.println("Implement");
    }

    @Test
    public void testClearNothing() {
        System.out.println("Implement");
    }

}