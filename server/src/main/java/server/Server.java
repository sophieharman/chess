package server;

import com.google.gson.Gson;
import service.Service;
import spark.*;

public class Server {

    private final Gson serializer = new Gson();
    private final Service service = new Service();

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

    public String register(Request req, Response res) {

        // Verify Username Does Not Exist
        //      Call getUser()
        service.getUser();
        //

        // Create New User
        //      Call CreateUser()

        return "Implement";

    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
