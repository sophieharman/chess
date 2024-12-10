package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import model.*;
import websocket.messages.Notification;
import websockets.ErrorHandler;
import websockets.NotificationHandler;
import websockets.WebSocketFacade;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Client implements NotificationHandler {

    private WebSocketFacade ws;
    BoardDisplay display = new BoardDisplay();
    ChessBoard board = new ChessBoard();
    boolean listGamesCalled = false;
    GameIDs ids = new GameIDs();
    GameInfo gameInfo = new GameInfo();
    private String username;
    private String authToken;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private GameState gameState = GameState.OUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;


        try {
            new Repl(serverUrl, this).run();
        } catch (ResponseException e) {
            System.out.println("Error: ");
        }
    }



    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "joinGame" -> joinGame(params);
                case "observeGame" -> observeGame(params);
                case "redrawBoard" -> redrawBoard(params);
                case "leave" -> leave(params);
                case "makeMove" -> makeMove(params);
                case "resign" -> resign(params);
                case "showLegalMoves" -> showLegalMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | IOException | DeploymentException | URISyntaxException e) {
            return e.getMessage();
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
            UserData userInfo = new UserData(params[0], params[1], null);
            LoginResult result = server.login(userInfo, params[1]);
            state = State.SIGNEDIN;
            authToken = result.authToken();
            username = params[0];
            return String.format("You have successfully logged in as %s.", userInfo.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logout(String... params) throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must be logged in in order to log out.");
        }
        if (params.length == 0) {
            server.logout(authToken);
            state = State.SIGNEDOUT;
            return "You have successfully logged out";
        }
        throw new ResponseException(400, "Expected: logout <>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateGameResult game = server.createGame(params[0], authToken);

            gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.game());

            // Reset Board
            board.resetBoard();

            return String.format("New Game Created: %s", params[0]);
        }
        throw new ResponseException(400, "Expected: <GameName>");
    }

    public String listGames(String... params) throws ResponseException {
        int idCount = 0;
        if (params.length == 0) {
            ListGamesResult result = server.listGames(authToken);

            System.out.printf("%-10s %-10s %-10s %-10s%n", "GameID", "GameName", "White", "Black");
            for(GameData game: result.games()) {

                // Record Game IDs in Client
                idCount++;
                ids.addID(game.gameID(), idCount);

                // Record Game Information in Client
                gameInfo.addInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game);
                listGamesCalled = true;

                System.out.printf("%-10s %-10s %-10s %-10s%n", idCount, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
            return "";
        }
        throw new ResponseException(400, "Expected: <>");
    }

    public String joinGame(Object... params) throws ResponseException, IOException, DeploymentException, URISyntaxException {
        if (!listGamesCalled) {
            throw new ResponseException(500, "Error: Please list games before attempting to join a game");
        }
        if (params.length == 2) {

            // Verify 2nd Parameter is an Integer
            try {
                Integer.valueOf(params[1].toString());
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: GameID must be an Integer");
            }

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            // Unpack Parameters
            String playerColor = params[0].toString().toUpperCase();
            Integer gameID = Integer.valueOf(params[1].toString());

            // Grab Game Data
            String whiteUser = gameInfo.getWhiteUser(primaryID);
            String blackUser = gameInfo.getBlackUser(primaryID);
            GameData game = gameInfo.getGame(primaryID);

            // Verify Position is Open
            if (playerColor.equals("WHITE") && whiteUser != null || playerColor.equals("BLACK") && blackUser != null) {
                throw new ResponseException(400, "Error: Player Color Occupied");
            }

            server.joinGame(playerColor, authToken, primaryID);
            gameState = GameState.IN;

            ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, gameID);

            if (playerColor.equals("WHITE")) {
                display.main(game.game().getBoard(),"white");
            }
            if (playerColor.equals("BLACK")) {
                display.main(game.game().getBoard(), "black");
            }

            return "You have successfully joined Game " + gameID;
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String observeGame(String... params) throws ResponseException, DeploymentException, URISyntaxException, IOException {
        if (params.length == 1) {

            // Verify Parameter is an Integer
            try {
                Integer.valueOf(params[0].toString());
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: GameID must be an Integer");
            }

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[0].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }
            GameData game = gameInfo.getGame(primaryID);

            ws = new WebSocketFacade(serverUrl, this);
            ws.connect(authToken, primaryID);
            gameState = GameState.IN;

            display.main(game.game().getBoard(),"white");
            return "You have successfully joined the game as an observer";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String redrawBoard(String... params) throws ResponseException {
        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }
            GameData game = gameInfo.getGame(primaryID);

            // Unpack Parameters
            String playerColor = params[0].toString().toUpperCase();
            Integer gameID = Integer.valueOf(params[1].toString());

            // Redraw Board
            display.main(game.game().getBoard(), playerColor);
            return "";
        }
        throw new ResponseException(400, "Expected: <PlayerColor> <GameID>");
    }

    public String leave(Object... params) throws DeploymentException, URISyntaxException, IOException, ResponseException {
        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            String user = params[0].toString();
;

            ws = new WebSocketFacade(serverUrl, this);
            ws.leave(user, primaryID);

            gameState = GameState.OUT;
            return "You've left the game.";
        }
        throw new ResponseException(400, "Expected: <Username> <GameID>");
    }

    public String makeMove(String... params) throws ResponseException, DeploymentException, URISyntaxException, IOException {

        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            ws = new WebSocketFacade(serverUrl, this);
            ws.makeMove(authToken, primaryID);
            return "";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String resign(String... params) throws DeploymentException, URISyntaxException, IOException, ResponseException {

        if (params.length == 2) {

            // Verify Game ID
            Integer primaryID = ids.getPrimaryGameID(Integer.valueOf(params[1].toString()));
            if (primaryID == null) {
                throw new ResponseException(404, "Error: Game ID not found");
            }

            ws = new WebSocketFacade(serverUrl, this);
            ws.leave(authToken, primaryID);

            gameState = GameState.OUT;
            return "Game Over. You’ve forfeited the game.";
        }
        throw new ResponseException(400, "Expected: <GameID>");
    }

    public String showLegalMoves(String... params) {
        System.out.println("Implement");
        return "";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        else {
            if (gameState == GameState.IN) {
                return """
                - help
                - redrawBoard <PlayerColor> <GameID>
                - highlightLegalMoves
                - makeMove
                - leave <Username> <GameID>
                - resign
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

    @Override
    public void notify(String notification) {
        JsonObject jsonObject = JsonParser.parseString(notification).getAsJsonObject();
        String msg = jsonObject.get("message").getAsString();
        System.out.println(msg);
    }
}
