package service;

import java.util.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import model.UserData;
import server.*;

public class Service {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;


    public Service(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {

        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(UserData userInfo) throws ServiceException {

        // Verify the Provided Username Does Not Exist
        UserData userData = userDAO.getUser(userInfo.username());
        if(userData != null) {
            throw new UnauthorizedException();
        }

        // Create New User
        userDAO.createUser(userInfo.username(), userInfo.password(), userInfo.email());

        // Create and Retrieve Authentication Token
        String authToken = authDAO.createAuth(userInfo.username());

        // Return Username and Authentication Token
        return new RegisterResult(userInfo.username(), authToken);
    }

    public LoginResult login(String username, String password) throws ServiceException {

        // Verify Username Exists
        UserData userInfo = userDAO.getUser(username);
        if(userInfo == null) {
            throw new ServiceException(404, "Error: Username Not Found");
        }

        // Verify Password is Correct
        if(!Objects.equals(userInfo.password(), password)) {
            throw new UnauthorizedException();
        }

        // Create and Retrieve Authentication Token
        String authToken = authDAO.createAuth(username);

        // Return Username and Authentication Token
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) throws ServiceException {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new UnauthorizedException();
        }

        // Delete Authentication Token
        authDAO.deleteAuth(authToken);

        return new LogoutResult();
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new UnauthorizedException();
        }

        // List of All Games
        List<GameData> games = new ArrayList<>(gameDAO.listGames().values());
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ServiceException {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new UnauthorizedException();
        }

        // Create New Game
        Integer gameID = gameDAO.createGame(gameName);
        return new CreateGameResult(gameID, null, null, gameName);
    }

    public JoinGameResult joinGame(String playerColor, String authToken, Integer gameID) throws ServiceException {

        // Verify Authentication
        String validAuth = authDAO.getAuth(authToken);
        if (!Objects.equals(authToken, validAuth)) {
            throw new UnauthorizedException();
        }

        // Join Game
        String username = authDAO.getUser(authToken);
        gameDAO.joinGame(playerColor, username, authToken, gameID);

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
