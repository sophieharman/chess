package ui;

import exception.ResponseException;
import model.ListGamesResult;
import model.LoginResult;
import model.RegisterResult;
import model.UserData;

import java.util.Arrays;

public class Client {

    private String authToken;
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
            return ex.getMessage();
        }

    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            UserData userInfo = new UserData(params[0], params[1], params[2]);
            RegisterResult result = server.register(userInfo);
            authToken = result.authToken();
            return String.format("You have successfully registered as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "You must be logged out in order to log in.");
        }
        if (params.length == 2) {
            state = State.SIGNEDIN;
            UserData userInfo = new UserData(params[0], params[1], null);
            LoginResult result = server.login(userInfo, params[1]);
            authToken = result.authToken();
            return String.format("You have successfully logged in as %s.", userInfo.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logout() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be logged in in order to log out.");
        }
        server.logout(authToken);
        state = State.SIGNEDOUT;
        return "You have successfully logged out";
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            server.createGame(params[0], authToken);
            return String.format("New Game Created: %s", params[0]);
        }
        throw new ResponseException(400, "Expected: <GameName>");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0) {
            ListGamesResult result = server.listGames(authToken);
            return result.toString();
        }
        throw new ResponseException(400, "Expected: <>");
    }

    public String joinGame(Object... params) throws ResponseException {
        if (params.length == 2) {

            String playerColor;
            Integer gameID;
            if (params[0] instanceof String && params[1] instanceof Integer) {
                playerColor = params[0].toString();
                gameID = (Integer) params[1];
            }
            else
            {
                throw new ResponseException(400, "Player color must consist of characters, and the gameID must be an integer");
            }
            server.joinGame(playerColor, authToken, gameID);
            return "";
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length == 1) {
            // Account for params[0] not being valid Integer!!!!
            server.joinGame(null, authToken, Integer.valueOf(params[0]));
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - listGames
                - joinGame <PlayerColor> <GameID>
                - observeGame <GameID>
                - createGame <GameName>
                - logout
                """;
    }

}
