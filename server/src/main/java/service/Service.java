package service;

import java.util.*;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;

public class Service {

    private final UserDAO userDAO;

    public Service(UserDAO userDAO)
    {
        this.userDAO = userDAO;
    }

    public void getUser(String username){
        // Search for User in Database
        System.out.println("Implement!");
    }

    public void createUser(String username, String password, String email) {

        // Add User Data: Username, Password, Email
        userDAO.addUser(username, password, email);
    }

    public void getAuth(String authToken){
        System.out.println("Implement!");
    }

    public static String createAuth(String authData) {
        return UUID.randomUUID().toString();
    }
}
