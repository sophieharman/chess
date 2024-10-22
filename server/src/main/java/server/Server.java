package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.Service;
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
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
        Spark.delete("/db", (req, response) -> "{}");

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object register(Request req, Response res) {

        UserData userInfo = new Gson().fromJson(req.body(), UserData.class);

        RegisterResult result = service.register(userInfo);

        res.status();
        String body = new Gson().toJson(result);
        res.body(body);
        return body;
    }

    public Object login(Request req, Response res) {

        UserData userInfo = new Gson().fromJson(req.body(), UserData.class);

        LoginResult result = service.login(userInfo.username(), userInfo.password());

        res.status();
        String body = new Gson().toJson(result);
        res.body(body);
        return body;
    }

    public Object logout(Request req, Response res) {

        AuthData authData = new Gson().fromJson(req.body(), AuthData.class);

        LogoutResult result = service.logout(authData.authToken());

        res.status();
        String body = new Gson().toJson(result);
        res.body(body);
        return body;
    }

    public Object listGames(Request req, Response res) {

        ListGamesResult result = service.listGames();

        res.status();
        String body = new Gson().toJson(result);
        res.body(body);
        return body;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
