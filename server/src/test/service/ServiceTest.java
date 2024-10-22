package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.LoginResult;
import server.LogoutResult;
import server.RegisterResult;

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
    public void testLogout()
    {
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
    public void testListGamesAll() {
        System.out.println("Implement");
    }

    @Test
    public void testListGamesNone() {
        System.out.println("Implement");
    }

}