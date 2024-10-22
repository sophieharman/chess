package service;

import java.util.*;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.*;

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
        Collection<String> userData = userDAO.getUser(userInfo.username());
        if(userData != null)
        {
            throw new ServiceException(403, "Error: Username Taken");
        }

        // Create New User
        userDAO.createUser(userInfo.username(), userInfo.password(), userInfo.email());

        // Create Authentication Token
        authDAO.createAuth(userInfo.username()); // CHANGE TO JUST USERNAME

        // Retrieve Authentication Token
        String authToken = authDAO.getAuth(userInfo.username());

        return new RegisterResult(userInfo.username(), authToken);
    }

//    public LoginResult login(AuthData authData, UserData userInfo) {
//        return new LoginResult();
//    }

//    public LogoutResult logout() {
//        return new LogoutResult();
//    }

//    public ListGamesResult listGames() {
//        return new ListGamesResult();
//    }

//    public CreateGameResult createGame() {
//        return new CreateGameResult();
//    }

//    public JoinGameResult joinGame() {
//        return new JoinGameResult();
//    }

}
