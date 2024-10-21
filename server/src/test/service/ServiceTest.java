package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceTest {

    @BeforeEach
    public void setUp() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        Service service = new Service(authDAO, userDAO);
    }

//    @Test
//    public void testRegisterUsernameAvailable() {
//        AuthData authData = new AuthData("1237", "Bob");
//        UserData userInfo = new UserData("Bob","pword", "bobwork@email.com")
//
//        RegisterResult actual = service.register(authData, userInfo);
//        RegisterResult expected = new RegisterResult("Bob", "1237");
//        Assertions.assertEquals(expected, actual);
//    }

    public void testRegisterUsernameTaken() {
        System.out.println("Implement");
    }

}