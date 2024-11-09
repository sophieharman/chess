package ui;

import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

public class Client {

    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public static void main(String[] args) throws ResponseException {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "joinGame" -> joinGame(params);
                case "observeGame" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return "";
        }

    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData userInfo = new UserData(params[0], params[1], params[2]);
            server.register(userInfo);
            return String.format("You have successfully registered as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
//        if (state == State.SIGNEDIN) {
//            throw new ResponseException(400, "You must be logged out in order to log in.");
//        }
//        if (params.length == 2) {
//            state = State.SIGNEDIN;
//            UserData userInfo = "????????";
//            server.login(userInfo, params[1]);
            return String.format("You have successfully logged in as %s.", "?");
//        }
//        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be logged in in order to log out.");
        }
        String authToken = "??????????";
        server.logout(authToken);
        return "You have successfully logged out";
    }

    public String createGame(String... params){
        return "";
    }

    public String listGames(String... params){
        return "";
    }

    public String joinGame(String... params){
        return "";
    }

    public String observeGame(String... params){
        return "";
    }

    public String help() {
        System.out.println("Implement!");
        return "";
    }

}
