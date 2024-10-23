package service;

import java.util.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
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
        UserData userData = userDAO.getUser(userInfo.username());

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

        UserData userInfo = userDAO.getUser(username);

        // Verify Username Exists
        if(userInfo == null) {
            throw new ServiceException(404, "Error: Username Not Found");
        }

        // Verify Password is Correct
        if(Objects.equals(userInfo.password(), password)) {
            throw new ServiceException(401, "Error: Incorrect Password");
        }

        // Create Authentication Token
        authDAO.createAuth(username);

        // Get Authentication Token
        String authToken = authDAO.getAuth(username);

        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new ServiceException(401, "Error: Unauthorized Access");
        }

        // Delete Authentication Token
        authDAO.deleteAuth(authToken);

        return new LogoutResult();
    }

    public ListGamesResult listGames(String authToken) {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new ServiceException(401, "Error: Unauthorized Access");
        }

        HashMap<String, GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String gameName, String authToken) {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new ServiceException(401, "Error: Unauthorized Access");
        }

        // Create New Game
        Integer gameID = gameDAO.createGame(gameName);

        return new CreateGameResult(gameID, null, null, gameName);
    }

    public JoinGameResult joinGame(String playerColor, String authToken, Integer gameID) {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new ServiceException(401, "Error: Unauthorized Access");
        }

        String username = "?";

        // Join Game
        gameDAO.joinGame();


        return new JoinGameResult();
    }

    public ClearResult clear() {

        // Delete Authentication Data
        authDAO.clear();

        // Delete User Data
        userDAO.clear();

        // Delete Game Data
        gameDAO.clear();

        return new ClearResult();
    }

}
