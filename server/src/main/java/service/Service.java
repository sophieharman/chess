package service;

import java.util.*;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.RegisterResult;

public class Service {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;


    public Service(AuthDAO authDAO, UserDAO userDAO)
    {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(AuthData authData, UserData userInfo)
    {
        // Verify the Provided Username Does Not Exist
        userDAO.getUser(userInfo.username());

        // Create New User
        userDAO.createUser(userInfo.username(), userInfo.password(), userInfo.email());

        // Create Authentication Token
        String authToken = authDAO.createAuth(authData);

        return new RegisterResult(userInfo.username(), authToken);
    }

}
