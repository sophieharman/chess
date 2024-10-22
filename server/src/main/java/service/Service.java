package service;

import java.util.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.*;

public class Service {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;


    public Service(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO)
    {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(UserData userInfo)
    {
        Collection<String> userData = userDAO.getUser(userInfo.username());

        // Verify the Provided Username Does Not Exist
        if(userData != null) {
            throw new ServiceException(403, "Error: Username Taken");
        }

        // Create New User
        userDAO.createUser(userInfo.username(), userInfo.password(), userInfo.email());

        // Create Authentication Token
        authDAO.createAuth(userInfo.username());

        // Retrieve Authentication Token
        String authToken = authDAO.getAuth(userInfo.username());

        return new RegisterResult(userInfo.username(), authToken);
    }

    public LoginResult login(String username, String password) {

        Collection<String> userInfo = userDAO.getUser(username);

        // Verify Username Exists
        if(userInfo == null) {
            throw new ServiceException(404, "Error: Username Not Found");
        }

        // Verify Password is Correct
        if(!userInfo.contains(password)) {
            throw new ServiceException(401, "Error: Incorrect Password");
        }

        // Create Authentication Token
        authDAO.createAuth(username);

        // Get Authentication Token
        String authToken = authDAO.getAuth(username);

        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) {

        // Delete Authentication Token
        authDAO.deleteAuth(authToken);

        return new LogoutResult();
    }

    public ListGamesResult listGames() {

        HashMap<String, Collection<String>> games = gameDAO.getGames();
        return new ListGamesResult(games);
    }

//    public CreateGameResult createGame() {
//        return new CreateGameResult();
//    }

//    public JoinGameResult joinGame() {
//        return new JoinGameResult();
//    }

    public void clear() {
        System.out.println("Implement");
    }

}
