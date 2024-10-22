package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceTest {

    private AuthDAO authDAO = new MemoryAuthDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private final Service service = new Service(authDAO, userDAO);

    @Test
    public void testRegisterUsernameAvailable() {
        AuthData authData = new AuthData("1237", "Bob");
        UserData userInfo = new UserData("Bob","pword", "bobwork@email.com");

        RegisterResult actual = service.register(authData, userInfo);
        Assertions.assertEquals(userInfo.username(), actual.username());
        Assertions.assertNotNull(actual.authToken());
    }

    @Test
    public void testRegisterUsernameTaken() {
        // Add User into System
        AuthData authData = new AuthData("??", "Susan");
        UserData userInfo = new UserData("Susan","pword", "bobwork@email.com");
        service.register(authData, userInfo);

        // Register with Taken Username
        assertThrows(ServiceException.class, () -> {
            service.register(authData, userInfo);});
    }

}