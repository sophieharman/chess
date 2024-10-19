package server;

import com.google.gson.Gson;
import service.Service;
import spark.*;
import java.util.*;

public class Server {

    private final Gson serializer = new Gson();
    private final Service service = new Service(); //LOOK AT THE THREAD IN SLACK

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.post("/user", this::register);
        Spark.delete("/db", (req, response) -> "{}");

        Spark.awaitInitialization();
        return Spark.port();
    }

    public Object register(Request req, Response res) {

        String username = req.params(":username");
        String password = req.params(":password");
        String email = req.params(":email");

        // Verify the Provided Username Does Not Exist
        service.getUser(username);

        // Create New User
        service.createUser(username, password, email);

        // Create Authentication Token
        String authData = "?????????";
        String authToken = service.createAuth(authData);

        return new Gson().toJson(Map.of("username", username, "authToken", authToken));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
