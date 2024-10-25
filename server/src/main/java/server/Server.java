package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import model.UserData;
import service.Service;
import service.ServiceException;
import spark.*;
import java.util.*;

public class Server {

    private final Gson serializer = new Gson();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final Service service = new Service(authDAO, gameDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.exception(ServiceException.class, this::serviceExceptionHandler);
        Spark.exception(Exception.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void exceptionHandler(Exception ex, Request req, Response res) {
        res.status(500);
        res.body("{'message': '%s'}".formatted("Error: " + ex.getMessage()));
    }

    public void serviceExceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body("{'message': '%s'}".formatted("Error: " + ex.getMessage()));
    }

    public Object register(Request req, Response res) throws ServiceException {

        UserData userInfo = new Gson().fromJson(req.body(), UserData.class);

        RegisterResult result = service.register(userInfo);

        return new Gson().toJson(result);
    }

    public Object login(Request req, Response res) throws ServiceException {

        UserData userInfo = new Gson().fromJson(req.body(), UserData.class);

        LoginResult result = service.login(userInfo.username(), userInfo.password());

        return new Gson().toJson(result);
    }

    public Object logout(Request req, Response res) throws ServiceException {

        String authToken = req.headers("Authorization");

        LogoutResult result = service.logout(authToken);

        return new Gson().toJson(result);
    }

    public Object listGames(Request req, Response res) throws ServiceException {

        String authToken = req.headers("Authorization");

        ListGamesResult result = service.listGames(authToken);

        return new Gson().toJson(result);
    }

    public Object createGame(Request req, Response res) throws ServiceException {

        GameData gameData = new Gson().fromJson(req.body(), GameData.class);

        String authToken = req.headers("Authorization");

        CreateGameResult result = service.createGame(gameData.gameName(), authToken);

        return new Gson().toJson(result);
    }

    public Object joinGame(Request req, Response res) throws ServiceException {

        GameData gameData = new Gson().fromJson(req.body(), GameData.class);
        UserData userData = new Gson().fromJson(req.body(), UserData.class);

        String authToken = req.headers("Authorization");

        // Determine Player Color
        String playerColor;
        if(Objects.equals(gameData.whiteUsername(), userData.username())) {
            playerColor = "WHITE";
        }
        else {
            playerColor = "BLACK";
        }

        // Join Game
        JoinGameResult result = service.joinGame(playerColor, authToken, gameData.gameID());

        return new Gson().toJson(result);
    }

    public Object clear(Request req, Response res) {

        ClearResult result = service.clear();

        return new Gson().toJson(result);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
