package client;

import exception.ResponseException;
import model.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import ui.Client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


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
     public void setUp(){
        // Clear Database
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

}
