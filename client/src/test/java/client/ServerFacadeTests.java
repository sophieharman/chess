package client;

import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

     @BeforeEach
     public void setUp() throws ResponseException {
        // Clear ServerFacade
         serverFacade.clear();
     }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        // User Information
        UserData userInfo = new UserData("username", "password", "my@email.com");

        // Register User
        RegisterResult result = serverFacade.register(userInfo);
        Assertions.assertEquals(result.username(), "username");
    }

    @Test
    public void registerFail() throws ResponseException {
        // User Information
        UserData userInfo = new UserData("username",null, "my@email.com");

        // Register User with No Password
        assertThrows(ResponseException.class, () -> {
            serverFacade.register(userInfo);});
    }

    @Test
    public void loginSuccess() throws ResponseException {
        // Register a User
        UserData userInfo = new UserData("username", "password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login
        LoginResult result = serverFacade.login(userInfo, registerResult.authToken());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals(result.username(), "username");
    }

    @Test
    public void loginFail() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username",null, "my@email.com");

        // Login Non-existent User
        assertThrows(ResponseException.class, () -> {
            serverFacade.login(userInfo, "InvalidAuth");});
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login User
        LoginResult result = serverFacade.login(userInfo, registerResult.authToken());

        // Logout User
        serverFacade.logout(result.authToken());

        // Attempt to Log Out again
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout(result.authToken());});
    }

    @Test
    public void logoutFail() {
        // Logout Non-Existent User
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout("InvalidAuth");});
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login User
        serverFacade.login(userInfo, registerResult.authToken());

        // Create Game
        CreateGameResult result = serverFacade.createGame("Game1", registerResult.authToken());

        // Assertions
        Assertions.assertNotNull(result.gameID());
        Assertions.assertNull(result.whiteUsername());
        Assertions.assertNull(result.blackUsername());
        Assertions.assertEquals("Game1", result.gameName());
    }

    @Test
    public void createGameFail() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login User
        serverFacade.login(userInfo, registerResult.authToken());

        // Create Game with No Name
        assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(null, registerResult.authToken());});
    }


    @Test
    public void listGamesSuccess() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login User
        serverFacade.login(userInfo, registerResult.authToken());

        // Create Game
        serverFacade.createGame("Game1", registerResult.authToken());

        // List Games
        ListGamesResult result = serverFacade.listGames(registerResult.authToken());

        // Verify Game is Listed
        Assertions.assertEquals(result.games().size(), 1);
    }

    @Test
    public void listGamesFail() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login User
        serverFacade.login(userInfo, registerResult.authToken());

        // List Games (None)
        ListGamesResult result = serverFacade.listGames(registerResult.authToken());
        Assertions.assertEquals(result.games().size(), 0);
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login
        serverFacade.login(userInfo, registerResult.authToken());

        // Create Game
        CreateGameResult game = serverFacade.createGame("Game1", registerResult.authToken());

        // Join Game
        serverFacade.joinGame("WHITE", registerResult.authToken(), game.gameID());

        // List Game
        ListGamesResult result = serverFacade.listGames(registerResult.authToken());

        // Assertions
        Assertions.assertEquals(result.games().size(), 1);
    }

    @Test
    public void joinGameFail() throws ResponseException {
        // Join Game with Invalid AuthData/GameID
        assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame("WHITE", "InvalidAuth", 12345);});
    }

    @Test
    public void clear() throws ResponseException {
        // Register User
        UserData userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult = serverFacade.register(userInfo);

        // Login
        serverFacade.login(userInfo, registerResult.authToken());

        // Create Game
        serverFacade.createGame("Game1", registerResult.authToken());

        // Assert List of Games is Not Null
        ListGamesResult result = serverFacade.listGames(registerResult.authToken());
        Assertions.assertEquals(result.games().size(), 1);

        // Clear
        serverFacade.clear();

        // Register User
        userInfo = new UserData("username","password", "my@email.com");
        RegisterResult registerResult1 = serverFacade.register(userInfo);

        // Assert List of Games is Null
        result = serverFacade.listGames(registerResult1.authToken());
        Assertions.assertEquals(result.games().size(), 0);
    }


}
