package service;

import java.util.*;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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

    public RegisterResult register(UserData userInfo) throws ServiceException, DataAccessException {

        // Verify Username, Password, and Email are not Null
        if(userInfo.username() == null || userInfo.password() == null|| userInfo.email() == null) {
            throw new BadRequestException();
        }

        // Verify the Provided Username Does Not Exist
        UserData userData = userDAO.getUser(userInfo.username());
        if(userData != null) {
            throw new AlreadyTakenException();
        }

        // Create New User
        userDAO.createUser(userInfo.username(), userInfo.password(), userInfo.email());

        // Create and Retrieve Authentication Token
        String authToken = authDAO.createAuth(userInfo.username());

        // Return Username and Authentication Token
        return new RegisterResult(userInfo.username(), authToken);
    }

    public LoginResult login(String username, String password) throws ServiceException, DataAccessException {

        // Verify Username Exists
        UserData userInfo = userDAO.getUser(username);
        if(userInfo == null) {
            throw new ServiceException(401, "Error: Unauthorized Access");
        }

        // Verify Password is Correct
        if (!verifyUser(username, password))  {
            throw new UnauthorizedException();
        }

        // Create and Retrieve Authentication Token
        String authToken = authDAO.createAuth(username);

        // Return Username and Authentication Token
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(String authToken) throws ServiceException, DataAccessException {

        // Verify Authentication
        String username = authDAO.getUser(authToken);
        if (username == null) {
            throw new UnauthorizedException();
        }

        // Delete Authentication Token
        authDAO.deleteAuth(authToken);

        return new LogoutResult();
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {

        // Verify Authentication
        String username = authDAO.getUser(authToken);
        if (username == null) {
            throw new UnauthorizedException();
        }

        // List All Games
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ServiceException, DataAccessException {

        // Verify Authentication
        String username = authDAO.getUser(authToken);
        if (username == null) {
            throw new UnauthorizedException();
        }

        // Create New Game
        Integer gameID = gameDAO.createGame(gameName);
        return new CreateGameResult(gameID, null, null, gameName);
    }

    public JoinGameResult joinGame(String playerColor, String authToken, Integer gameID) throws ServiceException, DataAccessException {

        // Verify Authentication
        String username = authDAO.getUser(authToken);
        if (username == null) {
            throw new UnauthorizedException();
        }

        // Verify Player Color and GameID or Null
        if(playerColor == null || gameID == null) {
            throw new BadRequestException();
        }

        // Verify Game ID
        GameData gameExists = gameDAO.getGame(gameID);
        if (gameExists == null) {
            throw new BadRequestException();
        }

        // Grab Old Game Information
        GameData gameInfo = gameDAO.getGame(gameID);
        String whiteUsername = gameInfo.whiteUsername();
        String blackUsername = gameInfo.blackUsername();
        String gameName = gameInfo.gameName();
        ChessGame game = gameInfo.game();

        // Update White/Black Usernames
        if(Objects.equals(playerColor, "WHITE")) {
            if(whiteUsername != null) {
                throw new AlreadyTakenException();
            }
            whiteUsername = username;

        }
        else if(Objects.equals(playerColor, "BLACK")) {
            if (blackUsername != null) {
                throw new AlreadyTakenException();
            }
            blackUsername = username;
        }
        else {
            throw new BadRequestException();
        }

        // Remove Game
        gameDAO.removeGame(gameID);

        // Add Game with Updated Information
        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        gameDAO.addGame(updatedGame);

        return new JoinGameResult();
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // Read Hashed Password from Database
        UserData userInfo = userDAO.getUser(username);
        String hashedPassword = userInfo.password();
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public ClearResult clear() throws DataAccessException {

        // Delete Authentication Data
        authDAO.clear();

        // Delete User Data
        userDAO.clear();

        // Delete Game Data
        gameDAO.clear();

        return new ClearResult();
    }

}
