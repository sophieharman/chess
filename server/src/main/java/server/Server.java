package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import service.Service;
import spark.*;
import java.util.*;

public class Server {

    private final Gson serializer = new Gson();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final Service service = new Service(authDAO, userDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.post("/user", this::register);
//        Spark.post("/session", this::login);
//        Spark.delete("/session", this::logout);
//        Spark.get("/game", this::getGame);
//        Spark.post("/game", this::createGame);
//        Spark.put("/game", this::joinGame);
        Spark.delete("/db", (req, response) -> "{}");

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object register(Request req, Response res) {

        UserData userInfo = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = new Gson().fromJson(req.body(), AuthData.class);

        RegisterResult result = service.register(authData, userInfo);

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
